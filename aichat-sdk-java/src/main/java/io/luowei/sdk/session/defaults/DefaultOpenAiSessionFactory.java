package io.luowei.sdk.session.defaults;

import io.luowei.sdk.executor.Executor;
import io.luowei.sdk.executor.interceptor.HTTPInterceptor;
import io.luowei.sdk.session.Configuration;
import io.luowei.sdk.session.OpenAiSession;
import io.luowei.sdk.session.OpenAiSessionFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class DefaultOpenAiSessionFactory implements OpenAiSessionFactory {

    private final Configuration configuration;

    public DefaultOpenAiSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public OpenAiSession openSession() {
        // 1. 日志配置
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(configuration.getLevel());
        // 2. 开启 Http 客户端
        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new HTTPInterceptor(configuration))
                .connectTimeout(configuration.getConnectTimeout(), TimeUnit.SECONDS)
                .writeTimeout(configuration.getWriteTimeout(), TimeUnit.SECONDS)
                .readTimeout(configuration.getReadTimeout(), TimeUnit.SECONDS)
//                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890)))
                .build();

        configuration.setOkHttpClient(okHttpClient);

        // 3. 创建执行器【模型 -> 映射执行器】
        HashMap<String, Executor> executorGroup = configuration.newExecutorGroup();

        // 4. 创建会话服务
        return new DefaultOpenAiSession(configuration, executorGroup);
    }
}
