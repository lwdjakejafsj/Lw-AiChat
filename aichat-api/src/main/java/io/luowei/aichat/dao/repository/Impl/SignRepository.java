package io.luowei.aichat.dao.repository.Impl;

import io.luowei.aichat.dao.mapper.ContinuousSignRecordMapper;
import io.luowei.aichat.dao.mapper.MonthSignMapper;
import io.luowei.aichat.dao.po.ContinuousSignRecord;
import io.luowei.aichat.dao.po.MonthSign;
import io.luowei.aichat.dao.repository.ISignRepository;
import io.luowei.aichat.model.sign.ContinuousSignRecordEntity;
import io.luowei.aichat.model.sign.MonthSignEntity;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.Date;
import java.util.List;

@Repository
public class SignRepository implements ISignRepository {

    @Resource
    private ContinuousSignRecordMapper continuousSignRecordMapper;

    @Resource
    private MonthSignMapper monthSignMapper;

    @Override
    public void todaySignUpdateContinuousSignRecord(String openId) {
        Date date = Date.valueOf(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000).toString());
        Date currenDate = Date.valueOf(new Date(System.currentTimeMillis()).toString());
        // 先判断是否有结束时间为当天-1的连续段
        ContinuousSignRecord signRecord = continuousSignRecordMapper.queryContinuousSignRecordByEndTime(openId, date);

        if (signRecord != null) {
            // 有则直接增加该连续段长度并且更新
            signRecord.setEndTime(currenDate);
            signRecord.setSignLength(signRecord.getSignLength() + 1);
            continuousSignRecordMapper.update(signRecord);
        } else {
            // 没有则创建一个新连续段
            ContinuousSignRecord continuousSignRecord = new ContinuousSignRecord();
            continuousSignRecord.setOpenid(openId);
            continuousSignRecord.setStartTime(new Date(System.currentTimeMillis()));
            continuousSignRecord.setEndTime(currenDate);
            continuousSignRecord.setSignLength(1);
            continuousSignRecordMapper.insert(continuousSignRecord);
        }
    }

    @Override
    public void updateContinuousSignRecord(ContinuousSignRecordEntity continuousSignRecordEntity) {
        ContinuousSignRecord continuousSignRecord = new ContinuousSignRecord();
        continuousSignRecord.setId(continuousSignRecordEntity.getId());
        continuousSignRecord.setOpenid(continuousSignRecordEntity.getOpenid());
        continuousSignRecord.setStartTime(continuousSignRecordEntity.getStartTime());
        continuousSignRecord.setSignLength(continuousSignRecordEntity.getSignLength());
        continuousSignRecord.setEndTime(continuousSignRecordEntity.getEndTime());

        continuousSignRecordMapper.update(continuousSignRecord);
    }

    @Override
    public ContinuousSignRecordEntity queryContinuousSignRecordByStartTime(String openId, Date date) {
        ContinuousSignRecord continuousSignRecord = continuousSignRecordMapper.queryContinuousSignRecordByStartTime(openId,date);

        if (continuousSignRecord == null) {
            return null;
        }

        ContinuousSignRecordEntity continuousSignRecordEntity = new ContinuousSignRecordEntity();
        continuousSignRecordEntity.setId(continuousSignRecord.getId());
        continuousSignRecordEntity.setOpenid(continuousSignRecord.getOpenid());
        continuousSignRecordEntity.setStartTime(continuousSignRecord.getStartTime());
        continuousSignRecordEntity.setEndTime(continuousSignRecord.getEndTime());
        continuousSignRecordEntity.setSignLength(continuousSignRecord.getSignLength());

        return continuousSignRecordEntity;
    }

    @Override
    public ContinuousSignRecordEntity queryContinuousSignRecordByEndTime(String openId, Date date) {
        ContinuousSignRecord continuousSignRecord = continuousSignRecordMapper.queryContinuousSignRecordByEndTime(openId,date);

        if (continuousSignRecord == null) {
            return null;
        }

        ContinuousSignRecordEntity continuousSignRecordEntity = new ContinuousSignRecordEntity();
        continuousSignRecordEntity.setId(continuousSignRecord.getId());
        continuousSignRecordEntity.setOpenid(continuousSignRecord.getOpenid());
        continuousSignRecordEntity.setStartTime(continuousSignRecord.getStartTime());
        continuousSignRecordEntity.setEndTime(continuousSignRecord.getEndTime());
        continuousSignRecordEntity.setSignLength(continuousSignRecord.getSignLength());

        return continuousSignRecordEntity;
    }

    @Override
    public void updateMonthSign(MonthSignEntity monthSignEntity) {
        MonthSign monthSign = new MonthSign();
        monthSign.setOpenid(monthSignEntity.getOpenid());
        monthSign.setSignMonth(monthSignEntity.getSignMonth());
        monthSign.setSignRecord(monthSignEntity.getSignRecord());

        // 没有有该月签到记录，插入
        MonthSign record = monthSignMapper.selectByOpenidAndMonth(monthSignEntity.getOpenid(), monthSignEntity.getSignMonth());

        if (record == null) {
            monthSignMapper.insert(monthSign);
            return;
        }

        monthSignMapper.updateByPrimaryKey(monthSign);
    }

    @Override
    public MonthSignEntity getMonthSign(String openid, String month) {

        MonthSign monthSign = monthSignMapper.selectByOpenidAndMonth(openid, month);

        if (monthSign == null) {
            return null;
        }

        MonthSignEntity monthSignEntity = new MonthSignEntity();
        monthSignEntity.setId(monthSign.getId());
        monthSignEntity.setSignMonth(monthSign.getSignMonth());
        monthSignEntity.setSignRecord(monthSign.getSignRecord());
        monthSignEntity.setOpenid(monthSign.getOpenid());

        return monthSignEntity;
    }

    @Override
    public void insert(ContinuousSignRecordEntity continuousSignRecordEntity) {
        ContinuousSignRecord continuousSignRecord = new ContinuousSignRecord();
        continuousSignRecord.setOpenid(continuousSignRecordEntity.getOpenid());
        continuousSignRecord.setStartTime(continuousSignRecordEntity.getStartTime());
        continuousSignRecord.setSignLength(continuousSignRecordEntity.getSignLength());
        continuousSignRecord.setEndTime(continuousSignRecordEntity.getEndTime());

        continuousSignRecordMapper.insert(continuousSignRecord);
    }

    @Override
    public void batchDelete(List<Long> ids) {
        continuousSignRecordMapper.batchDelete(ids);
    }
}
