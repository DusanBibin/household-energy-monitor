package com.example.nvt.service;


import com.example.nvt.exceptions.InvalidInputException;
import com.example.nvt.model.Clerk;
import com.example.nvt.repository.ClerkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClerkService {

    private final ClerkRepository clerkRepository;


    public Clerk getClerkById(Long clerkId){
        Optional<Clerk> wrapper = clerkRepository.findById(clerkId);
        if(wrapper.isEmpty()) throw new InvalidInputException("Clerk with this id doesn't exist");
        return wrapper.get();
    }

    public Clerk saveClerk(Clerk clerk) {
        return clerkRepository.save(clerk);
    }
}
