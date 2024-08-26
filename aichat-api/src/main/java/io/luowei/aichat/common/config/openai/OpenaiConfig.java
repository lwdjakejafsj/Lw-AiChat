package io.luowei.aichat.common.config.openai;


import io.luowei.sdk.executor.model.ali.config.AliConfig;
import io.luowei.sdk.executor.model.chatglm.config.ChatGLMConfig;
import io.luowei.sdk.session.OpenAiSession;
import io.luowei.sdk.session.OpenAiSessionFactory;
import io.luowei.sdk.session.defaults.DefaultOpenAiSessionFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@EnableConfigurationProperties({AliModelConfigProperties.class, GlmModelConfigProperties.class})
public class OpenaiConfig {

    @Resource
    private AliModelConfigProperties aliModelConfigProperties;

    @Resource
    private GlmModelConfigProperties glmModelConfigProperties;

    @Bean
    public OpenAiSession getOpenAiSession() {

        ChatGLMConfig chatGLMConfig = new ChatGLMConfig();
        chatGLMConfig.setApiHost(glmModelConfigProperties.getApiHost());
        chatGLMConfig.setApiSecretKey(glmModelConfigProperties.getApiSecretKey());

        AliConfig aliConfig = new AliConfig();
        aliConfig.setApiHost(aliModelConfigProperties.getApiHost());
        aliConfig.setApiKey(aliModelConfigProperties.getApiKey());

        // 1配置文件
        io.luowei.sdk.session.Configuration configuration = new io.luowei.sdk.session.Configuration();
        configuration.setChatGLMConfig(chatGLMConfig);
        configuration.setAliConfig(aliConfig);

        // 会话工厂
        OpenAiSessionFactory factory = new DefaultOpenAiSessionFactory(configuration);

        // 开启会话
        return factory.openSession();
    }

}
