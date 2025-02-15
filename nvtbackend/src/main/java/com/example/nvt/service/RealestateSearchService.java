package com.example.nvt.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.nvt.model.elastic.RealestateDoc;
import com.example.nvt.repository.RealestateDocRepository;
import com.example.nvt.repository.RealestateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class RealestateSearchService {

    private final RealestateDocRepository realestateDocRepository;

    private final ElasticsearchClient esClient;

    public List<RealestateDoc> search(String queryString) throws IOException {


        Query query = MatchQuery.of(m -> m
                .query(queryString)
                .field("fullAddress")
                .fuzziness("auto")
        )._toQuery();

        // Execute the search
        SearchResponse<RealestateDoc> response = esClient.search(s -> s
                        .index("realestate")
                        .query(query)  // Use the multi_match query
                        .size(10) // Limit to 10 results
                        .sort(SortOptions.of(so -> so
                                .field(f -> f
                                        .field("_score")
                                        .order(SortOrder.Desc)
                                )
                        )),
                RealestateDoc.class
        );

        List<Hit<RealestateDoc>> hits = response.hits().hits();

        return hits.stream()
                .map(hit -> {
                    RealestateDoc doc = hit.source();
                    if (doc != null) {
                        doc.setId(hit.id());  // Manually set the ID from the hit
                    }
                    return doc;
                })
                .collect(Collectors.toList());

    }
}
