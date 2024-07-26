package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.FileService;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author: FANGYUN
 * @date: 2024-07-25 22:01
 * @description:
 */

/**
 * 通用接口，上传图片到阿里云
 */
@RestController
@Slf4j
@RequestMapping("/admin/common/upload")
public class CommonController {
    @Autowired
    private FileService fileService;
    @PostMapping
    public Result<String> upload(MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {//上传file，响应体不用@ResponseBody，但是参数名要等于响应体中的名称
        log.info("上传图片：{}", file.getOriginalFilename());
        String url=fileService.upload(file);
        return Result.success(url);
    }

}
