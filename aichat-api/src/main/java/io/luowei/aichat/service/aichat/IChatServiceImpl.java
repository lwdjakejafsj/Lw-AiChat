package io.luowei.aichat.service.aichat;

import com.alibaba.fastjson2.JSON;
import io.luowei.aichat.model.aichat.ChatProcessAggregate;
import io.luowei.aichat.model.aichat.rule.LogicCheckTypeVO;
import io.luowei.aichat.model.aichat.rule.RuleLogicEntity;
import io.luowei.aichat.model.aichat.rule.UserAccountQuotaEntity;
import io.luowei.aichat.service.aichat.rule.ILogicFilter;
import io.luowei.aichat.service.aichat.rule.factory.DefaultLogicFactory;
import io.luowei.sdk.executor.common.EventType;
import io.luowei.sdk.executor.common.Role;
import io.luowei.sdk.executor.parameter.ChatChoice;
import io.luowei.sdk.executor.parameter.CompletionRequest;
import io.luowei.sdk.executor.parameter.CompletionResponse;
import io.luowei.sdk.executor.parameter.Message;
import lombok.extern.slf4j.Slf4j;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class IChatServiceImpl extends AbstractChatService {

    @Resource
    private DefaultLogicFactory logicFactory;


    // 校验流程
    @Override
    protected RuleLogicEntity<ChatProcessAggregate> doCheckLogic(ChatProcessAggregate chatProcess, UserAccountQuotaEntity userAccountQuotaEntity, String... logics) throws Exception {
        Map<String, ILogicFilter<UserAccountQuotaEntity>> logicFilterMap = logicFactory.openLogicFilter();
        RuleLogicEntity<ChatProcessAggregate> entity = null;

        for (String code : logics) {
            if (DefaultLogicFactory.LogicModel.NULL.getCode().equals(code))
                continue;
            entity = logicFilterMap.get(code).filter(chatProcess, userAccountQuotaEntity);
            if (!LogicCheckTypeVO.SUCCESS.equals(entity.getType()))
                return entity;
        }

        return entity != null ? entity : RuleLogicEntity.<ChatProcessAggregate>builder()
                .type(LogicCheckTypeVO.SUCCESS).data(chatProcess).build();
    }

    @Override
    public void doMessageResponse(ChatProcessAggregate chatProcess, ResponseBodyEmitter emitter) throws Exception {

        CompletionRequest request = CompletionRequest.builder()
                .stream(true)
                .model(chatProcess.getModel())
                .build();

        request.setMessages(new ArrayList<Message>(){
            private static final long serialVersionUID = -7988151926241837899L;
            {
                addAll(this.getPromptList());
            }

            private ArrayList<? extends Message> getPromptList() {
                 ArrayList<Message> list = new ArrayList();
                 chatProcess.getMessages().forEach(m -> {
                     Message prompt = Message.builder()
                         .role(Role.USER)
                         .content(m.getContent())
                         .build();
                     list.add(prompt);
                 });

                 return list;
            }
        });

        openAiSession.completions(request, new EventSourceListener() {
            /**
             * 每个模型的结束标识符不一样，为了保持统一全部使用finishReason
             * finishReason 为null就代表正在生成
             * finishReason 为stop代表停止
             */
            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data) {
                CompletionResponse response = JSON.parseObject(data, CompletionResponse.class);

                log.info("response：{}，type：{}",response,type);
                List<ChatChoice> choices = response.getChoices();
                for (ChatChoice chatChoice : choices) {
                    Message delta = chatChoice.getDelta();
                    // 这里需要根据模型判断，老版本返回数据的角色是system，新版本叫做assistant
//                    if (!Role.ASSISTANT.getCode().equals(delta.getRole())) continue;

                    if (EventType.STOP.getCode().equals(chatChoice.getFinishReason())) {
                        emitter.complete();
                        break;
                    }
                    // 发送信息
                    try {
                        log.info("data：{}",delta.getContent());
                        emitter.send(delta.getContent());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

            }

            @Override
            public void onClosed(EventSource eventSource) {
                log.info("对话完成");
            }

        });
    }
}
