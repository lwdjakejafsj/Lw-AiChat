package io.luowei.aichat.service.aichat;

import io.luowei.aichat.common.constants.Constants;
import io.luowei.aichat.common.exception.ChatGptException;
import io.luowei.aichat.dao.repository.IOpenAiRepository;
import io.luowei.aichat.model.aichat.ChatProcessAggregate;
import io.luowei.aichat.model.aichat.rule.LogicCheckTypeVO;
import io.luowei.aichat.model.aichat.rule.RuleLogicEntity;
import io.luowei.aichat.model.aichat.rule.UserAccountQuotaEntity;
import io.luowei.aichat.service.aichat.rule.factory.DefaultLogicFactory;
import io.luowei.sdk.session.OpenAiSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;

@Slf4j
public abstract class AbstractChatService implements IChatService{

    @Resource
    protected OpenAiSession openAiSession;

    @Resource
    private IOpenAiRepository openAiRepository;


    @Override
    public ResponseBodyEmitter completions(ResponseBodyEmitter emitter,ChatProcessAggregate chatProcess) {
        try {
            // 请求应答
            emitter.onCompletion(() -> {
                log.info("流式问答请求完成，使用模型：{}", chatProcess.getModel());
            });

            emitter.onError(throwable ->
                    log.error("流式问答请求错误，使用模型：{}", chatProcess.getModel(), throwable)
            );
            System.out.println(chatProcess);

            UserAccountQuotaEntity userAccount = openAiRepository.queryUserAccount(chatProcess.getOpenId());

            // 3. 规则过滤
            RuleLogicEntity<ChatProcessAggregate> ruleLogicEntity = this.doCheckLogic(chatProcess, userAccount,
                    DefaultLogicFactory.LogicModel.ACCESS_LIMIT.getCode(),
                    DefaultLogicFactory.LogicModel.SENSITIVE_WORD.getCode(),
                    null != userAccount ? DefaultLogicFactory.LogicModel.ACCOUNT_STATUS.getCode() : DefaultLogicFactory.LogicModel.NULL.getCode(),
                    null != userAccount ? DefaultLogicFactory.LogicModel.MODEL_TYPE.getCode() : DefaultLogicFactory.LogicModel.NULL.getCode(),
                    null != userAccount ? DefaultLogicFactory.LogicModel.USER_QUOTA.getCode() : DefaultLogicFactory.LogicModel.NULL.getCode()
            );

            if (!LogicCheckTypeVO.SUCCESS.equals(ruleLogicEntity.getType())) {
                emitter.send(ruleLogicEntity.getInfo());
                emitter.complete();
                return emitter;
            }

            // 应答处理
            this.doMessageResponse(chatProcess, emitter);
        } catch (Exception e) {
            throw new ChatGptException(Constants.ResponseCode.UN_ERROR.getCode(), Constants.ResponseCode.UN_ERROR.getInfo());
        }
        // 4. 返回结果
        return emitter;
    }

    protected abstract RuleLogicEntity<ChatProcessAggregate> doCheckLogic(ChatProcessAggregate chatProcess,UserAccountQuotaEntity userAccountQuotaEntity, String... logics) throws Exception;

    public abstract void doMessageResponse(ChatProcessAggregate chatProcess, ResponseBodyEmitter emitter) throws Exception;
}