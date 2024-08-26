package io.luowei.sdk.executor.model.chatglm.valobj;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: ZhangZhe
 * @description: 超拟人大模型会话请求对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CharGLMCompletionRequest extends ChatGLMCompletionRequest {

    /**
     * 角色及用户信息数据，该信息中 user_info：用户信息，bot_info：角色信息，bot_name：角色名，user_name：用户名
     */
    @JsonProperty("meta")
    private Meta meta;

    /**
     * 用于控制每次返回内容的类型，空或者没有此字段时默认按照json_string返回
     * - json_string 返回标准的 JSON 字符串
     * - text 返回原始的文本内容
     */
    @JsonProperty("return_type")
    private String returnType;


    /**
     *
     */
    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Meta {

        /**
         * 用户信息
         */
        @JsonProperty("user_info")
        private String userInfo;

        /**
         * 用户名称
         */
        @JsonProperty("user_name")
        private String userName = "用户";

        /**
         * 角色信息
         */
        @JsonProperty("bot_info")
        private String botInfo;

        /**
         * 角色名称
         */
        @JsonProperty("bot_name")
        private String botName;

    }


    @Override
    public String toString() {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("request_id", getRequestId());
        paramsMap.put("prompt", getPrompt());
        paramsMap.put("incremental", isIncremental());
        paramsMap.put("temperature", getTemperature());
        paramsMap.put("top_p", getTopP());
        paramsMap.put("sseFormat", getSseFormat());
        paramsMap.put("meta", meta);
        paramsMap.put("return_type", returnType);
        try {
            return new ObjectMapper().writeValueAsString(paramsMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
