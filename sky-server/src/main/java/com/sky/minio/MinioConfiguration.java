package com.sky.minio;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableConfigurationProperties(MinioProperties.class)
@ConfigurationPropertiesScan("com.sky.minio")//放properties类所在的包名
@ConditionalOnProperty(name = "minio.endpoint")//到yaml文件中找endpoint这个属性，不存在的话不执行该操作，因为webapp模块不需要用到minio
//不需要在yaml文件中设置这些属性，所以要进行排除，不然会报错
public class MinioConfiguration {
    @Autowired
    private MinioProperties minioProperties;
    //将minioclient对象放到ioc容器中
    @Bean
    public MinioClient MinioClient(){
        return MinioClient.builder().endpoint(minioProperties.getEndpoint()).credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey()).build();
    }
}
