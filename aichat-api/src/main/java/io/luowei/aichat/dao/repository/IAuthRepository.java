package io.luowei.aichat.dao.repository;

public interface IAuthRepository {
    String getCodeUserOpenId(String code);

    void removeCodeByOpenId(String code, String openId);
}