package io.luowei.sdk.executor.model.ali.valobj;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.luowei.sdk.executor.common.Model;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliCompletionRequest {

    @Getter
    @Setter
    private String model = Model.QWEN_TURBO.getCode();

    /**
     * 对话消息
     */
    private Input input;

    /**
     * 可选参数，均为非必须
     */
    private Parameters parameters;


    /**
     * 通义千问 对话消息
     *
     * @author Vanffer
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Input {
        /**
         * 用户当前输入的期望模型执行指令，支持中英文
         */
        private String prompt;

        /**
         * 用户与模型的对话历史，对话接口未来都会有message传输
         */
        private List<Message> messages;

    }

    @Slf4j
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Parameters {
        /**
         * "text"表示旧版本的text
         * "message"表示兼容openai的message
         * 非必须参数，默认设置"text"
         */
        @JsonProperty("result_format")
        private String resultFormat = "text";

        /**
         * 生成时，随机数的种子，用于控制模型生成的随机性。
         * 如果使用相同的种子，每次运行生成的结果都将相同；
         * 当需要复现模型的生成结果时，可以使用相同的种子。
         * seed参数支持无符号64位整数类型。默认值 1234。
         * 非必须参数
         */
        private Integer seed;

        /**
         * 用于限制模型生成token的数量
         * max_tokens设置的是生成上限，并不表示一定会生成这么多的token数量。
         * 其中qwen-turbo 最大值和默认值均为1500，
         * qwen-max 和 qwen-plus最大值和默认值均为2048。
         */
        @JsonProperty("max_tokens")
        private Integer maxTokens;

        /**
         * 生成时，核采样方法的概率阈值。
         * 默认值为0.8
         */
        @JsonProperty("top_p")
        private Float topP;

        /**
         * 生成时，采样候选集的大小
         * 取值越大，生成的随机性越高；取值越小，生成的确定性越高
         * 例如，取值为50时，仅将单次生成中得分最高的50个token组成随机采样的候选集。
         * 如果top_k参数为空或者top_k的值大于100，表示不启用top_k策略，此时仅有top_p策略生效
         * 默认是空
         */
        @JsonProperty("top_k")
        private Float topK;

        /**
         * 用于控制模型生成时的重复度
         * 提高repetition_penalty时可以降低模型生成的重复度
         * 1.0表示不做惩罚。默认为1.1
         */
        @JsonProperty("repetition_penalty")
        private Float repetitionPenalty;

        /**
         * 用于控制随机性和多样性的程度。
         */
        private Float temperature;

        /**
         * 用于控制遇到什么词语时停止
         */
        private List<String> stop;

        /**
         * 生成时，是否参考搜索的结果
         * 打开搜索并不意味着一定会使用搜索结果；
         * 如果打开搜索，模型会将搜索结果作为prompt，
         * 进而“自行判断”是否生成结合搜索结果的文本，默认为false
         */
        @JsonProperty("enable_search")
        private Boolean enableSearch;

        /**
         * 用于控制流式输出模式，默认False，即后面内容会包含已经输出的内容；
         * 设置为True，将开启增量输出模式，后面输出不会包含已经输出的内容，您需要自行拼接整体输出
         */
        @JsonProperty("incremental_output")
        private Boolean incrementalOutput = true;
    }

}
