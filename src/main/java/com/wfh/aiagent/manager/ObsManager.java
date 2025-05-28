package com.wfh.aiagent.manager;


import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.ObsObject;
import com.obs.services.model.PutObjectResult;
import com.wfh.aiagent.config.OBSClientConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * Cos 对象存储操作
 *
 * @author wangfenghuan
 * @from 
 */
@Slf4j
@Component
public class ObsManager {

    @Resource
    private OBSClientConfig obsClientConfig;


    private static ObsClient obsClient;

    /**
     * 上传文件到华为云OBS
     * @param file
     * @return
     * @throws IOException
     */
    public String upload(File file, String filePath) throws IOException {
        // 创建ObsClient实例
        obsClient = new ObsClient(obsClientConfig.getAccessKeyId(), obsClientConfig.getSecretAccessKey(), obsClientConfig.getEndpoint());
        InputStream inputStream = Files.newInputStream(file.toPath());
        try {
            // 上传文件到 OBS
            obsClient.putObject(obsClientConfig.getBucketName(), filePath, inputStream);
            // 文件访问路径
            String url = "https://" + obsClientConfig.getBucketName() + "." + obsClientConfig.getEndpoint() + "/" + filePath;
            // 把上传到OBS的路径返回
            return url;
        } catch (ObsException e) {
            // 处理异常
            throw new RuntimeException("上传文件失败: " + e.getMessage(), e);
        } finally {
            // 关闭obsClient
            if (obsClient != null) {
                try {
                    obsClient.close();
                    inputStream.close();
                } catch (Exception e) {
                    // 忽略关闭时可能产生的异常
                }
            }
        }
    }

    public String downLoad(String filePath) throws IOException {
        InputStream input = null;
        ByteArrayOutputStream bos = null;
        String file = null;
        try {
            // 创建ObsClient实例
            obsClient = new ObsClient(obsClientConfig.getAccessKeyId(), obsClientConfig.getSecretAccessKey(), obsClientConfig.getEndpoint());
            // 流式下载
            ObsObject obsObject = obsClient.getObject(obsClientConfig.getBucketName(), filePath);
            // 读取对象内容
            log.info("Object content:");
            input = obsObject.getObjectContent();
            byte[] b = new byte[1024 * 1024];
            bos = new ByteArrayOutputStream();
            int len;
            while ((len = input.read(b)) != -1) {
                bos.write(b, 0, len);
            }
            log.info("getObjectContent successfully");
            file = new String(bos.toByteArray());
        } catch (ObsException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            bos.close();
            input.close();
            obsClient.close();
        }

        return file;
    }

    /**
     * 上传图片
     * @param file
     * @param filePath
     * @return
     * @throws IOException
     */
    public PutObjectResult uploadWithInformation(File file, String filePath) throws IOException {
        // 创建ObsClient实例
        obsClient = new ObsClient(obsClientConfig.getAccessKeyId(), obsClientConfig.getSecretAccessKey(), obsClientConfig.getEndpoint());
        InputStream inputStream = Files.newInputStream(file.toPath());
        try {
            // 上传文件到 OBS
            PutObjectResult putObjectResult = obsClient.putObject(obsClientConfig.getBucketName(), filePath, inputStream);

            // 文件访问路径
            String url = "https://" + obsClientConfig.getBucketName() + "." + obsClientConfig.getEndpoint() + "/" + filePath;
            // 把上传到OBS的路径返回
            return putObjectResult;
        } catch (ObsException e) {
            // 处理异常
            throw new RuntimeException("上传文件失败: " + e.getMessage(), e);
        } finally {
            // 关闭obsClient
            if (obsClient != null) {
                try {
                    obsClient.close();
                    inputStream.close();
                } catch (Exception e) {
                    // 忽略关闭时可能产生的异常
                }
            }
        }
    }

    /**
     * 删除对象
     * @param objName
     */
    public void delObject(String objName) throws IOException {
        // 创建ObsClient实例
        obsClient = new ObsClient(obsClientConfig.getAccessKeyId(), obsClientConfig.getSecretAccessKey(), obsClientConfig.getEndpoint());
        try {
            obsClient.deleteObject(obsClientConfig.getBucketName(), objName);
        } catch (ObsException e) {
            throw new RuntimeException(e);
        } finally {
            obsClient.close();
        }
    }


}
