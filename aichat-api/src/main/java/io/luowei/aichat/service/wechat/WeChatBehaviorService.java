package io.luowei.aichat.service.wechat;

import io.luowei.aichat.model.weixin.UserBehaviorMessageEntity;

public interface WeChatBehaviorService {

    String acceptUserBehavior(UserBehaviorMessageEntity userBehaviorMessageEntity);

}
