package com.example.nvt.service;


import com.example.nvt.exceptions.NotFoundException;
import com.example.nvt.model.User;
import com.example.nvt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;


    public User getUserByEmail(String email){

        var userWrapper = userRepository.findByEmail(email);
        if(userWrapper.isEmpty()) throw new NotFoundException("User not found");
        return userWrapper.get();
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }
}
