package io.luowei.aichat.dao.mapper;

import io.luowei.aichat.dao.po.UserAccount;
import org.apache.ibatis.annotations.Mapper;

/**
* @author luowei
* @description 针对表【user_account】的数据库操作Mapper
* @createDate 2024-06-19 21:14:42
* @Entity generator.domain.UserAccount
*/
@Mapper
public interface UserAccountMapper {


    int subAccountQuota(String openid);

    UserAccount queryUserAccount(String openid);

    void insert(UserAccount newUserAccount);

    int addAccountQuota(UserAccount newUserAccount);

    void updateUserAccount(UserAccount newUserAccount);

    void updateTotalSignCount(String openid);

    Integer getTotalSignCount(String openid);

    void updateIntegral(String openid);
}
