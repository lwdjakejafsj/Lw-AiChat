package io.luowei.sdk.executor.model.ali.valobj;

import lombok.Data;

import java.util.List;

@Data
public class AliCompletionResponse {

    /**
     * token使用情况
     */
    private Usage usage;

    /**
     * 响应内容
     */
    private Output output;

    /**
     * 本次请求的系统唯一码
     */
    private String request_id;

    /**
     * 响应内容主体
     *
     * @author Vanffer
     */
    @Data
    public static class Output {
        /**
         * 本次请求的响应内容
         */
        private String text;


        /**
         * 兼容openAi格式的响应内容
         * 入参result_format=message时候的返回值
         */
        private List<Choice> choices;


        /**
         * 结束原因
         * 正在生成时为null;
         * 生成结束时如果由于停止token导致则为stop;
         * 生成结束时如果因为生成长度过长导致则为length。
         */
        private String finish_reason;


    }

    /**
     * 兼容openAi格式的响应内容
     */
    @Data
    public static class Choice {
        /**
         * 结束原因，同上
         */
        private String finish_reason;

        /**
         * 响应消息主体
         */
        private Message message;
    }

    /**
     * 本次对话 token使用情况
     *
     * @author Vanffer
     */
    @Data
    public static class Usage {
        /**
         * 本次请求算法输出内容的 token 数目
         */
        private Integer output_tokens;

        /**
         * 本次请求输入内容的 token 数目
         */
        private Integer input_tokens;

        private Integer total_tokens;
    }

}
