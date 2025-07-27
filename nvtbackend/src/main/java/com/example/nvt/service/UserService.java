package com.example.nvt.service;


import com.example.nvt.DTO.UserSummaryDTO;
import com.example.nvt.exceptions.NotFoundException;
import com.example.nvt.model.User;
import com.example.nvt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FileService fileService;

    public User getUserByEmail(String email){

        var userWrapper = userRepository.findByEmail(email);
        if(userWrapper.isEmpty()) throw new NotFoundException("User not found");
        return userWrapper.get();
    }

    public User getUserById(Long id) {
        var userWrapper = userRepository.findById(id);
        if(userWrapper.isEmpty()) throw new NotFoundException("User not found");
        return userWrapper.get();
    }

    public boolean emailAlreadyExists(String email){
        var userWrapper = userRepository.findByEmail(email);
        return userWrapper.isPresent();
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }



    public UserSummaryDTO convertToDTO(User user){
        return UserSummaryDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .lastname(user.getLastname())
                .name(user.getFirstName())
                .build();
    }

    public String getSmallProfileImg(Long id) {

        var user = getUserById(id);
        String imagePath = fileService.uploadDirUsers + "/" +  user.getId() + "/small_" + user.getProfileImg();

        if(!Files.exists(Paths.get(imagePath))) return "/" + fileService.uploadDirUsers + "/DEFAULT.jpg";

        return "/" + imagePath;
    }

}
