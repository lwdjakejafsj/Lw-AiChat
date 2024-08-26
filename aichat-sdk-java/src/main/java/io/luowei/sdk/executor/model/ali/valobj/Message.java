package io.luowei.sdk.executor.model.ali.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    /**
     * 消息的角色
     */
    private String role;

    /**
     * 对话内容
     */
    private String content;
}