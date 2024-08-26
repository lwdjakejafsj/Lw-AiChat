package io.luowei.aichat.service.wechat;

import com.google.common.cache.Cache;
import io.luowei.aichat.common.exception.ChatGptException;
import io.luowei.aichat.dao.repository.IWeChatRepository;
import io.luowei.aichat.model.weixin.MessageTextEntity;
import io.luowei.aichat.model.weixin.MsgTypeVO;
import io.luowei.aichat.model.weixin.UserBehaviorMessageEntity;
import io.luowei.aichat.utils.XmlUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class WeChatBehaviorServiceImpl implements WeChatBehaviorService {

    @Value("${wx.config.originalid}")
    private String originalId;

    @Resource
    private Cache<String,String> codeCache;

    @Resource
    private IWeChatRepository repository;

    /**
     * 1. 用户的请求行文，分为事件event、消息text，这里我们只处理消息内容
     * 2. 用户行为、消息类型，是多样性的，这部分如果用户有更多的扩展需求，可以使用设计模式【模板模式 + 策略模式 + 工厂模式】，分拆逻辑。
     */
    @Override
    public String acceptUserBehavior(UserBehaviorMessageEntity userBehaviorMessageEntity) {
        // Event 事件处理
        if (MsgTypeVO.EVENT.getCode().equals(userBehaviorMessageEntity.getMsgType())) {
            return "";
        }

        if (MsgTypeVO.TEXT.getCode().equals(userBehaviorMessageEntity.getMsgType())) {
            // 缓存验证码
//            String isExistCode = codeCache.getIfPresent(userBehaviorMessageEntity.getOpenId());

            if ("验证码".equals(userBehaviorMessageEntity.getContent())) {
                String code = repository.getCode(userBehaviorMessageEntity.getOpenId());

                MessageTextEntity res = new MessageTextEntity();
                res.setToUserName(userBehaviorMessageEntity.getFromUserName());
                res.setFromUserName(userBehaviorMessageEntity.getToUserName());
                res.setCreateTime(String.valueOf(System.currentTimeMillis() / 1000L));
                res.setMsgType("text");
                res.setContent(String.format("您的验证码为：%s 有效期%d分钟！", code, 3));
                return XmlUtil.beanToXml(res);
            }

            return "";
        }

        throw new ChatGptException(userBehaviorMessageEntity.getMsgType() + " 未被处理的行为类型 Err！");

    }
}
