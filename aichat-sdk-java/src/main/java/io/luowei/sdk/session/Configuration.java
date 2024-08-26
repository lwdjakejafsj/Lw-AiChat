package io.luowei.sdk.session;

import io.luowei.sdk.executor.Executor;
import io.luowei.sdk.executor.common.Model;
import io.luowei.sdk.executor.model.ali.AliModelExecutor;
import io.luowei.sdk.executor.model.ali.config.AliConfig;
import io.luowei.sdk.executor.model.chatglm.CharGLMModelExecutor;
import io.luowei.sdk.executor.model.chatglm.ChatGLMModelExecutor;
import io.luowei.sdk.executor.model.chatglm.config.ChatGLMConfig;
import io.luowei.sdk.executor.parameter.CompletionRequest;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

import java.util.HashMap;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Configuration {

    /**
     * 智谱Ai ChatGLM Config
     */
    private ChatGLMConfig chatGLMConfig;

    private AliConfig aliConfig;

    /**
     * OkHttpClient
     */
    private OkHttpClient okHttpClient;

    private HashMap<String, Executor> executorGroup;

    public EventSource.Factory createRequestFactory() {
        return EventSources.createFactory(okHttpClient);
    }

    // OkHttp 配置信息
    private HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.HEADERS;
    private long connectTimeout = 4500;
    private long writeTimeout = 4500;
    private long readTimeout = 4500;

    // http keywords
    public static final String SSE_CONTENT_TYPE = "text/event-stream";
    public static final String DEFAULT_USER_AGENT = "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)";
    public static final String APPLICATION_JSON = "application/json";
    public static final String JSON_CONTENT_TYPE = APPLICATION_JSON + "; charset=utf-8";

    public HashMap<String, Executor> newExecutorGroup() {
        this.executorGroup = new HashMap<>();
        // ChatGLM 类型执行器填充
        Executor chatGLMModelExecutor = new ChatGLMModelExecutor(this);
        Executor charGLMModelExecutor = new CharGLMModelExecutor(this);
        executorGroup.put(Model.CHATGLM_TURBO.getCode(), chatGLMModelExecutor);
        executorGroup.put(Model.CHARGLM_3.getCode(), charGLMModelExecutor);

        Executor aliModelExecutor = new AliModelExecutor(this);
        executorGroup.put(Model.QWEN_TURBO.getCode(), aliModelExecutor);
        executorGroup.put(Model.QWEN_PLUS.getCode(), aliModelExecutor);
        executorGroup.put(Model.QWEN_MAX.getCode(), aliModelExecutor);

        return this.executorGroup;
    }
}