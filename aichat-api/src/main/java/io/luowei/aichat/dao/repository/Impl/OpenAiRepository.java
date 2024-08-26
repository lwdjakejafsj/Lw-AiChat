package io.luowei.aichat.dao.repository.Impl;

import io.luowei.aichat.dao.mapper.UserAccountMapper;
import io.luowei.aichat.dao.po.UserAccount;
import io.luowei.aichat.dao.repository.IOpenAiRepository;
import io.luowei.aichat.model.aichat.rule.UserAccountQuotaEntity;
import io.luowei.aichat.model.aichat.rule.UserAccountStatusVO;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class OpenAiRepository implements IOpenAiRepository {

    @Resource
    private UserAccountMapper userAccountMapper;

    @Override
    public int subAccountQuota(String openai) {
        return userAccountMapper.subAccountQuota(openai);
    }

    @Override
    public UserAccountQuotaEntity queryUserAccount(String openid) {
        UserAccount userAccount = userAccountMapper.queryUserAccount(openid);
        if (null == userAccount) return null;
        UserAccountQuotaEntity userAccountQuotaEntity = new UserAccountQuotaEntity();
        userAccountQuotaEntity.setOpenid(userAccount.getOpenid());
        userAccountQuotaEntity.setTotalQuota(userAccount.getTotalQuota());
        userAccountQuotaEntity.setSurplusQuota(userAccount.getSurplusQuota());
        userAccountQuotaEntity.setUserAccountStatusVO(UserAccountStatusVO.get(userAccount.getStatus()));
        userAccountQuotaEntity.genModelTypes(userAccount.getModelTypes());
        return userAccountQuotaEntity;
    }
}