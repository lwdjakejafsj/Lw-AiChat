package io.luowei.sdk.executor.model.chatglm;


import com.alibaba.fastjson.JSON;
import io.luowei.sdk.executor.Executor;
import io.luowei.sdk.executor.common.Role;
import io.luowei.sdk.executor.model.chatglm.config.ChatGLMConfig;
import io.luowei.sdk.executor.model.chatglm.utils.BearerTokenUtils;
import io.luowei.sdk.executor.model.chatglm.valobj.ChatGLMCompletionRequest;
import io.luowei.sdk.executor.model.chatglm.valobj.ChatGLMCompletionResponse;
import io.luowei.sdk.executor.model.chatglm.valobj.EventType;
import io.luowei.sdk.executor.parameter.*;
import io.luowei.sdk.executor.result.ResultHandler;
import io.luowei.sdk.session.Configuration;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ChatGLM 模型执行器
 * author：luowei
 */
@Slf4j
public class ChatGLMModelExecutor implements Executor, ParameterHandler<ChatGLMCompletionRequest>, ResultHandler {

    /**
     * 配置信息
     */
    private final ChatGLMConfig chatGLMConfig;
    /**
     * 工厂事件
     */
    private final EventSource.Factory factory;

    public ChatGLMModelExecutor(Configuration configuration) {
        this.chatGLMConfig = configuration.getChatGLMConfig();
        this.factory = configuration.createRequestFactory();
    }

