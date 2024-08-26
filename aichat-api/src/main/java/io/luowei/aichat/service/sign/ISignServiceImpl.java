package io.luowei.aichat.service.sign;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import io.luowei.aichat.common.redis.IRedisService;
import io.luowei.aichat.dao.repository.ISignRepository;
import io.luowei.aichat.dao.repository.IUserRepository;
import io.luowei.aichat.model.sign.ContinuousSignRecordEntity;
import io.luowei.aichat.model.sign.MonthSignEntity;
import io.luowei.aichat.model.sign.SignInformationEntity;
import io.luowei.aichat.model.sign.SignTypeVO;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBitSet;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Date;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ISignServiceImpl implements ISignService{

    @Resource
    private IRedisService redisService;

    @Resource
    private IUserRepository userRepository;

    @Resource
    private ISignRepository signRepository;

    @Override
    public SignTypeVO sign(String openId, String dateStr) {
        Date date = getDate(dateStr);
        int day = DateUtil.dayOfMonth(date) - 1; // bitmap从0开始

        String key = buildRedisKey(openId,date);
        String monthKey = buildMonthKey(date);

        // 操作Bitmap
        RBitSet bitSet = redisService.getBitSet(key);

        // 获取某月的总天数
        int dayOfMonth = DateUtil.lengthOfMonth(DateUtil.month(date) + 1
                ,DateUtil.isLeapYear(DateUtil.year(date)));

        long unsigned = bitSet.getUnsigned(dayOfMonth, 0);

        if (unsigned == 0) {
            if (day != 0) {
                log.info("构建缓存");
                MonthSignEntity monthSignEntity = signRepository.getMonthSign(openId,monthKey);
                if (monthSignEntity != null) {
                    bitSet.setUnsigned(dayOfMonth,0,monthSignEntity.getSignRecord());
                }
            }
        }

        // 判断该日期是否已经签到
        boolean isSign = bitSet.get(day);

        if (isSign) {
            return SignTypeVO.REPEAT_SIGN;
        }

        boolean sign = bitSet.set(day);
        // 设置过期时间一个月
        bitSet.expireIfNotSet(Duration.ofMillis(1000L * 60 * 60 * 24 * dayOfMonth));

        long newUnsigned = bitSet.getUnsigned(dayOfMonth, 0);

        // 签到记录保存，将redis中的数据转换成long存储到数据库
        saveSignRecord(openId,date,newUnsigned);

        // 更新签到总数
        updateTotalSignCount(openId);

        // 更新积分
        updateIntegral(openId);

        // 更新连续签到数
        updateContinuousSignCount(openId,date);

        if (!sign) {
            return SignTypeVO.SIGN;
        }

        return SignTypeVO.SIGN_ERROR;
    }

    private void saveSignRecord(String openId, Date date, long unsigned) {
        String month = buildMonthKey(date);

        MonthSignEntity monthSignEntity = new MonthSignEntity();
        monthSignEntity.setSignMonth(month);
        monthSignEntity.setOpenid(openId);
        monthSignEntity.setSignRecord(unsigned);

        signRepository.updateMonthSign(monthSignEntity);
    }

    @Override
    public List<String> getSignRecords(String openId, String dateStr) {
        Date date = getDate(dateStr);

        String key = buildRedisKey(openId,date);

        String monthKey = buildMonthKey(date);

        RBitSet bitSet = redisService.getBitSet(key);

        int dayOfMonth = DateUtil.lengthOfMonth(DateUtil.month(date) + 1
                ,DateUtil.isLeapYear(DateUtil.year(date)));

        long unsigned = bitSet.getUnsigned(dayOfMonth, 0);


        List<String> records = new ArrayList<>();

        if (unsigned == 0) {
            // 为0则代表无任何数据，从数据库查询，在添加进缓存，时间一个月
            MonthSignEntity monthSignEntity = signRepository.getMonthSign(openId,monthKey);
            if (monthSignEntity != null) {
                bitSet.setUnsigned(dayOfMonth,0,monthSignEntity.getSignRecord());
                // 设置过期时间一个月
                bitSet.expireIfNotSet(Duration.ofMillis(1000L * 60 * 60 * 24 * dayOfMonth));
                unsigned = monthSignEntity.getSignRecord();
            } else {
                return records;
            }
        }

        for (int i = dayOfMonth; i > 0 ; i--) {
            if (unsigned >> 1 << 1 != unsigned) {
                LocalDateTime localDateTime = LocalDateTimeUtil.of(date.getTime()).withDayOfMonth(i);
                records.add(DateUtil.format(localDateTime,"yyyy-MM-dd"));
            }
            unsigned >>= 1;
        }
        return records;
    }

    @Override
    public SignInformationEntity getSignInformation(String openid) {
        SignInformationEntity signInformationEntity = new SignInformationEntity();

        Integer totalSignCount = userRepository.getTotalSignCount(openid);
        signInformationEntity.setTotalSignCount(totalSignCount);

        Date date = getDate("");

        int day = DateUtil.dayOfMonth(date) - 1; // bitmap从0开始

        String key = buildRedisKey(openid,date);

        RBitSet bitSet = redisService.getBitSet(key);

        if (bitSet.get(day)) {
            ContinuousSignRecordEntity signRecord = signRepository.queryContinuousSignRecordByEndTime(openid, getDate(""));
            if (signRecord != null) {
                signInformationEntity.setIsSign(true);
                signInformationEntity.setContinuousSignCount(signRecord.getSignLength());
                return signInformationEntity;
            }
        }

        ContinuousSignRecordEntity notSignRecord = signRepository.queryContinuousSignRecordByEndTime(openid, Date.valueOf(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000).toString()));

        if (notSignRecord != null) {
            signInformationEntity.setIsSign(false);
            signInformationEntity.setContinuousSignCount(notSignRecord.getSignLength());
            return signInformationEntity;
        }

        signInformationEntity.setIsSign(false);
        signInformationEntity.setContinuousSignCount(0);
        return signInformationEntity;
    }

    private void updateContinuousSignCount(String openId, Date date) {
        // 这里其实还是会比较时间，所以还需要转化一下，将时间置0
        Date currentDate = Date.valueOf(new Date(System.currentTimeMillis()).toString());

        if (currentDate.compareTo(date) == 0) {
            // 是当日签到，直接加1即可
            signRepository.todaySignUpdateContinuousSignRecord(openId);
        } else if (currentDate.compareTo(date) > 0) {
            handleMakeupSign(openId,date);
        }
    }

    private void handleMakeupSign(String openId, Date date) {
        // 查询开始时间-1的连续签到记录
        ContinuousSignRecordEntity prevRecord = signRepository.queryContinuousSignRecordByStartTime(openId, Date.valueOf(new Date(date.getTime() + 24 * 60 * 60 * 1000).toString()));

        // 查询结束时间+1的连续签到记录
        ContinuousSignRecordEntity nextRecord = signRepository.queryContinuousSignRecordByEndTime(openId, Date.valueOf(new Date(date.getTime() - 24 * 60 * 60 * 1000).toString()));

        if (prevRecord == null && nextRecord == null) {
            // 不存在开始时间-1的连续段，也不存在结束时间+1的连续段
            ContinuousSignRecordEntity continuousSignRecordEntity = new ContinuousSignRecordEntity();
            continuousSignRecordEntity.setSignLength(1);
            continuousSignRecordEntity.setOpenid(openId);
            continuousSignRecordEntity.setStartTime(date);
            continuousSignRecordEntity.setEndTime(date);
            signRepository.insert(continuousSignRecordEntity);
        } else if (prevRecord != null && nextRecord == null) {
            // 存在开始时间-1的连续段，不存在结束时间+1的连续段
            handlePrevRecord(date,prevRecord);
        } else if (prevRecord == null && nextRecord != null) {
            // 不存在开始时间-1的连续段，存在结束时间+1的连续段
            handleNextRecord(date,nextRecord);
        } else  {
            // 存在开始时间-1的连续段，存在结束时间+1的连续段
            mergeContinuousSignRecords(prevRecord, nextRecord);
        }
    }

    private void handlePrevRecord(Date date,ContinuousSignRecordEntity prevRecord) {
        prevRecord.setStartTime(date);
        prevRecord.setSignLength(prevRecord.getSignLength() + 1);
        signRepository.updateContinuousSignRecord(prevRecord);
    }

    private void handleNextRecord(Date date, ContinuousSignRecordEntity nextRecord) {
        nextRecord.setEndTime(date);
        nextRecord.setSignLength(nextRecord.getSignLength() + 1);
        signRepository.updateContinuousSignRecord(nextRecord);
    }

    private void mergeContinuousSignRecords(ContinuousSignRecordEntity prevRecord, ContinuousSignRecordEntity nextRecord) {
        ContinuousSignRecordEntity continuousSignRecordEntity = new ContinuousSignRecordEntity();
        continuousSignRecordEntity.setStartTime(nextRecord.getStartTime());
        continuousSignRecordEntity.setEndTime(prevRecord.getEndTime());
        continuousSignRecordEntity.setOpenid(prevRecord.getOpenid());
        continuousSignRecordEntity.setSignLength(prevRecord.getSignLength() + nextRecord.getSignLength() +1);

        List<Long> ids = new ArrayList<>();
        ids.add(prevRecord.getId());
        ids.add(nextRecord.getId());

        signRepository.insert(continuousSignRecordEntity);
        signRepository.batchDelete(ids);
    }

    private void updateTotalSignCount(String openId) {
        // 总签到数主要签到一次就加一，不需要去进行统计
        userRepository.updateTotalSignCount(openId);
    }

    private void updateIntegral(String openId) {
        userRepository.updateIntegral(openId);
    }

    private Date getDate(String dateStr) {
        return StrUtil.isBlank(dateStr) ? Date.valueOf(new Date(System.currentTimeMillis()).toString()) : new Date(DateUtil.parseDate(dateStr).getTime());
    }

    public String buildRedisKey(String openid,Date date) {
        return StrUtil.format("sign:{}:{}:{}",openid
                ,DateUtil.format(date,"yyyy")
                ,DateUtil.format(date,"MM"));
    }

    public String buildMonthKey(Date date) {
        return StrUtil.format("{}-{}"
                ,DateUtil.format(date,"yyyy")
                ,DateUtil.format(date,"MM"));
    }

}
