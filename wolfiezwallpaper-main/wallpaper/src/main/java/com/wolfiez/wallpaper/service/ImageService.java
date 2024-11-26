package com.wolfiez.wallpaper.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Base64;


/**
 * Service for handling image-related operations such as encoding, decoding,
 * and validating image files.
 *
 * @author luis
 * @version 1.0
 * @since 25-11-2024
 */
@Service
public class ImageService {


    /**
     * Encodes a MultipartFile to a Base64 encoded string.
     *
     * @param file MultipartFile to be encoded
     * @return Base64 encoded string representation of the image
     * @throws IOException If there's an error reading the file
     */

    public String encodeImage(MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            byte[] imageBytes = file.getBytes();
            return Base64.getEncoder().encodeToString(imageBytes);
        }
        return null;
    }


    /**
     * Decodes a Base64 encoded image string back to byte array.
     *
     * @param base64Image Base64 encoded image string
     * @return Byte array representing the decoded image
     */
    public byte[] decodeImage(String base64Image) {
        if (base64Image != null && !base64Image.isEmpty()) {
            return Base64.getDecoder().decode(base64Image);
        }
        return null;
    }


    /**
     * Validates if the uploaded file is a valid image file.
     *
     * @param file MultipartFile to be validated
     * @return true if file is a valid image (JPEG, PNG, JPG), false otherwise
     */
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


    /**
     * Processes an image file for storage by validating and encoding it.
     *
     * @param file MultipartFile to be processed
     * @return Base64 encoded string of the processed image
     * @throws IOException If there's an error reading the file
     * @throws IllegalArgumentException If file is invalid or exceeds size limit
     */
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
