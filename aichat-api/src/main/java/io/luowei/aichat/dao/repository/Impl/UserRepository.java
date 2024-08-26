package io.luowei.aichat.dao.repository.Impl;

import io.luowei.aichat.dao.mapper.UserAccountMapper;
import io.luowei.aichat.dao.po.UserAccount;
import io.luowei.aichat.dao.repository.IUserRepository;
import io.luowei.aichat.model.user.UserAccountEntity;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class UserRepository implements IUserRepository {

    @Resource
    private UserAccountMapper userAccountMapper;

    @Override
    public UserAccountEntity getUserInfo(String openid) {
        UserAccount userAccount = userAccountMapper.queryUserAccount(openid);
        return UserAccountEntity.builder()
                .openid(userAccount.getOpenid())
                .userName(userAccount.getUserName())
                .integral(userAccount.getIntegral())
                .avatar(userAccount.getAvatar())
                .totalQuota(userAccount.getTotalQuota())
                .surplusQuota(userAccount.getSurplusQuota())
                .build();
    }

    @Override
    public void updateUserInfo(UserAccountEntity userAccountEntity) {
        UserAccount userAccount = UserAccount.builder()
                .openid(userAccountEntity.getOpenid())
                .avatar(userAccountEntity.getAvatar())
                .userName(userAccountEntity.getUserName())
                .build();
        userAccountMapper.updateUserAccount(userAccount);
    }

    @Override
    public void updateTotalSignCount(String openid) {
        userAccountMapper.updateTotalSignCount(openid);
    }

    @Override
    public Integer getTotalSignCount(String openid) {
        return userAccountMapper.getTotalSignCount(openid);
    }

    @Override
    public void updateIntegral(String openid) {
        userAccountMapper.updateIntegral(openid);
    }
}
