package io.luowei.sdk.executor.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 对话信息
 *
 * author：luowei
 */
@Data
public class ChatChoice implements Serializable {

    private static final long serialVersionUID = 802469482706186701L;

    private long index;
    /**
     * stream = true 请求参数里返回的属性是 delta
     */
    @JsonProperty("delta")
    private Message delta;
    /**
     * stream = false 请求参数里返回的属性是 delta
     */
    @JsonProperty("message")
    private Message message;

    @JsonProperty("finish_reason")
    private String finishReason;

}
