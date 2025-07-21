package com.example.nvt.service;


import com.example.nvt.enumeration.RequestStatus;
import com.example.nvt.enumeration.RequestType;
import com.example.nvt.exceptions.InvalidInputException;
import com.example.nvt.model.*;
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
    private final ClientService clientService;

    public void createClaimRequest(Client client, Long realestateId, Long householdId, List<MultipartFile> files) {


        client = clientService.findClientById(client.getId());

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


        client.getAssetRequests().add(request);
        client = clientService.saveClient(client);

    }


    public Optional<HouseholdRequest> getRequestByHouseholdIdAndClientId(Long householdId, Long clientId){
        return householdRequestRepository.getRequestByHouseholdIdAndClientId(householdId, clientId);
    }


    public HouseholdRequest getRequestByIdAndHouseholdId(Long requestId, Long householdId){
        Optional<HouseholdRequest> wrapper = householdRequestRepository.getRequestByIdAndHouseholdId(requestId, householdId);
        if(wrapper.isEmpty()) throw new InvalidInputException("Request doesn't exist");
        return wrapper.get();
    }



    public boolean hasClientAlreadyRequestedHousehold(Long householdId, Long clientId) {
        return getRequestByHouseholdIdAndClientId(householdId, clientId).isPresent();
    }

    public void acceptHouseholdRequest(Admin admin, Long realestateId, Long householdId, Long requestId) {

        Realestate realestate = realestateService.getRealestateById(realestateId);
        Household household = householdService.getHouseholdByIdAndRealestateId(realestateId, householdId);

        HouseholdRequest request = getRequestByIdAndHouseholdId(requestId, householdId);

        if(!request.getRequestStatus().equals(RequestStatus.PENDING)) throw new InvalidInputException("The request is already processed");


        List<HouseholdRequest> otherRequests = householdRequestRepository.getAllPendingHouseholdRequests(householdId);
        for(HouseholdRequest otherRequest : otherRequests){
            otherRequest.setRequestStatus(RequestStatus.REJECTED);
            otherRequest.setRequestProcessed(LocalDateTime.now());
            otherRequest.setDenialReason("Request from another user was accepted.");
            otherRequest.setReviewingAdmin(admin);
        }

        request.setRequestStatus(RequestStatus.ACCEPTED);
        request.setReviewingAdmin(admin);
        request.setRequestProcessed(LocalDateTime.now());

        otherRequests.add(request);
        otherRequests = householdRequestRepository.saveAll(otherRequests);
        //TODO DODATI EMAIL NOTIFIKACIJU
    }


    public void denyHouseholdRequest(Admin admin, Long realestateId, Long householdId, Long requestId, String denialReason) {

        Realestate realestate = realestateService.getRealestateById(realestateId);
        Household household = householdService.getHouseholdByIdAndRealestateId(realestateId, householdId);

        HouseholdRequest request = getRequestByIdAndHouseholdId(requestId, householdId);

        if(!request.getRequestStatus().equals(RequestStatus.PENDING)) throw new InvalidInputException("The request is already processed");


        request.setRequestStatus(RequestStatus.REJECTED);
        request.setReviewingAdmin(admin);
        request.setRequestProcessed(LocalDateTime.now());
        request.setDenialReason(denialReason);

        request =  householdRequestRepository.save(request);
        //TODO DODATI EMAIL NOTIFIKACIJU

    }









}
