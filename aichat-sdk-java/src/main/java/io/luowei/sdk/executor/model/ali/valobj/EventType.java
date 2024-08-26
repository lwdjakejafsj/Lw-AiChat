package io.luowei.sdk.executor.model.ali.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Ali 消息类型
 *
 * author：luowei
 */
@Getter
@AllArgsConstructor
public enum EventType {

    STOP("stop", "回答结束时的结束标识"),
    CONTINUE("null", "生成过程中"),
    Length("length", "token长度超出限制"),

    ;
    private final String code;
    private final String info;

}
