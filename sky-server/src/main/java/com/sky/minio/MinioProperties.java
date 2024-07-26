package com.sky.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "minio")//有这个注解不需要加component
@Component
//自动到yaml文件中找对应属性
public class MinioProperties {
    private String endpoint;
    private String accessKey;//下划线或者横杠会自动映射到驼峰命名
    private String secretKey;
    private String bucketName;
}
