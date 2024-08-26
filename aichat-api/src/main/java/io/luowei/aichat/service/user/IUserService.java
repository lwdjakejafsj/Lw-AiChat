package io.luowei.aichat.service.user;

import io.luowei.aichat.model.user.UserAccountEntity;

public interface IUserService {

    UserAccountEntity getUserInfo(String openid);

    void updateUserInfo(UserAccountEntity userAccountEntity);

}
