package io.luowei.aichat.model.aichat;

import io.luowei.sdk.executor.common.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatGPTRequestDTO {
    /** 默认模型 */
    private String model = Model.CHATGLM_TURBO.getCode();
    /** 问题描述 */
    private List<MessageEntity> messages;
}