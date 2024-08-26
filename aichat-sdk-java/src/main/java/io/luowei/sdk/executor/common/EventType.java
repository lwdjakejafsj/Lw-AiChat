package io.luowei.sdk.executor.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息类型
 *
 * author：luowei
 */
@Getter
@AllArgsConstructor
public enum EventType {

    /**
     * 智谱
     * author: luowei
     * date:
     */
    add("add", "增量"),
    finish("finish", "结束"),
    error("error", "错误"),
    interrupted("interrupted", "中断"),

    /**
     * 阿里
     * author: luowei
     * date:
     */
    STOP("stop", "回答结束时的结束标识"),
    CONTINUE("null", "生成过程中"),
    Length("length", "token长度超出限制"),

    ;
    private final String code;
    private final String info;

}
