package io.luowei.aichat.controller;

import io.luowei.aichat.common.constants.Constants;
import io.luowei.aichat.controller.dto.SignInformationDTO;
import io.luowei.aichat.model.Response;
import io.luowei.aichat.model.sign.SignInformationEntity;
import io.luowei.aichat.model.sign.SignTypeVO;
import io.luowei.aichat.service.auth.IAuthService;
import io.luowei.aichat.service.sign.ISignService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/sign")
public class SignController {

    @Resource
    private ISignService signService;

    @Resource
    private IAuthService authService;

    @PostMapping("/doSign")
    public Response<String> doSign(@RequestHeader("Authorization") String token, @RequestParam(value = "dateStr",required = false)String dateStr) {
        // 校验token
        boolean success = authService.checkToken(token);
        if (!success) {
            return Response.<String>builder()
                    .code(Constants.ResponseCode.TOKEN_ERROR.getCode())
                    .info(Constants.ResponseCode.TOKEN_ERROR.getInfo())
                    .build();
        }

        String openid = authService.openId(token);

        SignTypeVO sign = signService.sign(openid, dateStr);

        if (SignTypeVO.SIGN.getCode().equals(sign.getCode())) {
            return Response.<String>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .build();
        } else if (SignTypeVO.REPEAT_SIGN.getCode().equals(sign.getCode())) {
            return Response.<String>builder()
                    .code(Constants.ResponseCode.REPEAT_SIGN.getCode())
                    .info(Constants.ResponseCode.REPEAT_SIGN.getInfo())
                    .build();
        }
        return Response.<String>builder()
                .code(Constants.ResponseCode.SIGN_ERROR.getCode())
                .info(Constants.ResponseCode.SIGN_ERROR.getInfo())
                .build();
    }

    @PostMapping("/getSignRecords")
    public Response<List<String>> getSignRecords(@RequestHeader("Authorization") String token, @RequestParam(required = false)String dateStr) {
        // 校验token
        boolean success = authService.checkToken(token);
        if (!success) {
            return Response.<List<String>>builder()
                    .code(Constants.ResponseCode.TOKEN_ERROR.getCode())
                    .info(Constants.ResponseCode.TOKEN_ERROR.getInfo())
                    .build();
        }

        String openid = authService.openId(token);

        List<String> record = signService.getSignRecords(openid, dateStr);

        return Response.<List<String>>builder()
                .code(Constants.ResponseCode.SUCCESS.getCode())
                .info(Constants.ResponseCode.SUCCESS.getInfo())
                .data(record)
                .build();
    }


    @GetMapping("/getSignInformation")
    public Response<SignInformationDTO> getSignInformation(@RequestHeader("Authorization") String token) {

        // 校验token
        boolean success = authService.checkToken(token);
        if (!success) {
            return Response.<SignInformationDTO>builder()
                    .code(Constants.ResponseCode.TOKEN_ERROR.getCode())
                    .info(Constants.ResponseCode.TOKEN_ERROR.getInfo())
                    .build();
        }

        String openid = authService.openId(token);
        // 总签到数从useraccount中获得
        // 连续签到数
            // 根据今天的日期作为最后时间去表中查询，有则代表今天签到过
            // 假如根据今天没有查询到，那在根据昨天去查询，有则代表没签到
            // 假如都没有，代表连续签到断了，返回0、
        // 今天是否签到，根据连续签到其实就可以判断出来

        SignInformationEntity signInformationEntity = signService.getSignInformation(openid);


        SignInformationDTO signInformationDTO = SignInformationDTO.builder()
                .continuousSignCount(signInformationEntity.getContinuousSignCount())
                .isSign(signInformationEntity.getIsSign())
                .totalSignCount(signInformationEntity.getTotalSignCount())
                .build();


        return Response.<SignInformationDTO>builder()
                .code(Constants.ResponseCode.SUCCESS.getCode())
                .info(Constants.ResponseCode.SUCCESS.getInfo())
                .data(signInformationDTO)
                .build();
    }

}
