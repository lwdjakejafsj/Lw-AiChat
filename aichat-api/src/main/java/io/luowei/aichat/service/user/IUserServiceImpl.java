package io.luowei.aichat.service.user;

import io.luowei.aichat.dao.repository.IUserRepository;
import io.luowei.aichat.model.user.UserAccountEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class IUserServiceImpl implements IUserService {

    @Resource
    private IUserRepository userRepository;


    @Override
    public UserAccountEntity getUserInfo(String openid) {
        return userRepository.getUserInfo(openid);
    }

    @Override
    public void updateUserInfo(UserAccountEntity userAccountEntity) {
        userRepository.updateUserInfo(userAccountEntity);
    }
}
