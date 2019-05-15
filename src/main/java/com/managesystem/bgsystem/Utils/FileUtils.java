package com.managesystem.bgsystem.Utils;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.UUID;

/*
 * design all by zhichao zhang
 * 文件上传：图片上传（支持校验图片）
 * */
public class FileUtils {

    public static String saveImg(MultipartFile file, String savePath) {
        String realFileName = file.getOriginalFilename();
        String backPath = "";
        String suffix = realFileName.substring(realFileName.lastIndexOf(".") + 1).toLowerCase();
/**
 * 文件大小拦截，不能超过20M
 */
        if (file.getSize() > 1024 * 1024 * 20 || !suffix.matches("^.*(jpg|png|gif|jpeg)$")) {
            return backPath;
        } else {
            try {
                InputStream inputStream = file.getInputStream();
                BufferedImage bufferedImage = ImageIO.read(inputStream);
                int width = bufferedImage.getWidth();
                int height = bufferedImage.getHeight();
                if (width == 0 || height == 0) {
                    return " ";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return " ";
            }
            File fileSave = new File(savePath);
            if (!fileSave.exists()) {
                fileSave.mkdirs();
            }
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            savePath += uuid + realFileName;
            File saveFile = new File(savePath);
            try {
                file.transferTo(saveFile);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
        return savePath;
    }
}

