package com.ayushjainttn.bootcampproject.ecommerce.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    public String uploadImage(String path, MultipartFile imageFile, String userId);
    public String searchImage(String path, String userId);
}
