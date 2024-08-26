package io.luowei.aichat.controller;

import io.luowei.aichat.common.constants.Constants;
import io.luowei.aichat.model.Response;
import io.luowei.aichat.service.auth.IAuthService;
import io.luowei.aichat.service.file.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/file")
public class FileController {

    @Resource
    private FileService fileServiceImpl;

    @Resource
    private IAuthService authService;

    @RequestMapping("/testGetAllBuckets")
    public String testGetAllBuckets() throws Exception {
        List<String> allBucket = fileServiceImpl.getAllBucket();
        return allBucket.get(0);
    }

    @RequestMapping("/getUrl")
    public String getUrl(String bucketName, String objectName) throws Exception {
        return fileServiceImpl.getUrl(bucketName, objectName);
    }

    /**
     * 上传文件
     */
    @RequestMapping("/upload")
    public Response upload(@RequestParam("avatar") MultipartFile uploadFile, @RequestHeader("Authorization") String token) throws Exception {

        //校验token
        boolean success = authService.checkToken(token);
        if (!success) {
            return Response.<String>builder()
                    .code(Constants.ResponseCode.TOKEN_ERROR.getCode())
                    .info(Constants.ResponseCode.TOKEN_ERROR.getInfo())
                    .build();
        }

        String url = fileServiceImpl.uploadFile(uploadFile, null, null);
        return Response.<String>builder()
                .code(Constants.ResponseCode.SUCCESS.getCode())
                .info(Constants.ResponseCode.SUCCESS.getInfo())
                .data(url)
                .build();
    }

}
