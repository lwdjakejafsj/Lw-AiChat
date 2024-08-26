package io.luowei.aichat.dao.repository.Impl;

import io.luowei.aichat.common.redis.IRedisService;
import io.luowei.aichat.dao.repository.IAuthRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class AuthRepository implements IAuthRepository {
    private static final String Key = "wechat_code";

    @Resource
    private IRedisService redisService;

    @Override
    public String getCodeUserOpenId(String code) {
        return redisService.getValue(Key + "_" + code);
    }

    @Override
    public void removeCodeByOpenId(String code, String openId) {
        redisService.remove(Key + "_" + code);
        redisService.remove(Key + "_" + openId);
    }
}