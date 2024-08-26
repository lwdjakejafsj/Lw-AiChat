package io.luowei.aichat.dao.repository;

import io.luowei.aichat.model.aichat.rule.UserAccountQuotaEntity;

public interface IOpenAiRepository {

    int subAccountQuota(String openai);

    UserAccountQuotaEntity queryUserAccount(String openid);

}