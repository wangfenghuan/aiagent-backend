package com.wfh.aiagent.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 华为云对象存储客户端
 *
 * @author wangfenghuan
 * @from 
 */
@Configuration
@ConfigurationProperties(prefix = "com.obs")
@Data
public class OBSClientConfig {

    /**
     * accessKey
     */
    private String accessKeyId;

    /**
     * secretKey
     */
    private String secretAccessKey;

    /**
     * 区域
     */
    private String endpoint;

    /**
     * 桶名
     */
    private String bucketName;

}