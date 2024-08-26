package io.luowei.sdk.executor.parameter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.luowei.sdk.executor.common.Model;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 对话请求信息
 *
 * author：luowei
 */
@Data
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompletionRequest implements Serializable {

    private static final long serialVersionUID = 2447852191771534038L;

    /**
     * 默认模型
     */
    private String model = Model.CHATGLM_TURBO.getCode();
    /**
     * 问题描述
     */
    private List<Message> messages;
    /**
     * 控制温度【随机性】；0到2之间。较高的值(如0.8)将使输出更加随机，而较低的值(如0.2)将使输出更加集中和确定
     */
    @Builder.Default
    private double temperature = 0.2;
    /**
     * 多样性控制；使用温度采样的替代方法称为核心采样，其中模型考虑具有top_p概率质量的令牌的结果。因此，0.1 意味着只考虑包含前 10% 概率质量的代币
     */
    @JsonProperty("top_p")
    @Builder.Default
    private Double topP = 1d;
    /**
     * 为每个提示生成的完成次数
     */
    @Builder.Default
    private Integer n = 1;
    /**
     * 是否为流式输出；就是一蹦一蹦的，出来结果
     */
    @Builder.Default
    private boolean stream = false;
    /**
     * 停止输出标识
     */
    private List<String> stop;
    /**
     * 输出字符串限制；0 ~ 4096
     */
    @JsonProperty("max_tokens")
    @Builder.Default
    private Integer maxTokens = 2048;
    /**
     * 频率惩罚；降低模型重复同一行的可能性
     */
    @JsonProperty("frequency_penalty")
    @Builder.Default
    private double frequencyPenalty = 0;
    /**
     * 存在惩罚；增强模型谈论新话题的可能性
     */
    @JsonProperty("presence_penalty")
    @Builder.Default
    private double presencePenalty = 0;
    /**
     * 生成多个调用结果，只显示最佳的。这样会更多的消耗你的 api token
     */
    @JsonProperty("logit_bias")
    private Map logitBias;
    /**
     * 调用标识，避免重复调用
     */
    private String user;

//    @Getter
//    @AllArgsConstructor
//    public enum Role {
//
//        SYSTEM("system"),
//        USER("user"),
//        ASSISTANT("assistant"),
//        MODEL("model"),
//
//
//        USER_INFO("user_info"),
//        BOT_INFO("bot_info"),
//        BOT_NAME("bot_name"),
//        USER_NAME("user_name"),
//        ;
//
//        private final String code;
//
//    }

//    @Getter
//    @AllArgsConstructor
//    public enum Model {
//        /**
//         * gpt-3.5-turbo
//         */
//        GPT_3_5_TURBO("gpt-3.5-turbo"),
//        GPT_3_5_TURBO_1106("gpt-3.5-turbo-1106"),
//        /**
//         * gpt-3.5-turbo-16k
//         */
//        GPT_3_5_TURBO_16K("gpt-3.5-turbo-16k"),
//        /**
//         * GPT4.0
//         */
//        GPT_4("gpt-4"),
//        /**
//         * GPT4.0 超长上下文
//         */
//        GPT_4_32K("gpt-4-32k"),
//        DALL_E_2("dall-e-2"),
//        DALL_E_3("dall-e-3"),
//        /**
//         * ChatGLM
//         */
//        CHATGLM_TURBO("chatglm_turbo"),
//        /**
//         * ChatGLM-超拟人大模型
//         */
//        CHARGLM_3("charglm-3"),
//        /**
//         * xunfei
//         */
//        XUNFEI("xunfei"),
//        /**
//         * 阿里通义千问
//         */
//        QWEN_TURBO("qwen-turbo"),
//        QWEN_PLUS("qwen-plus"),
//        QWEN_MAX("qwen-max"),
//        /**
//         * baidu
//         */
//        ERNIE_BOT_TURBO("ERNIE_Bot_turbo"),
//        ERNIE_BOT("ERNIE_Bot"),
//        ERNIE_Bot_4("ERNIE_Bot_4"),
//        ERNIE_Bot_8K("ERNIE_Bot_8K"),
//        STABLE_DIFFUSION_XL("Stable_Diffusion_XL"),
//        /**
//         * 腾讯混元
//         */
//        HUNYUAN_CHATSTD("hunyuan-chatstd"),
//        HUNYUAN_CHATPRO("hunyuan-chatpro"),
//
//        /**
//         * 360智脑
//         */
//        Brain_360GPT_S2_V9("360GPT_S2_V9");
//
//
//        private final String code;
//    }

}
