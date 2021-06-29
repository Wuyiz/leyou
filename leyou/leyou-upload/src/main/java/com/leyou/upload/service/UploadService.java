package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author: suhai
 * @create: 2020-10-08 17:43
 */
@Service
public class UploadService {
    @Autowired
    private FastFileStorageClient storageClient;
    @Autowired
    private ThumbImageConfig thumbImageConfig;

    //image/gif ：gif图片格式
    //image/jpeg ：jpg图片格式
    //image/png：png图片格式
    private static final List<String> CONTENT_TYPES =
            Arrays.asList("image/gif", "image/jpeg", "image/png");
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);
    private static final String URL = "http://image.leyou.com/";

    /**
     * 上传图片
     * @param file
     * @return
     */
    public String uploadImage(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        //校验文件类型
        String contentType = file.getContentType();
        if (!CONTENT_TYPES.contains(contentType)) {
            LOGGER.info("文件类型不合法: {}", originalFilename);
            return null;
        }
        try {
            //校验文件内容
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage == null) {
                LOGGER.info("文件内容不合法：{}", originalFilename);
                return null;
            }
            //保存到本地服务器
            file.transferTo(new File("E:\\Files\\Development\\FullStack\\leyou\\image\\" + originalFilename));

            //保存到远程服务器
            //String suffix = StringUtils.substringAfterLast(originalFilename, ".");
            //StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), suffix, null);

            //返回url，进行回显
            return URL + originalFilename;
            //return URL + storePath.getFullPath();
        } catch (IOException e) {
            LOGGER.info("服务器内部错误： {}", originalFilename);
            e.printStackTrace();
        }
        return null;
    }
}

