package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.upload.config.UploadProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@EnableConfigurationProperties(UploadProperties.class)
public class UploadService {

    @Autowired
    private UploadProperties properties;

    @Autowired
    private FastFileStorageClient storageClient;

    private static final List<String> ALLOW_TYPES = Arrays.asList("image/jpeg","image/png","image/jpg");
    public String uploadImage(MultipartFile file) {
        try {
            //校验文件
            final String contentType = file.getContentType();
            if(!properties.getAllowTypes().contains(contentType)){
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            //校验文件内容
            final BufferedImage image = ImageIO.read(file.getInputStream());
            if(image == null){
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }

            //上传文件到FastDFS
//            String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+ 1);
            String extension = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            final StorePath storePath = storageClient.uploadImageAndCrtThumbImage(file.getInputStream(), file.getSize(), extension, null);

            //目标路径
//        this.getClass().getClassLoader().getResource().getFile()
//            File dest = new File("/Users/jwang/javaworkplace/project_all/leyou/upload/", file.getOriginalFilename());
//            //保存文件到本地
//            file.transferTo(dest);
            //返回路径
//            return "http://image.leyou.com/"+file.getOriginalFilename();
            String s = properties.getBaseUrl() + storePath.getFullPath();
            System.out.println(s);
            return s;
        } catch (IOException e) {
            log.error("[文件上传] 上传文件失败", e);
            throw new LyException(ExceptionEnum.UPLOAD_FILE_ERROR);
        }
    }
}
