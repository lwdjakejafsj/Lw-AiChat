package io.luowei.aichat.service.aichat.rule.Impl;

import io.luowei.aichat.common.annotation.LogicStrategy;
import io.luowei.aichat.model.aichat.ChatProcessAggregate;
import io.luowei.aichat.model.aichat.rule.LogicCheckTypeVO;
import io.luowei.aichat.model.aichat.rule.RuleLogicEntity;
import io.luowei.aichat.model.aichat.rule.UserAccountQuotaEntity;
import io.luowei.aichat.model.aichat.rule.UserAccountStatusVO;
import io.luowei.aichat.service.aichat.rule.ILogicFilter;
import io.luowei.aichat.service.aichat.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 账户校验
 * author: luowei
 * date:
 */
@Slf4j
@Service
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.ACCOUNT_STATUS)
public class AccountStatusFilter implements ILogicFilter<UserAccountQuotaEntity> {

    @Override
    public RuleLogicEntity<ChatProcessAggregate> filter(ChatProcessAggregate chatProcess, UserAccountQuotaEntity data) throws Exception {
        // 账户可用，直接放行
        if (UserAccountStatusVO.AVAILABLE.equals(data.getUserAccountStatusVO())) {
            return RuleLogicEntity.<ChatProcessAggregate>builder()
                    .type(LogicCheckTypeVO.SUCCESS).data(chatProcess).build();
        }

        return RuleLogicEntity.<ChatProcessAggregate>builder()
                .info("您的账户已冻结，暂时不可使用。如果有疑问，可以联系客户解冻账户。")
                .type(LogicCheckTypeVO.REFUSE).data(chatProcess).build();
    }

}
