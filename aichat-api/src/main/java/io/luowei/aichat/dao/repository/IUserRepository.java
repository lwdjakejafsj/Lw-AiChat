package io.luowei.aichat.dao.repository;

import io.luowei.aichat.model.user.UserAccountEntity;

public interface IUserRepository {

    UserAccountEntity getUserInfo(String openid);

    void updateUserInfo(UserAccountEntity userAccount);

    void updateTotalSignCount(String openid);

    Integer getTotalSignCount(String openid);

    void updateIntegral(String openId);
}
