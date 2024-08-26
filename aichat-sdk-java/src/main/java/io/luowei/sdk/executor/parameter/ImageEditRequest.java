package io.luowei.sdk.executor.parameter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.Serializable;

/**
 * 修改图片
 *
 * author：luowei
 */
@Slf4j
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class ImageEditRequest extends ImageEnum implements Serializable {
    /** 模型 */
    private String model = ImageRequest.Model.DALL_E_2.getCode();
    /** 图片*/
    @NonNull
    private File image;
    private File mask;
    /**
     * 问题描述
     */
    @NonNull
    private String prompt;
    /**
     * 为每个提示生成的完成次数
     */
    @Builder.Default
    private Integer n = 1;
    /**
     * 图片大小
     */
    @Builder.Default
    private String size = Size.size_256.getCode();
    /**
     * 图片格式化方式；URL、B64_JSON
     */
    @JsonProperty("response_format")
    @Builder.Default
    private String responseFormat = ResponseFormat.URL.getCode();
    @Setter
    private String user;

    @Getter
    @AllArgsConstructor
    public enum Model {
        DALL_E_2("dall-e-2"),
        DALL_E_3("dall-e-3"),
        STABLE_DIFFUSION_XL("Stable_Diffusion_XL"),
        ;
        private final String code;
    }
}
