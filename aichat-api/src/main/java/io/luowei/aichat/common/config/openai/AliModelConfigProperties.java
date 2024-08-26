package io.luowei.aichat.common.config.openai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "model.ali", ignoreInvalidFields = true)
public class AliModelConfigProperties {

    private String apiHost;
    private String apiKey;

}
