package io.luowei.sdk.executor.parameter;

/**
 * 参数处理器
 *
 * author：luowei
 */
public interface ParameterHandler<T> {

    T getParameterObject(CompletionRequest completionRequest);

}
