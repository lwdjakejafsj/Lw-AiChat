package io.luowei.sdk.executor.model.chatglm;


import com.alibaba.fastjson.JSON;
import io.luowei.sdk.executor.Executor;
import io.luowei.sdk.executor.common.Role;
import io.luowei.sdk.executor.model.chatglm.config.ChatGLMConfig;
import io.luowei.sdk.executor.model.chatglm.utils.BearerTokenUtils;
import io.luowei.sdk.executor.model.chatglm.valobj.CharGLMCompletionRequest;
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
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.luowei.sdk.executor.common.Role.*;


/**
 * @author: ZhangZhe
 * @description: 超拟人大模型执行器
 */
@Slf4j
public class CharGLMModelExecutor implements Executor, ParameterHandler<CharGLMCompletionRequest>, ResultHandler {

    /**
     * 配置信息
     */
    private final ChatGLMConfig chatGLMConfig;
    /**
     * 工厂事件
     */
    private final EventSource.Factory factory;

    public CharGLMModelExecutor(Configuration configuration) {
        this.chatGLMConfig = configuration.getChatGLMConfig();
        this.factory = configuration.createRequestFactory();
    }

    /**
     * 问答模式，流式反馈
     *
     * @param completionRequest   请求信息
     * @param eventSourceListener 实现监听；通过监听的 onEvent 方法接收数据
     * @return 应答结果
     * @throws Exception 异常
     */
    @Override
    public EventSource completions(CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws Exception {
        //转换参数信息
        CharGLMCompletionRequest charGLMCompletionRequest = getParameterObject(completionRequest);

        //构建请求信息
        Request request = new Request.Builder()
                .header("Authorization", "Bearer " + BearerTokenUtils.getToken(chatGLMConfig.getApiKey(), chatGLMConfig.getApiSecret()))
                .url(chatGLMConfig.getApiHost().concat(chatGLMConfig.getV3_completions()).replace("{model}", completionRequest.getModel()))
                .post(RequestBody.create(MediaType.parse(Configuration.APPLICATION_JSON), charGLMCompletionRequest.toString()))
                .build();
        // 3. 返回事件结果
        return factory.newEventSource(request, eventSourceListener(eventSourceListener));
    }

    /**
     * 问答模式，流式反馈 & 接收用户自定义 apiHost、apiKey - 适用于每个用户都有自己独立配置的场景
     *
     * @param apiHostByUser       apiHost
     * @param apiKeyByUser        apiKey
     * @param completionRequest   请求信息
     * @param eventSourceListener 实现监听；通过监听的 onEvent 方法接收数据
     * @return 应答结果
     * @throws Exception 异常
     */
    @Override
    public EventSource completions(String apiHostByUser, String apiKeyByUser, CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws Exception {
        //转换参数信息
        CharGLMCompletionRequest charGLMCompletionRequest = getParameterObject(completionRequest);

        // 2. 自定义配置
        ChatGLMConfig chatGLMConfigByUser = new ChatGLMConfig();
        chatGLMConfigByUser.setApiHost(apiHostByUser);
        if (null != apiKeyByUser) {
            chatGLMConfigByUser.setApiSecretKey(apiKeyByUser);
        }


        String apiKey = chatGLMConfigByUser.getApiKey() == null ? chatGLMConfig.getApiKey() : chatGLMConfigByUser.getApiKey();
        String apiSecret = chatGLMConfigByUser.getApiSecret() == null ? chatGLMConfig.getApiSecret() : chatGLMConfigByUser.getApiSecret();

        //构建请求信息
        Request request = new Request.Builder()
                .header("Authorization", "Bearer " + BearerTokenUtils.getToken(apiKey, apiSecret))
                .url(chatGLMConfig.getApiHost().concat(chatGLMConfig.getV3_completions()).replace("{model}", completionRequest.getModel()))
                .post(RequestBody.create(MediaType.parse(Configuration.APPLICATION_JSON), charGLMCompletionRequest.toString()))
                .build();
        // 3. 返回事件结果
        return factory.newEventSource(request, eventSourceListener(eventSourceListener));
    }

    /**
     * 生成图片
     *
     * @param imageRequest 图片描述
     * @return 应答结果
     */
    @Override
    public ImageResponse genImages(ImageRequest imageRequest) throws Exception {
        return null;
    }

    /**
     * 生成图片
     *
     * @param apiHostByUser apiHost
     * @param apiKeyByUser  apiKey
     * @param imageRequest  图片描述
     * @return 应答结果
     */
    @Override
    public ImageResponse genImages(String apiHostByUser, String apiKeyByUser, ImageRequest imageRequest) throws Exception {
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

    /**
     * 图片理解
     *
     * @param pictureRequest      图片和对图片的描述
     * @param eventSourceListener
     * @return 应答结果
     * @throws Exception
     */
    @Override
    public EventSource pictureUnderstanding(PictureRequest pictureRequest, EventSourceListener eventSourceListener) throws Exception {
        return null;
    }

    /**
     * 图片理解
     *
     * @param apiHostByUser
     * @param apiKeyByUser
     * @param pictureRequest      图片和对图片的描述
     * @param eventSourceListener
     * @return 应答结果
     * @throws Exception
     */
    @Override
    public EventSource pictureUnderstanding(String apiHostByUser, String apiKeyByUser, PictureRequest pictureRequest, EventSourceListener eventSourceListener) throws Exception {
        return null;
    }

    @Override
    public CharGLMCompletionRequest getParameterObject(CompletionRequest completionRequest) {
        CharGLMCompletionRequest request = new CharGLMCompletionRequest();

        request.setTemperature(completionRequest.getTemperature());
        request.setTopP(completionRequest.getTopP());
        List<ChatGLMCompletionRequest.Prompt> prompts = new ArrayList<>();
        request.setMeta(CharGLMCompletionRequest.Meta.builder().build());
        List<Message> messages = completionRequest.getMessages();
        for (Message message : messages) {
            String role = message.getRole();
            if (StringUtils.equals(role, USER_INFO.getCode())) {
                request.getMeta().setUserInfo(message.getContent());
            } else if (StringUtils.equals(role, BOT_INFO.getCode())) {
                request.getMeta().setBotInfo(message.getContent());
            } else if (StringUtils.equals(role, USER_NAME.getCode())) {
                request.getMeta().setUserName(message.getContent());
            } else if (StringUtils.equals(role, BOT_NAME.getCode())) {
                request.getMeta().setBotName(message.getContent());
            } else {
                prompts.add(ChatGLMCompletionRequest.Prompt.builder()
                        .role(message.getRole())
                        .content(message.getContent())
                        .build());
            }
        }
        request.setPrompt(prompts);
        return request;
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
            public void onFailure(EventSource eventSource, @javax.annotation.Nullable Throwable t, @javax.annotation.Nullable Response response) {
                eventSourceListener.onFailure(eventSource, t, response);
            }
        };
    }
}
