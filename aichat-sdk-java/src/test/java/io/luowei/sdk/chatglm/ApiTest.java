package io.luowei.sdk.chatglm;


import com.alibaba.fastjson.JSON;
import io.luowei.sdk.executor.common.Model;
import io.luowei.sdk.executor.common.Role;
import io.luowei.sdk.executor.model.chatglm.config.ChatGLMConfig;
import io.luowei.sdk.executor.model.chatglm.valobj.EventType;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;


@Slf4j
public class ApiTest {

    private OpenAiSession openAiSession;

    @Before
    public void test_OpenAiSessionFactory() {
        ChatGLMConfig chatGLMConfig = new ChatGLMConfig();
        chatGLMConfig.setApiHost("https://open.bigmodel.cn/");
        chatGLMConfig.setApiSecretKey("b960e564e87f342bf86bbd80b9d15a16.yWDk5kdyLVwsNYyD");

        // 1配置文件
        io.luowei.sdk.session.Configuration configuration = new io.luowei.sdk.session.Configuration();
        configuration.setChatGLMConfig(chatGLMConfig);

        // 会话工厂
        OpenAiSessionFactory factory = new DefaultOpenAiSessionFactory(configuration);
        // 3. 开启会话
        this.openAiSession = factory.openSession();
    }

    /**
     * 流式对话
     */
    @Test
    public void test_completions() throws Exception {
// 1. 创建参数
        CompletionRequest request = CompletionRequest.builder()
                .stream(true)
                .messages(Collections.singletonList(Message.builder().role(Role.USER).content("你好").build()))
                .model(Model.CHATGLM_TURBO.getCode())
                .build();

        // 2. 请求等待
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // 3. 应答请求
        EventSource eventSource = openAiSession.completions(request, new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, @javax.annotation.Nullable String id, @javax.annotation.Nullable String type, String data) {
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

                    log.info("测试结果：{},type：{}", chatChoice.toString(),type);
                }
            }

            @Override
            public void onClosed(EventSource eventSource) {
                log.info("对话完成");
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(EventSource eventSource, @javax.annotation.Nullable Throwable t, @Nullable Response response) {
                log.info("对话异常");
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();
    }

    @Test
    public void test_genImages() throws Exception {
//        ImageRequest request = new ImageRequest();
//        request.setModel(Model.COGVIEW_3);
//        request.setPrompt("画个小狗");
//        ImageResponse response = openAiSession.genImages(request);
//        log.info("测试结果：{}", JSON.toJSONString(response));
    }
}