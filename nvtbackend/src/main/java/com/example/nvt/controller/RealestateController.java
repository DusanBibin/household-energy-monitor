package com.example.nvt.controller;

import com.example.nvt.DTO.RealestateImagePathsDTO;
import com.example.nvt.enumeration.FilterType;
import com.example.nvt.model.elastic.RealestateDoc;
import com.example.nvt.service.RealestateSearchService;
import com.example.nvt.service.RealestateService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAnyAuthority('CLIENT', 'ADMIN', 'OFFICIAL', 'SUPERADMIN')")
    @GetMapping("/{realestateId}/households")
    public List<Long> getRealestateHouseholdIds(@PathVariable Long realestateId){
        System.out.println(realestateId);

        return realestateService.getVacantRealestateHouseholdIds(realestateId);
    }
}