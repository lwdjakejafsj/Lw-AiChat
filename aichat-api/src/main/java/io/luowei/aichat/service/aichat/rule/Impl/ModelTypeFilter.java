package io.luowei.aichat.service.aichat.rule.Impl;

import io.luowei.aichat.common.annotation.LogicStrategy;
import io.luowei.aichat.model.aichat.ChatProcessAggregate;
import io.luowei.aichat.model.aichat.rule.LogicCheckTypeVO;
import io.luowei.aichat.model.aichat.rule.RuleLogicEntity;
import io.luowei.aichat.model.aichat.rule.UserAccountQuotaEntity;
import io.luowei.aichat.service.aichat.rule.ILogicFilter;
import io.luowei.aichat.service.aichat.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 模型过滤
 * author: luowei
 * date:
 */
@Slf4j
@Service
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.MODEL_TYPE)
public class ModelTypeFilter implements ILogicFilter<UserAccountQuotaEntity> {

    @Override
    public RuleLogicEntity<ChatProcessAggregate> filter(ChatProcessAggregate chatProcess, UserAccountQuotaEntity data) throws Exception {

        System.out.println("用户可用模型："+data.getAllowModelTypeList());

        // 用户可用模型
        List<String> allowModelTypeList = data.getAllowModelTypeList();
        String modelType = chatProcess.getModel();

        // 模型校验通过
        if (allowModelTypeList.contains(modelType)) {
            return RuleLogicEntity.<ChatProcessAggregate>builder()
                    .type(LogicCheckTypeVO.SUCCESS)
                    .data(chatProcess)
                    .build();
        }
        // 模型校验拦截
        return RuleLogicEntity.<ChatProcessAggregate>builder()
                .type(LogicCheckTypeVO.REFUSE)
                .info("当前账户不支持使用 " + modelType + " 模型！可以联系客服升级账户。")
                .data(chatProcess)
                .build();
    }

}
