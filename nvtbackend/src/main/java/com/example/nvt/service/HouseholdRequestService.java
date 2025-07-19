package com.example.nvt.service;


import com.example.nvt.enumeration.RequestStatus;
import com.example.nvt.enumeration.RequestType;
import com.example.nvt.exceptions.InvalidAuthorizationException;
import com.example.nvt.exceptions.InvalidInputException;
import com.example.nvt.model.Client;
import com.example.nvt.model.Household;
import com.example.nvt.model.HouseholdRequest;
import com.example.nvt.model.Realestate;
import com.example.nvt.repository.elastic.HouseholdRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HouseholdRequestService {

    private final RealestateService realestateService;
    private final HouseholdService householdService;
    private final HouseholdRequestRepository householdRequestRepository;
    private final FileService fileService;

    public void createClaimRequest(Client client, Long realestateId, Long householdId, List<MultipartFile> files) {

        Realestate realestate = realestateService.getRealestateById(realestateId);
        Household household = householdService.getHouseholdByIdAndRealestateId(realestateId, householdId);
        if(household.getHouseholdOwner() != null) throw new InvalidInputException("Household already has an owner");

        if(hasClientAlreadyRequestedHousehold(householdId, client.getId())) throw new InvalidInputException("You already made a request for this household");


        HouseholdRequest request = HouseholdRequest.builder()
                .household(household)
                .requester(client)
                .requestStatus(RequestStatus.PENDING)
                .requestType(RequestType.CLAIM)
                .requestSubmitted(LocalDateTime.now())
                .build();

        request = householdRequestRepository.save(request);

        List<String> proof_images = new ArrayList<>();
        List<String> proof_pdfs = new ArrayList<>();

        for(MultipartFile file : files) {

            String contentType = file.getContentType();
            if (contentType == null) throw new InvalidInputException("There must be content type");

            String path = "";
            try{
                path = fileService.saveHouseholdRequestFile(file, request.getId());
            }catch (IOException e){
                System.out.println(e.getMessage());
                throw new InvalidInputException("There was an input output exception");
            }

            if(contentType.equals("application/pdf")) proof_pdfs.add(path);
            if(contentType.equals("image/jpeg") || contentType.equals("image/png")) proof_images.add(path);

        }

        request.setProof_images(proof_images);
        request.setProof_pdfs(proof_pdfs);
        request = householdRequestRepository.save(request);



        household.getClaimRequests().add(request);
        householdService.saveHousehold(household);

    }


    public Optional<HouseholdRequest> getRequestByClient(Long householdId, Long clientId){
        return householdRequestRepository.getRequestByClient(householdId, clientId);
    }

    public boolean hasClientAlreadyRequestedHousehold(Long householdId, Long clientId) {
        return getRequestByClient(householdId, clientId).isPresent();
    }

}
