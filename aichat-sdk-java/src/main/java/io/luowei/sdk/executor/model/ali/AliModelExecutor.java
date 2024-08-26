package io.luowei.sdk.executor.model.ali;

import com.alibaba.fastjson.JSON;
import io.luowei.sdk.executor.Executor;
import io.luowei.sdk.executor.common.Role;
import io.luowei.sdk.executor.model.ali.config.AliConfig;
import io.luowei.sdk.executor.model.ali.valobj.AliCompletionRequest;
import io.luowei.sdk.executor.model.ali.valobj.AliCompletionResponse;
import io.luowei.sdk.executor.model.ali.valobj.EventType;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 阿里模型执行器
 * author: luowei
 * date:
 */
@Slf4j
public class AliModelExecutor implements Executor, ParameterHandler<AliCompletionRequest>, ResultHandler {

    private final AliConfig aliConfig;

    private final EventSource.Factory factory;

    public AliModelExecutor(Configuration configuration) {
        this.aliConfig = configuration.getAliConfig();
        this.factory = configuration.createRequestFactory();
    }

    @Override
    public EventSource completions(CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws Exception {

        AliCompletionRequest aliCompletionRequest = getParameterObject(completionRequest);

        Request request = new Request.Builder()
                .header("Authorization", "Bearer " + aliConfig.getApiKey())
                .url(aliConfig.getApiHost().concat(aliConfig.getV1_completions()))
                .post(RequestBody.create(MediaType.parse(Configuration.APPLICATION_JSON), JSON.toJSONString(aliCompletionRequest)))
                .build();


        return factory.newEventSource(request,eventSourceListener(eventSourceListener));
    }

    @Override
    public EventSource completions(String apiHostByUser, String apiKeyByUser, CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws Exception {
        return null;
    }

    @Override
    public ImageResponse genImages(ImageRequest imageRequest) throws Exception {
        return null;
    }

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

    @Override
    public EventSource pictureUnderstanding(PictureRequest pictureRequest, EventSourceListener eventSourceListener) throws Exception {
        return null;
    }

    @Override
    public EventSource pictureUnderstanding(String apiHostByUser, String apiKeyByUser, PictureRequest pictureRequest, EventSourceListener eventSourceListener) throws Exception {
        return null;
    }

    @Override
    public AliCompletionRequest getParameterObject(CompletionRequest completionRequest) {
        AliCompletionRequest request = new AliCompletionRequest();

        request.setModel(completionRequest.getModel());

        AliCompletionRequest.Input input = new AliCompletionRequest.Input();
        List<Message> messages = completionRequest.getMessages();

        List<io.luowei.sdk.executor.model.ali.valobj.Message> aliMessages = new ArrayList<>();
        for (Message message : messages) {
            aliMessages.add(io.luowei.sdk.executor.model.ali.valobj.Message.builder()
                    .role(message.getRole())
                    .content(message.getContent())
                    .build());
        }
        input.setMessages(aliMessages);
        request.setInput(input);
        request.setParameters(AliCompletionRequest.Parameters.builder()
                .incrementalOutput(true)
                .build());

        System.out.println(JSON.toJSONString(request));

        return request;
    }

    @Override
    public EventSourceListener eventSourceListener(EventSourceListener eventSourceListener) {
        return new EventSourceListener() {
            @Override
            public void onOpen(EventSource eventSource, Response response) {
                eventSourceListener.onOpen(eventSource, response);
            }

            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data) {

                AliCompletionResponse aliResponse = JSON.parseObject(data, AliCompletionResponse.class);

                if (EventType.CONTINUE.getCode().equals(aliResponse.getOutput().getFinish_reason())) {
                    CompletionResponse completionResponse = new CompletionResponse();
                    List<ChatChoice> choices = new ArrayList<>();
                    ChatChoice chatChoice = new ChatChoice();
                    chatChoice.setDelta(Message.builder()
                            .role(Role.SYSTEM)
                            .name("")
                            .content(aliResponse.getOutput().getText())
                            .build());
                    choices.add(chatChoice);
                    completionResponse.setChoices(choices);
                    eventSourceListener.onEvent(eventSource, id, type, JSON.toJSONString(completionResponse));
                }else if (EventType.STOP.getCode().equals(aliResponse.getOutput().getFinish_reason())) {
                    AliCompletionResponse.Usage aliUsage = aliResponse.getUsage();
                    Usage usage = new Usage();
                    usage.setPromptTokens(aliUsage.getInput_tokens());
                    usage.setCompletionTokens(aliUsage.getOutput_tokens());
                    usage.setTotalTokens(aliUsage.getTotal_tokens());
                    List<ChatChoice> choices = new ArrayList<>();
                    ChatChoice chatChoice = new ChatChoice();
                    chatChoice.setFinishReason("stop");
                    chatChoice.setDelta(Message.builder()
                            .name("")
                            .role(Role.SYSTEM)
                            .content(aliResponse.getOutput().getText())
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
            public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                eventSourceListener.onOpen(eventSource, response);
            }
        };
    }
}