    @Override
    public EventSource completions(CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws Exception {
        // 1. 转换参数信息
        ChatGLMCompletionRequest chatGLMCompletionRequest = getParameterObject(completionRequest);
        // 2. 构建请求信息
        Request request = new Request.Builder()
                .header("Authorization", "Bearer " + BearerTokenUtils.getToken(chatGLMConfig.getApiKey(), chatGLMConfig.getApiSecret()))
                .url(chatGLMConfig.getApiHost().concat(chatGLMConfig.getV3_completions()).replace("{model}", completionRequest.getModel()))
                .post(RequestBody.create(MediaType.parse(Configuration.APPLICATION_JSON), chatGLMCompletionRequest.toString()))
                .build();
        // 3. 返回事件结果
        return factory.newEventSource(request, eventSourceListener(eventSourceListener));
    }

    @Override
    public EventSource completions(String apiHostByUser, String apiKeyByUser, CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws Exception {
        // 1. 转换参数信息
        ChatGLMCompletionRequest chatGLMCompletionRequest = getParameterObject(completionRequest);

        // 2. 自定义配置
        ChatGLMConfig chatGLMConfigByUser = new ChatGLMConfig();
        chatGLMConfigByUser.setApiHost(apiHostByUser);
        if (null != apiKeyByUser) {
            chatGLMConfigByUser.setApiSecretKey(apiKeyByUser);
        }

        String apiHost = chatGLMConfigByUser.getApiHost() == null ? chatGLMConfig.getApiHost() : chatGLMConfigByUser.getApiHost();
        String apiKey = chatGLMConfigByUser.getApiKey() == null ? chatGLMConfig.getApiKey() : chatGLMConfigByUser.getApiKey();
        String apiSecret = chatGLMConfigByUser.getApiSecret() == null ? chatGLMConfig.getApiSecret() : chatGLMConfigByUser.getApiSecret();

        // 3. 构建请求信息
        Request request = new Request.Builder()
                .header("Authorization", "Bearer " + BearerTokenUtils.getToken(apiKey, apiSecret))
                .url(chatGLMConfig.getApiHost().concat(chatGLMConfig.getV3_completions()).replace("{model}", completionRequest.getModel()))
                .post(RequestBody.create(MediaType.parse(Configuration.APPLICATION_JSON), chatGLMCompletionRequest.toString()))
                .build();
        // 4. 返回事件结果
        return factory.newEventSource(request, eventSourceListener(eventSourceListener));
    }

    @Override
    public ImageResponse genImages(ImageRequest imageRequest) {
        return null;
    }

    @Override
    public ImageResponse genImages(String apiHostByUser, String apiKeyByUser, ImageRequest imageRequest) {
        return null;
    }

    @Override
    public ImageResponse editImages(ImageEditRequest imageEditRequest) throws Exception {
        return null;
    }

    @Override
    public ImageResponse editImages(String apiHostByUser, String apiKeyByUser, ImageEditRequest imageEditRequest) throws Exception {
        return null;
    }

    @Override
    public EventSource pictureUnderstanding(PictureRequest pictureRequest, EventSourceListener eventSourceListener) throws Exception {
        return null;
    }

    @Override
    public EventSource pictureUnderstanding(String apiHostByUser, String apiKeyByUser, PictureRequest pictureRequest, EventSourceListener eventSourceListener) throws Exception {
        return null;
    }

    @Override
    public ChatGLMCompletionRequest getParameterObject(CompletionRequest completionRequest) {

        ChatGLMCompletionRequest chatGLMCompletionRequest = new ChatGLMCompletionRequest();
        chatGLMCompletionRequest.setTemperature(completionRequest.getTemperature());
        chatGLMCompletionRequest.setTopP(completionRequest.getTopP());
        List<ChatGLMCompletionRequest.Prompt> prompts = new ArrayList<>();

        // 重新组装参数，ChatGLM 需要用 Okay 间隔历史消息
        List<Message> messages = completionRequest.getMessages();
        for (int i = 0; i < messages.size(); i++) {
            Message message = messages.get(i);
            if (0 == i) {
                prompts.add(ChatGLMCompletionRequest.Prompt.builder()
                        .role(message.getRole())
                        .content(message.getContent())
                        .build());
            } else {
                String role = message.getRole();
                if (Objects.equals(role, Role.SYSTEM.getCode())) {
                    prompts.add(ChatGLMCompletionRequest.Prompt.builder()
                            .role(Role.SYSTEM.getCode())
                            .content(message.getContent())
                            .build());

                    prompts.add(ChatGLMCompletionRequest.Prompt.builder()
                            .role(Role.USER.getCode())
                            .content("Okay")
                            .build());
                } else {
                    prompts.add(ChatGLMCompletionRequest.Prompt.builder()
                            .role(Role.USER.getCode())
                            .content(message.getContent())
                            .build());

                    prompts.add(ChatGLMCompletionRequest.Prompt.builder()
                            .role(Role.USER.getCode())
                            .content("Okay")
                            .build());
                }
            }
        }

        chatGLMCompletionRequest.setPrompt(prompts);

        return chatGLMCompletionRequest;
    }

    @Override
    public EventSourceListener eventSourceListener(EventSourceListener eventSourceListener) {
        return new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data) {
                ChatGLMCompletionResponse response = JSON.parseObject(data, ChatGLMCompletionResponse.class);
                // type 消息类型，add 增量，finish 结束，error 错误，interrupted 中断
                if (EventType.add.getCode().equals(type)) {
                    CompletionResponse completionResponse = new CompletionResponse();
                    List<ChatChoice> choices = new ArrayList<>();
                    ChatChoice chatChoice = new ChatChoice();
                    chatChoice.setDelta(Message.builder()
                            .name("")
                            .role(Role.SYSTEM)
                            .content(response.getData())
                            .build());
                    choices.add(chatChoice);
                    completionResponse.setChoices(choices);
                    eventSourceListener.onEvent(eventSource, id, type, JSON.toJSONString(completionResponse));
                } else if (EventType.finish.getCode().equals(type)) {
                    ChatGLMCompletionResponse.Meta meta = JSON.parseObject(response.getMeta(), ChatGLMCompletionResponse.Meta.class);
                    ChatGLMCompletionResponse.Usage chatGLMUsage = meta.getUsage();
                    // 封装额度
                    Usage usage = new Usage();
                    usage.setPromptTokens(chatGLMUsage.getPrompt_tokens());
                    usage.setCompletionTokens(chatGLMUsage.getCompletion_tokens());
                    usage.setTotalTokens(chatGLMUsage.getTotal_tokens());
                    // 封装结束
                    List<ChatChoice> choices = new ArrayList<>();
                    ChatChoice chatChoice = new ChatChoice();
                    chatChoice.setFinishReason("stop");
                    chatChoice.setDelta(Message.builder()
                            .name("")
                            .role(Role.SYSTEM)
                            .content(response.getData())
                            .build());
                    choices.add(chatChoice);

                    // 构建结果
                    CompletionResponse completionResponse = new CompletionResponse();
                    completionResponse.setChoices(choices);
                    completionResponse.setUsage(usage);
                    completionResponse.setCreated(System.currentTimeMillis());
                    // 返回数据
                    eventSourceListener.onEvent(eventSource, id, type, JSON.toJSONString(completionResponse));
                } else {
                    onClosed(eventSource);
                }

            }

            @Override
            public void onClosed(EventSource eventSource) {
                eventSourceListener.onClosed(eventSource);
            }

            @Override
            public void onOpen(EventSource eventSource, Response response) {
                eventSourceListener.onOpen(eventSource, response);
            }

            @Override
            public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                eventSourceListener.onFailure(eventSource, t, response);
            }

        };
    }

}
