package io.luowei.sdk.executor.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {

        SYSTEM("system"),
        USER("user"),
        ASSISTANT("assistant"),
        MODEL("model"),


        USER_INFO("user_info"),
        BOT_INFO("bot_info"),
        BOT_NAME("bot_name"),
        USER_NAME("user_name"),
        ;

        private final String code;

}