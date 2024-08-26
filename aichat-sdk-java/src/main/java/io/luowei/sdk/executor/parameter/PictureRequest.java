package io.luowei.sdk.executor.parameter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 图片和图片的描述
 */
@Slf4j
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class PictureRequest extends PictureContentEnum {

    private String model;


    private List<Text> messages;

    /*
     * 控制温度【随机性】；0到2之间。较高的值(如0.8)将使输出更加随机，而较低的值(如0.2)将使输出更加集中和确定
     */
    @Builder.Default
    private double temperature = 0.5;

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



    @Data
    @Builder
    @AllArgsConstructor
    public static class Text{

        private Role role;

        /**
         * contentType is image,picture for base64
         * contentType is text,content for prompt
         */
        private String content;

        /**
         * @see PictureContentEnum
         */
        private String contentType;
    }

    @Getter
    @AllArgsConstructor
    public enum Role {

        SYSTEM("system"),
        USER("user"),
        ASSISTANT("assistant"),
        ;

        private final String code;

    }

}
