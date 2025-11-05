package com.example.nvt.service;


import com.example.nvt.DTO.PagedResponse;
import com.example.nvt.DTO.UserSummaryDTO;
import com.example.nvt.exceptions.InvalidInputException;
import com.example.nvt.model.Clerk;
import com.example.nvt.repository.ClerkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public Clerk getClerkByIdForUpdate(Long clerkId){
        Optional<Clerk> wrapper = clerkRepository.findByIdForUpdate(clerkId);
        if(wrapper.isEmpty()) throw new InvalidInputException("Clerk with this id doesn't exist");
        return wrapper.get();
    }

    public Clerk saveClerk(Clerk clerk) {
        return clerkRepository.save(clerk);
    }



    @Cacheable(
            value = "clerksPageCache",
            key = "#page + '_' + #size"
    )
    public PagedResponse<UserSummaryDTO> getClerks(int page, int size) {

        if (page < 0) page = 0;
        if (size < 1) size = 10;

        Page<Clerk> clerksPage = clerkRepository.findAll(PageRequest.of(page, size));


        List<UserSummaryDTO> dtos = clerksPage.getContent()
                .stream()
                .map(userService::convertToDTO)
                .toList();


        return PagedResponse.<UserSummaryDTO>builder()
                .content(dtos)
                .page(clerksPage.getNumber())
                .size(clerksPage.getSize())
                .totalElements(clerksPage.getTotalElements())
                .totalPages(clerksPage.getTotalPages())
                .build();
    }
}
