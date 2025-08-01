package com.example.nvt.service;


import com.example.nvt.DTO.UserSummaryDTO;
import com.example.nvt.exceptions.InvalidInputException;
import com.example.nvt.model.Clerk;
import com.example.nvt.repository.ClerkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClerkService {

    private final ClerkRepository clerkRepository;
    private final UserService userService;

    public Clerk getClerkById(Long clerkId){
        Optional<Clerk> wrapper = clerkRepository.findById(clerkId);
        if(wrapper.isEmpty()) throw new InvalidInputException("Clerk with this id doesn't exist");
        return wrapper.get();
    }

    public Clerk saveClerk(Clerk clerk) {
        return clerkRepository.save(clerk);
    }

    public Page<UserSummaryDTO> getClerks(int page, int size) {

        Page<Clerk> clerksPage = clerkRepository.findAll(PageRequest.of(page, size));

        return clerksPage.map(userService::convertToDTO);
    }
}
