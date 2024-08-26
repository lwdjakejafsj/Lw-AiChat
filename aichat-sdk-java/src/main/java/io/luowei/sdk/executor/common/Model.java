package io.luowei.sdk.executor.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Model {
    /**
     * gpt-3.5-turbo
     */
    GPT_3_5_TURBO("gpt-3.5-turbo"),
    GPT_3_5_TURBO_1106("gpt-3.5-turbo-1106"),
    /**
     * gpt-3.5-turbo-16k
     */
    GPT_3_5_TURBO_16K("gpt-3.5-turbo-16k"),
    /**
     * GPT4.0
     */
    GPT_4("gpt-4"),
    /**
     * GPT4.0 超长上下文
     */
    GPT_4_32K("gpt-4-32k"),
    DALL_E_2("dall-e-2"),
    DALL_E_3("dall-e-3"),
    /**
     * ChatGLM
     */
    CHATGLM_TURBO("chatglm_turbo"),
    /**
     * ChatGLM-超拟人大模型
     */
    CHARGLM_3("charglm-3"),
    /**
     * xunfei
     */
    XUNFEI("xunfei"),
    /**
     * 阿里通义千问
     */
    QWEN_TURBO("qwen-turbo"),
    QWEN_PLUS("qwen-plus"),
    QWEN_MAX("qwen-max"),
    /**
     * baidu
     */
    ERNIE_BOT_TURBO("ERNIE_Bot_turbo"),
    ERNIE_BOT("ERNIE_Bot"),
    ERNIE_Bot_4("ERNIE_Bot_4"),
    ERNIE_Bot_8K("ERNIE_Bot_8K"),
    STABLE_DIFFUSION_XL("Stable_Diffusion_XL"),
    /**
     * 腾讯混元
     */
    HUNYUAN_CHATSTD("hunyuan-chatstd"),
    HUNYUAN_CHATPRO("hunyuan-chatpro"),

    /**
     * 360智脑
     */
    Brain_360GPT_S2_V9("360GPT_S2_V9");


    private final String code;
}