package io.luowei.aichat.service.aichat;

import io.luowei.aichat.model.aichat.ChatProcessAggregate;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

public interface IChatService {

    ResponseBodyEmitter completions(ResponseBodyEmitter emitter,ChatProcessAggregate aggregate);

}
