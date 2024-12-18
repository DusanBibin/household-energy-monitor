package com.example.nvt.service;

import com.example.nvt.exceptions.InvalidInputException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class FileService {

    private final String uploadDir = "files";
    private final UserService userService;

    public String saveProfileImg(MultipartFile file, Long userId) throws IOException {

        String contentType = file.getContentType();
        if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
            throw new IllegalArgumentException("Only JPEG or PNG images are allowed");
        }

        Path uploadPath = Paths.get(uploadDir, userId.toString());
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;

    }

    public String getProfileImg(Long id) {

        var user = userService.getUserById(id);
        return uploadDir + "/" + user.getId() + "/" + user.getProfileImg();
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


    public void checkProfileImg(MultipartFile img){

        BufferedImage image = null;
        try {
            image = ImageIO.read(img.getInputStream());
        } catch (IOException e) {
            throw new InvalidInputException("There was an image input error");
        }

        int width = image.getWidth();
        int height = image.getHeight();

        if(width != 500 || height != 500) throw new InvalidInputException("Image dimensions must be 500x500");

    }
}
