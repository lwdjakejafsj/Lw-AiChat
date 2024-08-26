package io.luowei.aichat.service.file;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    /**
     * 列出所有桶
     */
    List<String> getAllBucket();
    /**
     * 获取文件路径
     */
    String getUrl(String bucketName,String objectName);

    /**
     * 上传文件
     */
    String uploadFile(MultipartFile uploadFile, String bucket, String objectName);

}
