package io.luowei.aichat.service.auth;

import com.google.common.cache.Cache;
import io.jsonwebtoken.Claims;
import io.luowei.aichat.common.utils.JwtUtil;
import io.luowei.aichat.dao.repository.IAuthRepository;
import io.luowei.aichat.model.auth.AuthStateEntity;
import io.luowei.aichat.model.auth.AuthTypeVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class IAuthServiceImpl extends AbstractAuthService{

    @Resource
    private Cache<String,String> codeCache;

    @Resource
    private IAuthRepository repository;

    @Override
    protected AuthStateEntity checkCode(String code) {

//        String openId = codeCache.getIfPresent(code);
        // 获取验证码校验
        String openId = repository.getCodeUserOpenId(code);

        if (StringUtils.isBlank(openId)){
            log.info("鉴权，用户收入的验证码不存在 {}", code);
            return AuthStateEntity.builder()
                    .code(AuthTypeVO.A0001.getCode())
                    .info(AuthTypeVO.A0001.getInfo())
                    .build();
        }

        // 移除缓存key值
//        codeCache.invalidate(openId);
//        codeCache.invalidate(code);
        repository.removeCodeByOpenId(code, openId);

        return AuthStateEntity.builder()
                .code(AuthTypeVO.A0000.getCode())
                .info(AuthTypeVO.A0000.getInfo())
                .openId(openId)
                .build();
    }

    @Override
    public boolean checkToken(String token) {
        return JwtUtil.isVerify(token);
    }

    @Override
    public String openId(String token) {
        Claims claims = JwtUtil.decode(token);
        return claims.get("openId").toString();
    }
}
