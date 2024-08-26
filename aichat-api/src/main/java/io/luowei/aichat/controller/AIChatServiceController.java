package io.luowei.aichat.controller;

import com.alibaba.fastjson.JSON;
import io.luowei.aichat.common.annotation.AccessInterceptor;
import io.luowei.aichat.common.constants.Constants;
import io.luowei.aichat.common.exception.ChatGptException;
import io.luowei.aichat.model.aichat.ChatGPTRequestDTO;
import io.luowei.aichat.model.aichat.ChatProcessAggregate;
import io.luowei.aichat.model.aichat.MessageEntity;
import io.luowei.aichat.service.auth.IAuthService;
import io.luowei.aichat.service.aichat.IChatService;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1")
public class AIChatServiceController {

    @Resource
    private IChatService chatService;

    @Resource
    private IAuthService authService;


    @Timed(value = "completions_stream", description = "问答接口")
    @AccessInterceptor(key = "token",fallbackMethod = "aiErr",permitsPerSecond = 2.0d, blacklistCount = 10)
    @PostMapping("/chat/completions")
    public ResponseBodyEmitter completionsStream(@RequestBody ChatGPTRequestDTO request
                                                , @RequestHeader("Authorization") String token
                                                , HttpServletResponse response) {
        log.info("流式问答请求开始，使用模型：{} 请求信息：{}", request.getModel(), JSON.toJSONString(request.getMessages()));

        try {
            // 基础配置；流式输出(sse)、编码、禁用缓存
            response.setContentType("text/event-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache");

            // 对过期token进行校验
            ResponseBodyEmitter emitter = new ResponseBodyEmitter(3 * 60 * 1000L);
            boolean success = authService.checkToken(token);
            if (!success) {
                try {
                    emitter.send(Constants.ResponseCode.TOKEN_ERROR.getCode());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                emitter.complete();
                return emitter;
            }

            String openid = authService.openId(token);
            log.info("流式问答请求处理，openid:{} 请求模型:{}", openid, request.getModel());

            // 构建参数
            ChatProcessAggregate aggregate = ChatProcessAggregate.builder()
                    .openId(openid)
                    .model(request.getModel())
                    .messages(request.getMessages().stream()
                            .map(entity -> MessageEntity.builder()
                                    .role(entity.getRole())
                                    .content(entity.getContent())
                                    .name(entity.getName())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();

            return chatService.completions(emitter,aggregate);
        } catch (Exception e) {
            log.error("流式应答，请求模型：{} 发生异常", request.getModel(), e);
            throw new ChatGptException(e.getMessage());
        }
    }

    public ResponseBodyEmitter aiErr() {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(3 * 60 * 1000L);

        emitter.onCompletion(() -> {
            log.info("流式问答限流");
        });

        emitter.onError(throwable ->
                log.error("流式返回错误", throwable)
        );

        try {
            emitter.send(Constants.ResponseCode.RATE_LIMIT.getCode());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        emitter.complete();
        return emitter;
    }
}
