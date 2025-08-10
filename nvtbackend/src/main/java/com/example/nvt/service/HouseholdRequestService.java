package com.example.nvt.service;


import com.example.nvt.DTO.HouseholdDetailsDTO;
import com.example.nvt.DTO.HouseholdRequestDTO;
import com.example.nvt.DTO.HouseholdRequestPreviewDTO;
import com.example.nvt.enumeration.RealEstateType;
import com.example.nvt.enumeration.RequestStatus;
import com.example.nvt.enumeration.RequestType;
import com.example.nvt.exceptions.InvalidInputException;
import com.example.nvt.exceptions.NotFoundException;
import com.example.nvt.model.*;
import com.example.nvt.repository.HouseholdRequestRepository;
import com.example.nvt.specifications.HouseholdRequestSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final UserService userService;
    private final EmailService emailService;
    private final RealestateSearchService realestateSearchService;


    @Transactional
    public HouseholdDetailsDTO createClaimRequest(Client client, Long realestateId, Long householdId, List<MultipartFile> files) {


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


        return householdService.getHouseholdDetails(client, realestateId, householdId);
    }


    public Optional<HouseholdRequest> getRequestByHouseholdIdAndClientId(Long householdId, Long clientId){
        return householdRequestRepository.getRequestByHouseholdIdAndClientId(householdId, clientId);
    }


    public HouseholdRequest getRequestByIdAndHouseholdId(Long requestId, Long householdId){
        Optional<HouseholdRequest> wrapper = householdRequestRepository.getRequestByIdAndHouseholdId(requestId, householdId);
        if(wrapper.isEmpty()) throw new NotFoundException("Request doesn't exist");
        return wrapper.get();
    }



    public boolean hasClientAlreadyRequestedHousehold(Long householdId, Long clientId) {
        return getRequestByHouseholdIdAndClientId(householdId, clientId).isPresent();
    }

    @Transactional
    public HouseholdRequestDTO acceptHouseholdRequest(Admin admin, Long realestateId, Long householdId, Long requestId) {

        Realestate realestate = realestateService.getRealestateById(realestateId);
        Household household = householdService.getHouseholdByIdAndRealestateId(realestateId, householdId);


        HouseholdRequest request = getRequestByIdAndHouseholdId(requestId, householdId);

        if(!request.getRequestStatus().equals(RequestStatus.PENDING)) throw new InvalidInputException("The request is already processed");


        List<HouseholdRequest> otherRequests = householdRequestRepository.getAllPendingHouseholdRequests(householdId, request.getId());
        for(HouseholdRequest otherRequest : otherRequests){
            otherRequest.setRequestStatus(RequestStatus.REJECTED);
            otherRequest.setRequestProcessed(LocalDateTime.now());
            otherRequest.setDenialReason("Request from another user was accepted.");
            otherRequest.setReviewingAdmin(admin);
        }

        request.setRequestStatus(RequestStatus.ACCEPTED);
        request.setReviewingAdmin(admin);
        request.setRequestProcessed(LocalDateTime.now());


        try {
            otherRequests.add(request);
            otherRequests = householdRequestRepository.saveAll(otherRequests);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new InvalidInputException("The request has already been accepted by other admin moments ago");
        }


        Client client = request.getRequester();
        client.getHouseholds().add(household);

        household.setHouseholdOwner(client);
        if(!realestate.getType().equals(RealEstateType.BUILDING)){
            realestate.setRealestateOwner(client);
            client.getRealEstates().add(realestate);
        }
        realestate = realestateService.saveRealestate(realestate);
        household = householdService.saveHousehold(household);
        client =  clientService.saveClient(client);

        if (realestate.getType() != RealEstateType.BUILDING ||
                householdService.countUnownedHouseholds(realestateId) == 0) {
            realestateSearchService.updateRealestateVacancy(realestateId);
        }


//        otherRequests.add(request);
//        otherRequests = householdRequestRepository.saveAll(otherRequests);


        City c = realestate.getCity();
        Municipality m = c.getMunicipality();
        Region rg = m.getRegion();

        String address = realestate.getAddressStreet() + " " + realestate.getAddressNum() + " " + c.getName() + " " + m.getName() + " " + rg.getName();

        if(realestate.getType().equals(RealEstateType.BUILDING)) {
            address = "Apartment " + household.getApartmentNum() + " " + address;
        }

        for(HouseholdRequest req:  otherRequests){
            Client requester = req.getRequester();
            emailService.sendRequestUpdate(requester.getEmail(), requester.getFirstName(), address, req.getRequestStatus().equals(RequestStatus.ACCEPTED) ? "accepted" : "denied", req.getDenialReason());
        }


        return convertToDto(request);
    }

    @Transactional
    public HouseholdRequestDTO denyHouseholdRequest(Admin admin, Long realestateId, Long householdId, Long requestId, String denialReason) {

        Realestate realestate = realestateService.getRealestateById(realestateId);
        Household household = householdService.getHouseholdByIdAndRealestateId(realestateId, householdId);

        HouseholdRequest request = getRequestByIdAndHouseholdId(requestId, householdId);

        if(!request.getRequestStatus().equals(RequestStatus.PENDING)) throw new InvalidInputException("The request is already processed");


        request.setRequestStatus(RequestStatus.REJECTED);
        request.setReviewingAdmin(admin);
        request.setRequestProcessed(LocalDateTime.now());
        request.setDenialReason(denialReason);

        request =  householdRequestRepository.save(request);

        City c = realestate.getCity();
        Municipality m = c.getMunicipality();
        Region rg = m.getRegion();
        Client requester = request.getRequester();

        String address = realestate.getAddressStreet() + " " + realestate.getAddressNum() + " " + c.getName() + " " + m.getName() + " " + rg.getName();

        if(realestate.getType().equals(RealEstateType.BUILDING)) {
            address = "Apartment " + household.getApartmentNum() + " " + address;
        }



        emailService.sendRequestUpdate(requester.getEmail(), requester.getFirstName(), address, request.getRequestStatus().equals(RequestStatus.ACCEPTED) ? "accepted" : "denied", denialReason);

        return convertToDto(request);
    }

    public HouseholdRequestDTO getHouseholdRequestDetails(Long userId, Long realestateId, Long householdId, Long requestId) {
        Realestate realestate = realestateService.getRealestateById(realestateId);
        Household household = householdService.getHouseholdByIdAndRealestateId(realestateId, householdId);

        HouseholdRequest request = getRequestByIdAndHouseholdId(requestId, householdId);


        return convertToDto(request);
    }


    public Page<HouseholdRequestPreviewDTO> getClientRequests(User user, RequestStatus status, int page, int size,
                                                              String sortField, String sortDir) {
        if(page < 0 ) page = 0;
        if(size < 1) size = 10;
        System.out.println(user.getId());
        user = userService.getUserById(user.getId());

        Pageable pageable = PageRequest.of(page, size, sortDir.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending());

        Specification<HouseholdRequest> spec = Specification.where(null);
        if(user instanceof Client){
            System.out.println("Da li smo usli ovde");
            spec = Specification.where(HouseholdRequestSpecifications.hasRequesterId(user.getId()));
        }


        if (status != null) {
            spec = spec.and(HouseholdRequestSpecifications.hasRequestStatus(status));
        }

        Page<HouseholdRequest> resultPage = householdRequestRepository.findAll(spec, pageable);
        System.out.println("resultPage = " + resultPage);
        return resultPage.map(this::convertToSummaryDto);

    }


    private HouseholdRequestDTO convertToDto(HouseholdRequest request){
        Realestate r = request.getHousehold().getRealestate();
        City c = r.getCity();
        Municipality m = c.getMunicipality();
        Region rg = m.getRegion();
        Household h =  request.getHousehold();

        String address = r.getAddressStreet() + " " + r.getAddressNum() + " " + c.getName() + " " + m.getName() + " " + rg.getName();

        if(r.getType().equals(RealEstateType.BUILDING)) {
            address = "Apartment " + h.getApartmentNum() + " " + address;
        }

        return HouseholdRequestDTO.builder()
                .id(request.getId())
                .address(address)
                .householdId(request.getHousehold().getId())
                .realestateId(request.getHousehold().getRealestate().getId())
                .denialReason(request.getDenialReason())
                .realEstateType(r.getType())
                .requestProcessed(request.getRequestProcessed())
                .requestSubmitted(request.getRequestSubmitted())
                .requestStatus(request.getRequestStatus())
                .proof_images(request.getProof_images())
                .proof_pdfs(request.getProof_pdfs())
                .requestType(request.getRequestType())
                .requester(request.getRequester() != null ? userService.convertToDTO(request.getRequester()) : null)
                .reviewingAdmin(request.getReviewingAdmin() != null ? userService.convertToDTO(request.getReviewingAdmin()) : null)
                .build();
    }

    private HouseholdRequestPreviewDTO convertToSummaryDto(HouseholdRequest request) {
        Realestate r = request.getHousehold().getRealestate();
        City c = r.getCity();
        Municipality m = c.getMunicipality();
        Region rg = m.getRegion();
        Household h =  request.getHousehold();


        String address = r.getAddressStreet() + " " + r.getAddressNum() + " " + c.getName() + " " + m.getName() + " " + rg.getName();

        if(r.getType().equals(RealEstateType.BUILDING)) {
            address = "Apartment " + h.getApartmentNum() + " " + address;
        }

        return HouseholdRequestPreviewDTO.builder()
                .id(request.getId())

                .requestStatus(request.getRequestStatus())
                .realEstateType(r.getType())
                .address(address)
                .requestSubmitted(request.getRequestSubmitted())
                .requestProcessed(request.getRequestProcessed())
                .householdId(request.getHousehold() != null ? request.getHousehold().getId() : null)
                .realestateId(request.getHousehold() != null && request.getHousehold().getRealestate() != null
                        ? request.getHousehold().getRealestate().getId() : null)
                .build();
    }


    public HouseholdRequest getRequestById(Long requestId) {
        Optional<HouseholdRequest> wrapper = householdRequestRepository.findById(requestId);
        if(wrapper.isEmpty()) throw new InvalidInputException("Request with this id doesn't exist");
        return wrapper.get();
    }

    public String getHouseholdRequestFile(Long requestId, String fileName) {

            HouseholdRequest request = getRequestById(requestId);

            if(!(request.getProof_images().contains(fileName) || request.getProof_pdfs().contains(fileName))) throw new InvalidInputException("This file doesn't exist");

            return "/" + fileService.uploadDirHouseholdRequests + "/" + request.getId() + "/" + fileName;

    }

    public Page<HouseholdRequestPreviewDTO> getConflictedPendingRequests(Long realestateId, Long householdId, Long requestId, User user, int page, int size) {


        if(page < 0 ) page = 0;
        if(size < 1) size = 10;


        Realestate realestate = realestateService.getRealestateById(realestateId);
        Household household = householdService.getHouseholdByIdAndRealestateId(realestateId, householdId);

        HouseholdRequest request = getRequestByIdAndHouseholdId(requestId, householdId);
        if(!request.getRequestStatus().equals(RequestStatus.PENDING)) throw new InvalidInputException("Request status for this request is not PENDING");

        Pageable pageable = PageRequest.of(page, size, Sort.by("requestSubmitted").ascending());

        Specification<HouseholdRequest> spec = Specification
                .where(HouseholdRequestSpecifications.hasHouseholdId(householdId))
                .and(HouseholdRequestSpecifications.hasStatus(RequestStatus.PENDING))
                .and(HouseholdRequestSpecifications.idNotEqual(requestId));



        Page<HouseholdRequest> resultPage = householdRequestRepository.findAll(spec, pageable);

        return resultPage.map(this::convertToSummaryDto);
    }
}
