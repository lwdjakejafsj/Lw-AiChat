package io.luowei.aichat.service.file;

import cn.hutool.core.date.DateTime;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService{

    @Value("${aliyun.oss.file.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.file.keyid}")
    private String accessKeyId;

    @Value("${aliyun.oss.file.keysecret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.file.bucketname}")
    private String bucketName;

    @Override
    public List<String> getAllBucket() {
        return null;
    }

    @Override
    public String getUrl(String bucketName, String objectName) {
        return null;
    }

    @Override
    public String uploadFile(MultipartFile uploadFile, String bucket, String objectName) {

        // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
        // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        //        String filePath = "D:\\localpath\\examplefile.txt";

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            //获取上传文件的输入流
            InputStream inputStream = uploadFile.getInputStream();

            //获取文件名称
            String FileName = uploadFile.getOriginalFilename();

            //在文件名称里添加唯一随机的值
            String uuid = UUID.randomUUID().toString();

            FileName = uuid + FileName;

            //把文件按照日期进行分类，这里使用joda-time工具类
            String datePath = new DateTime().toString("yyyy/MM/dd");
            FileName = datePath +"/"+ FileName;

            /** 创建PutObject请求。
             * 第一个参数，Bucket名称
             * 第二个参数，上传到oss文件路径和文件名称
             */
            ossClient.putObject(bucketName, FileName, inputStream);

            //把上传的阿里云路径返回
            String url = "https://" + bucketName + "." + endpoint + "/" + FileName;

            return url;

        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return null;
    }
}
