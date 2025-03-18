package com.example.nvt.controller;

import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.nvt.enumeration.FilterType;
import com.example.nvt.model.elastic.RealestateDoc;
import com.example.nvt.service.RealestateSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/realestate")
@RequiredArgsConstructor
public class RealestateController {

    private final RealestateSearchService searchService;

    @GetMapping("/search")
    public List<Object> search(@RequestParam String query) throws IOException {
        return searchService.search(query);
    }

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

}