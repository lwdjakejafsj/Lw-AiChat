package io.luowei.aichat.service.auth;

import io.luowei.aichat.model.auth.AuthStateEntity;

public interface IAuthService {

    /**
     * 登录验证
     * author: luowei
     * date:
     */
    AuthStateEntity doLogin(String code);

    boolean checkToken(String token);

    String openId(String token);
}
