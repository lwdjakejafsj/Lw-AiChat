package io.luowei.sdk.executor.model.ali.config;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class AliConfig {

    @Getter
    @Setter
    private String apiHost = "https://dashscope.aliyuncs.com/";

    @Getter
    @Setter
    private String apiKey;

    @Getter
    private String v1_completions = "api/v1/services/aigc/text-generation/generation";



}
