package com.wolfiez.wallpaper.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Base64;

@Service
public class ImageService {


    public String encodeImage(MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            byte[] imageBytes = file.getBytes();
            return Base64.getEncoder().encodeToString(imageBytes);
        }
        return null;
    }


    public byte[] decodeImage(String base64Image) {
        if (base64Image != null && !base64Image.isEmpty()) {
            return Base64.getDecoder().decode(base64Image);
        }
        return null;
    }


    public boolean isValidImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        return contentType != null && (
                contentType.equals("image/jpeg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/jpg")
        );
    }


    public String processImageForStorage(MultipartFile file) throws IOException {
        if (!isValidImageFile(file)) {
            throw new IllegalArgumentException("Invalid image file format. Only JPEG, JPG and PNG are allowed.");
        }


        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 5MB");
        }

        return encodeImage(file);
    }
}
