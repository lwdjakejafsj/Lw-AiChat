package io.luowei.sdk.executor.model.chatglm.valobj;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.luowei.sdk.executor.common.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ChatGLM 请求参数
 *
 * author：luowei
 */
@Slf4j
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatGLMCompletionRequest {

    /**
     * 模型
     */
    private Model model = Model.CHATGLM_TURBO;

    /**
     * 请求ID
     */
    @JsonProperty("request_id")
    private String requestId = String.format("xfg-%d", System.currentTimeMillis());
    /**
     * 控制温度【随机性】
     */
    private double temperature = 0.9d;
    /**
     * 多样性控制；
     */
    @JsonProperty("top_p")
    private double topP = 0.7d;
    /**
     * 输入给模型的会话信息
     * 用户输入的内容；role=user
     * 挟带历史的内容；role=assistant
     */
    private List<Prompt> prompt;
    /**
     * 智普AI sse 固定参数 incremental = true 【增量返回】
     */
    private boolean incremental = true;
    /**
     * sseformat, 用于兼容解决sse增量模式okhttpsse截取data:后面空格问题, [data: hello]。只在增量模式下使用sseFormat。
     */
    private String sseFormat = "data";

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Prompt {
        private String role;
        private String content;
    }

    @Override
    public String toString() {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("request_id", requestId);
        paramsMap.put("prompt", prompt);
        paramsMap.put("incremental", incremental);
        paramsMap.put("temperature", temperature);
        paramsMap.put("top_p", topP);
        paramsMap.put("sseFormat", sseFormat);
        try {
            return new ObjectMapper().writeValueAsString(paramsMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
