package io.luowei.aichat.service.aichat.rule;

import io.luowei.aichat.model.aichat.ChatProcessAggregate;
import io.luowei.aichat.model.aichat.rule.RuleLogicEntity;

// 策略 + 工厂 + 模板
public interface ILogicFilter<T> {

    RuleLogicEntity<ChatProcessAggregate> filter(ChatProcessAggregate chatProcess,T data)throws Exception;

}
