package io.luowei.aichat.service.wechat;

public interface WeChatValidateService {
    boolean checkSign(String signature, String timestamp, String nonce);
}
