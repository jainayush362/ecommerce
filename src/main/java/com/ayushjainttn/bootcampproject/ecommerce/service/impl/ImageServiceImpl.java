package com.ayushjainttn.bootcampproject.ecommerce.service.impl;

import com.ayushjainttn.bootcampproject.ecommerce.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
public class ImageServiceImpl implements ImageService {
    /**
     * Method to upload images
     * @param path
     * @param imageFile
     * @param userId
     * @return uploaded imagePath
     */
    @Override
    public String uploadImage(String path, MultipartFile imageFile, String userId) {
        log.info("----inside uploadImage() method----");
        String existingImage = searchImage(path,userId);
        if (existingImage!=null){
            try {
                log.info("----deleting existing image----");
                Files.delete(Path.of(path+File.separator+existingImage));
            } catch (IOException e) {
                log.error("----deleting image error----");
                throw new RuntimeException(e.getMessage());
            }
        }
        String fileName = imageFile.getOriginalFilename();
        String newFileName = userId.concat(fileName.substring(fileName.lastIndexOf(".")));
        String filePath = path+File.separator+newFileName;
        File file = new File(path);
        if (!file.exists()) {
            log.info("----creating directories required where image will store----");
            file.mkdirs();
        }

        try (InputStream input = imageFile.getInputStream()) {
            try {
                // If it's an image then ImageIO can read the input image file (only BMP, GIF, JPG and PNG are recognized).
                ImageIO.read(input).toString();
                Files.copy(imageFile.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
                log.info("----image uploaded----");
            } catch (Exception e) {
                log.error("----unsupported image type----");
                throw new RuntimeException("Unsupported image type! Only BMP, JPEG, JPG and PNG are supported.");
            }
        } catch (IOException e) {
            log.error("----image upload error----");
            throw new RuntimeException(e.getMessage());
        }
        log.info("----image upload path returned. method executed success----");
        return filePath;
    }

    /**
     * Method to search for an image
     * @param path
     * @param userId
     * @return searched imagePath
     */
    @Override
    public String searchImage(String path, String userId){
        log.info("----inside searchImage() method----");
        File dir = new File(path);
        String[] files = dir.list();
        if(files==null) {
            log.info("----no files found. method executed success----");
            return null;
        }
        for (int i = 0; i < files.length; i++) {
            String[] name = files[i].toString().split("\\.");
            if(name[0].equals(userId)) {
                log.info("----files found returned. method executed success----");
                return files[i];
            }
        }
        log.info("----no files found. method executed success----");
        return null;
    }
}
