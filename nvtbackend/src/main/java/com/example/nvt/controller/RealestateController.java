package com.example.nvt.controller;

import com.example.nvt.DTO.PagedResponse;
import com.example.nvt.DTO.RealestateImagePathsDTO;
import com.example.nvt.DTO.RealestateSummaryDTO;
import com.example.nvt.DTO.UserSummaryDTO;
import com.example.nvt.enumeration.FilterType;
import com.example.nvt.model.Client;
import com.example.nvt.model.User;
import com.example.nvt.model.elastic.RealestateDoc;
import com.example.nvt.service.RealestateSearchService;
import com.example.nvt.service.RealestateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/realestate")
@RequiredArgsConstructor
public class RealestateController {

    private final RealestateSearchService searchService;
    private final RealestateService realestateService;

    @PreAuthorize("hasAnyAuthority('CLIENT', 'ADMIN', 'OFFICIAL', 'SUPERADMIN')")
    @GetMapping("/search")
    public List<Object> search(@RequestParam String query) throws IOException {
        return searchService.search(query);
    }

    @PreAuthorize("hasAnyAuthority('CLIENT', 'ADMIN', 'OFFICIAL', 'SUPERADMIN')")
    @GetMapping("/aggregate")
    public List<RealestateDoc> aggregate(
            @RequestParam double topLeftLon,
            @RequestParam double topLeftLat,
            @RequestParam double bottomRightLon,
            @RequestParam double bottomRightLat,
            @RequestParam(required = false) FilterType filterType,
            @RequestParam(required = false) String filterDocId,
            @RequestParam(defaultValue = "13") int zoomLevel) throws IOException {

        return searchService.aggregate(
                topLeftLon, topLeftLat, bottomRightLon, bottomRightLat, filterType, filterDocId, zoomLevel);
    }

    @PreAuthorize("hasAnyAuthority('CLIENT', 'ADMIN', 'OFFICIAL', 'SUPERADMIN')")
    @PostMapping("/paged-realestate-image-paths")
    public List<RealestateImagePathsDTO> getImagePaths(@RequestBody List<Long> realestateIds) throws IOException {
        return realestateService.getImagePaths(realestateIds);
    }


    @PreAuthorize("hasAuthority('CLIENT')")
    @GetMapping()
    public ResponseEntity<PagedResponse<RealestateSummaryDTO>> getRealestateSummaries(@AuthenticationPrincipal User user,
                                                                                      @RequestParam(defaultValue = "0") int page,
                                                                                      @RequestParam(defaultValue = "10") int size){

        PagedResponse<RealestateSummaryDTO> realestates = realestateService.getRealestateSummaries(user.getId(), page, size);
        return ResponseEntity.ok(realestates);

    }




//
//    @PreAuthorize("hasAnyAuthority('CLIENT', 'ADMIN', 'OFFICIAL', 'SUPERADMIN')")
//    @GetMapping("/{realestateId}/households")
//    public List<String> getRealestateHouseholdIds(@PathVariable Long realestateId){
//        System.out.println(realestateId);
//
//        return realestateService.getVacantRealestateHouseholdIds(realestateId);
//    }
}