package com.sky.service.impl;

import com.sky.minio.MinioProperties;
import com.sky.service.FileService;
import io.minio.*;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author: FANGYUN
 * @date: 2024-07-25 23:17
 * @description:
 */
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private MinioClient minioClient;
    @Autowired
    private MinioProperties minioProperties;

    @Override
    public String upload(MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        boolean b = false;

        b = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioProperties.getBucketName()).build());
        if (!b) {
            //创建桶
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.getBucketName()).build());
        }
        //设置桶的访问权限
        minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(minioProperties.getBucketName()).config(createBucketPolicyConfig(minioProperties.getBucketName())).build());

        //文件传到minio中对应的对象名称
        String filename = new SimpleDateFormat("yyyyMMdd").format(new Date()) +
                "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        // minioClient.uploadObject() 只能上传本地磁盘的文件
        // 将流数据上传到minio的对象中
        minioClient.putObject(PutObjectArgs.builder().bucket(minioProperties.getBucketName()).object(filename).stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build());//流数据一部分一部分上传，知道size，partsize设为-1，系统自动匹配合适的数据部分尺寸
        //getcontenttype获取文件的类型，使得传入minio的对象是同种类型


        //放到minio中对象的url
        String url = minioProperties.getEndpoint() + "/" + minioProperties.getBucketName() + "/" + filename;
        //String url1 = String.join("/", minioProperties.getEndpoint(), minioProperties.getBucketName(), filename);
        return url;

    }

    /**
     * 用json字符串设置桶的权限
     *
     * @param bucketName
     * @return
     */
    private String createBucketPolicyConfig(String bucketName) {

        return """
                {
                  "Statement" : [ {
                    "Action" : "s3:GetObject",
                    "Effect" : "Allow",
                    "Principal" : "*",
                    "Resource" : "arn:aws:s3:::%s/*"
                  } ],
                  "Version" : "2012-10-17"
                }
                """.formatted(bucketName);
        //允许所有人读这个桶下面的所有资源，默认只允许自己写
    }
}
