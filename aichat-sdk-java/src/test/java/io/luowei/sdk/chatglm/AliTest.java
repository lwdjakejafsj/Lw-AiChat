package io.luowei.sdk.chatglm;

import com.alibaba.fastjson.JSON;
import io.luowei.sdk.executor.common.Model;
import io.luowei.sdk.executor.common.Role;
import io.luowei.sdk.executor.model.ali.config.AliConfig;
import io.luowei.sdk.executor.model.chatglm.config.ChatGLMConfig;
import io.luowei.sdk.executor.parameter.ChatChoice;
import io.luowei.sdk.executor.parameter.CompletionRequest;
import io.luowei.sdk.executor.parameter.CompletionResponse;
import io.luowei.sdk.executor.parameter.Message;
import io.luowei.sdk.session.OpenAiSession;
import io.luowei.sdk.session.OpenAiSessionFactory;
import io.luowei.sdk.session.defaults.DefaultOpenAiSessionFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class AliTest {

    private OpenAiSession openAiSession;

    @Before
    public void test_OpenAiSessionFactory() {
        AliConfig aliConfig = new AliConfig();

        aliConfig.setApiKey("sk-79dce452719b42a6800f4c53f5a61084");

        // 1配置文件
        io.luowei.sdk.session.Configuration configuration = new io.luowei.sdk.session.Configuration();
        configuration.setAliConfig(aliConfig);

        // 会话工厂
        OpenAiSessionFactory factory = new DefaultOpenAiSessionFactory(configuration);
        // 3. 开启会话
        this.openAiSession = factory.openSession();
    }

    @Test
    public void test_completions() throws Exception {
        // 1. 创建参数
        CompletionRequest request = CompletionRequest.builder()
                .stream(true)
                .messages(Collections.singletonList(Message.builder().role(Role.USER).content("1+1").build()))
                .model(Model.QWEN_TURBO.getCode())
                .build();

        // 2. 请求等待
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // 3. 应答请求
        EventSource eventSource = openAiSession.completions(request, new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data) {
                if ("[DONE]".equalsIgnoreCase(data)) {
                    log.info("OpenAI 应答完成");
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
                        return;
                    }

                    log.info("测试结果：{}", delta.getContent());
                }
            }

            @Override
            public void onClosed(EventSource eventSource) {
                log.info("对话完成");
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                log.info("对话异常");
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();
    }
}
