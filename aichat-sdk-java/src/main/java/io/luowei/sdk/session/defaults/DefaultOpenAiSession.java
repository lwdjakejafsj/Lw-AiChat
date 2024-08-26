package io.luowei.sdk.session.defaults;

import com.alibaba.fastjson.JSON;
import io.luowei.sdk.executor.Executor;

import io.luowei.sdk.executor.common.Role;
import io.luowei.sdk.executor.parameter.*;
import io.luowei.sdk.session.Configuration;
import io.luowei.sdk.session.OpenAiSession;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DefaultOpenAiSession implements OpenAiSession {

    private final Configuration configuration;
    private final Map<String, Executor> executorGroup;

    public DefaultOpenAiSession(Configuration configuration, Map<String, Executor> executorGroup) {
        this.configuration = configuration;
        this.executorGroup = executorGroup;
    }

    @Override
    public EventSource completions(CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws Exception {
        // 选择执行器；model -> ChatGLM/ChatGPT
        Executor executor = executorGroup.get(completionRequest.getModel());
        if (null == executor) throw new RuntimeException(completionRequest.getModel() + " 模型执行器尚未实现！");
        return executor.completions(completionRequest, eventSourceListener);
    }

    @Override
    public EventSource completions(String apiHostByUser, String apiKeyByUser, CompletionRequest completionRequest, EventSourceListener eventSourceListener) throws Exception {
        // 选择执行器；model -> ChatGLM/ChatGPT
        Executor executor = executorGroup.get(completionRequest.getModel());
        if (null == executor) throw new RuntimeException(completionRequest.getModel() + " 模型执行器尚未实现！");
        // 执行结果
        return executor.completions(apiHostByUser, apiKeyByUser, completionRequest, eventSourceListener);
    }

    @Override
    public CompletableFuture<String> completions(CompletionRequest completionRequest) throws Exception {
        return completions(null, null, completionRequest);
    }

    @Override
    public CompletableFuture<String> completions(String apiHostByUser, String apiKeyByUser, CompletionRequest completionRequest) throws Exception {
        // 用于执行异步任务并获取结果
        CompletableFuture<String> future = new CompletableFuture<>();
        StringBuffer dataBuffer = new StringBuffer();

        completions(apiHostByUser, apiKeyByUser, completionRequest, new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data) {
                if ("[DONE]".equalsIgnoreCase(data)) {
                    future.complete(dataBuffer.toString());
                    return;
                }

                CompletionResponse chatCompletionResponse = JSON.parseObject(data, CompletionResponse.class);
                List<ChatChoice> choices = chatCompletionResponse.getChoices();
                for (ChatChoice chatChoice : choices) {
                    Message delta = chatChoice.getDelta();
                    if (Role.ASSISTANT.getCode().equals(delta.getRole())) continue;

                    // 应答完成
                    String finishReason = chatChoice.getFinishReason();
                    if (StringUtils.isNoneBlank(finishReason) && "stop".equalsIgnoreCase(finishReason)) {
                        future.complete(dataBuffer.toString());
                        return;
                    }

                    // 填充数据
                    dataBuffer.append(delta.getContent());
                }
            }

            @Override
            public void onClosed(EventSource eventSource) {
                future.completeExceptionally(new RuntimeException("Request closed before completion"));
            }

            @Override
            public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                future.completeExceptionally(new RuntimeException("Request closed before completion"));
            }
        });

        return future;
    }

    @Override
    public ImageResponse genImages(ImageRequest imageRequest) throws Exception {
        // 选择执行器；model -> ChatGLM/ChatGPT
        return executorGroup.get(imageRequest.getModel()).genImages(imageRequest);
    }

    @Override
    public ImageResponse genImages(String apiHostByUser, String apiKeyByUser, ImageRequest imageRequest) throws Exception {
        // 选择执行器；model -> ChatGLM/ChatGPT
        return executorGroup.get(imageRequest.getModel()).genImages(apiHostByUser, apiKeyByUser, imageRequest);
    }

    @Override
    public OkHttpClient getClient() {
        return configuration.getOkHttpClient();
    }

    @Override
    public EventSource pictureUnderstanding(PictureRequest pictureRequest, EventSourceListener eventSourceListener) throws Exception {
        return pictureUnderstanding(null, null, pictureRequest, eventSourceListener);
    }

    @Override
    public EventSource pictureUnderstanding(String apiHostByUser, String apiKeyByUser, PictureRequest pictureRequest, EventSourceListener eventSourceListener) throws Exception {
        // 选择执行器；model -> ChatGLM/ChatGPT
        Executor executor = executorGroup.get(pictureRequest.getModel());
        if (null == executor) throw new RuntimeException(pictureRequest.getModel() + " 模型执行器尚未实现！");
        // 执行结果
        return executor.pictureUnderstanding(apiHostByUser, apiKeyByUser, pictureRequest, eventSourceListener);
    }

    @Override
    public ImageResponse editImages(ImageEditRequest imageEditRequest) throws Exception {
        return editImages(null, null, imageEditRequest);
    }

    @Override
    public ImageResponse editImages(String apiHostByUser, String apiKeyByUser, ImageEditRequest imageEditRequest) throws Exception {
        Executor executor = executorGroup.get(imageEditRequest.getModel());
        return executor.editImages(apiHostByUser, apiKeyByUser, imageEditRequest);
    }
}
