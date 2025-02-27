package com.example.nvt.controller;

import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.nvt.model.elastic.RealestateDoc;
import com.example.nvt.service.RealestateSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        //return new ArrayList<>();
    }
}