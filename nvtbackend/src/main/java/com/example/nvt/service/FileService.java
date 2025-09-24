package com.example.nvt.service;

import com.example.nvt.exceptions.InvalidInputException;
import com.example.nvt.model.HouseholdRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    public final String uploadDirUsers = "files/users";
    public final String uploadDirHouseholdRequests = "files/requests/households";
    public final String uploadDirRealestates = "files/realestates";


    public String saveProfileImg(MultipartFile file, Long userId) throws IOException {

        String contentType = file.getContentType();
        if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
            throw new IllegalArgumentException("Only JPEG or PNG images are allowed");
        }

        Path uploadPath = Paths.get(uploadDirUsers, userId.toString());
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IllegalArgumentException("Invalid file name");
        }
        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);

        // Resize the image
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        BufferedImage resizedImage;

        if(originalImage.getWidth() < 128 || originalImage.getHeight() < 128) throw new InvalidInputException("Image is too small");
        if(originalImage.getWidth() >= 256 || originalImage.getHeight() >= 256) resizedImage = resizeImage(originalImage, 256, 256);
        else resizedImage = originalImage;

        BufferedImage resizedSmallImage = resizeImage(originalImage, 100, 100);


        Path filePath = uploadPath.resolve(fileName);
        Path filePathSmall = uploadPath.resolve("small_" + fileName);
        ImageIO.write(resizedImage, fileExtension, filePath.toFile());
        ImageIO.write(resizedSmallImage, fileExtension, filePathSmall.toFile());

        return fileName;
    }




    public String saveHouseholdRequestFile(MultipartFile file, Long requestId) throws IOException {

        String contentType = file.getContentType();
        if (contentType == null ||
                !(contentType.equals("application/pdf") ||
                        contentType.equals("image/jpeg") ||
                        contentType.equals("image/png"))) {
            throw new InvalidInputException("Only PDF, JPEG, and PNG files are allowed.");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        Path uploadPath = Paths.get(uploadDirHouseholdRequests, requestId.toString());
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(uniqueFilename);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFilename;
    }






    BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        return Scalr.resize(originalImage, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, targetWidth, targetHeight, Scalr.OP_ANTIALIAS);
    }




    public Resource getFileResource(String filePath) {

        Path path = Paths.get(filePath);
        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return resource;

    }

    public MediaType getFileMediaType(String filePath) {

        String ext = FilenameUtils.getExtension(filePath);
        MediaType mediaType = null;
        switch (ext) {
            case "jpg":
                mediaType = MediaType.IMAGE_JPEG;
                break;
            case "png":
                mediaType = MediaType.IMAGE_PNG;
                break;
            case "jpeg":
                mediaType = MediaType.IMAGE_JPEG;
                break;
            default:
                throw new InvalidInputException("Media type not recognized");
        }

        return mediaType;
    }



}
