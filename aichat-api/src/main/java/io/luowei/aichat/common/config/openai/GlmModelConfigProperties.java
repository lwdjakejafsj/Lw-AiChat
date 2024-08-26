package io.luowei.aichat.common.config.openai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "model.chatglm", ignoreInvalidFields = true)
public class GlmModelConfigProperties {

    private String apiHost;
    private String apiSecretKey;

}
