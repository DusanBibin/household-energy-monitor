package com.example.nvt.service;


import com.example.nvt.DTO.HouseholdRequestDTO;
import com.example.nvt.DTO.HouseholdRequestPreviewDTO;
import com.example.nvt.DTO.UserSummaryDTO;
import com.example.nvt.enumeration.RealEstateType;
import com.example.nvt.enumeration.RequestStatus;
import com.example.nvt.enumeration.RequestType;
import com.example.nvt.exceptions.InvalidInputException;
import com.example.nvt.model.*;
import com.example.nvt.repository.elastic.HouseholdRequestRepository;
import com.example.nvt.specifications.HouseholdRequestSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
    private final UserService userService;

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

    public HouseholdRequestDTO getHouseholdRequestDetails(Long userId, Long realestateId, Long householdId, Long requestId) {
        Realestate realestate = realestateService.getRealestateById(realestateId);
        Household household = householdService.getHouseholdByIdAndRealestateId(realestateId, householdId);

        HouseholdRequest request = getRequestByIdAndHouseholdId(requestId, householdId);


        return convertToDto(request);
    }


    public Page<HouseholdRequestPreviewDTO> getClientRequests(Long clientId, RequestStatus status, int page, int size,
                                                              String sortField, String sortDir) {
        if(page < 0 ) page = 0;
        if(size < 1) size = 10;
        Pageable pageable = PageRequest.of(page, size, sortDir.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending());


        Specification<HouseholdRequest> spec = Specification
                .where(HouseholdRequestSpecifications.hasRequesterId(clientId));

        if (status != null) {
            spec = spec.and(HouseholdRequestSpecifications.hasRequestStatus(status));
        }

        Page<HouseholdRequest> resultPage = householdRequestRepository.findAll(spec, pageable);

        return resultPage.map(this::convertToSummaryDto);

    }


    private HouseholdRequestDTO convertToDto(HouseholdRequest request){

        return HouseholdRequestDTO.builder()
                .id(request.getId())
                .householdId(request.getHousehold().getId())
                .realestateId(request.getHousehold().getRealestate().getId())
                .denialReason(request.getDenialReason())
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

                .address(address)
                .requestSubmitted(request.getRequestSubmitted())
                .requestProcessed(request.getRequestProcessed())
                .householdId(request.getHousehold() != null ? request.getHousehold().getId() : null)
                .realestateId(request.getHousehold() != null && request.getHousehold().getRealestate() != null
                        ? request.getHousehold().getRealestate().getId() : null)
                .build();
    }


}
