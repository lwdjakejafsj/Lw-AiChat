package io.luowei.aichat.controller;

import io.luowei.aichat.common.constants.Constants;
import io.luowei.aichat.controller.dto.UserAccountDTO;
import io.luowei.aichat.model.Response;
import io.luowei.aichat.model.user.UserAccountEntity;
import io.luowei.aichat.service.auth.IAuthService;
import io.luowei.aichat.service.user.IUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private IUserService userService;

    @Resource
    private IAuthService authService;

    @GetMapping("/getUserInfo")
    public Response<UserAccountDTO> getUserInfo(@RequestHeader("Authorization") String token) {
        //校验token
        boolean success = authService.checkToken(token);
        if (!success) {
            return Response.<UserAccountDTO>builder()
                    .code(Constants.ResponseCode.TOKEN_ERROR.getCode())
                    .info(Constants.ResponseCode.TOKEN_ERROR.getInfo())
                    .build();
        }

        //解析token
        String openid = authService.openId(token);

        UserAccountEntity userInfo = userService.getUserInfo(openid);

        UserAccountDTO userAccountDTO = UserAccountDTO.builder()
                .avatar(userInfo.getAvatar())
                .openid(userInfo.getOpenid())
                .integral(userInfo.getIntegral())
                .totalQuota(userInfo.getTotalQuota())
                .surplusQuota(userInfo.getSurplusQuota())
                .userName(userInfo.getUserName())
                .build();

        return Response.<UserAccountDTO>builder()
                .code(Constants.ResponseCode.SUCCESS.getCode())
                .info(Constants.ResponseCode.SUCCESS.getInfo())
                .data(userAccountDTO)
                .build();

    }

    @PostMapping("/updateUserInfo")
    public Response<String> updateUserInfo(@RequestHeader("Authorization") String token, @RequestBody UserAccountDTO userAccountDTO) {

        //校验token
        boolean success = authService.checkToken(token);
        if (!success) {
            return Response.<String>builder()
                    .code(Constants.ResponseCode.TOKEN_ERROR.getCode())
                    .info(Constants.ResponseCode.TOKEN_ERROR.getInfo())
                    .build();
        }

        //解析token
        String openid = authService.openId(token);

        UserAccountEntity entity = UserAccountEntity.builder()
                .openid(openid)
                .userName(userAccountDTO.getUserName())
                .avatar(userAccountDTO.getAvatar())
                .build();

        userService.updateUserInfo(entity);

        return Response.<String>builder()
                .code(Constants.ResponseCode.SUCCESS.getCode())
                .info(Constants.ResponseCode.SUCCESS.getInfo())
                .build();
    }

}
