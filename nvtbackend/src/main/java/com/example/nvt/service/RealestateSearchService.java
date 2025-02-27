package com.example.nvt.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.example.nvt.model.elastic.CityDoc;
import com.example.nvt.model.elastic.MunicipalityDoc;
import com.example.nvt.model.elastic.RealestateDoc;
import com.example.nvt.model.elastic.RegionDoc;
import com.example.nvt.repository.elastic.RealestateDocRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class RealestateSearchService {

    private final RealestateDocRepository realestateDocRepository;

    private final ElasticsearchClient esClient;

    public List<Object> search(String queryString) throws IOException {

        Query query = Query.of(q -> q
                .multiMatch(MultiMatchQuery.of(m -> m
                        .query(queryString)
                        .fields("address", "city^2", "municipality^4", "region^7")
                        .type(TextQueryType.BestFields)
                        .fuzziness("1")
                ))
        );

        SearchRequest request = SearchRequest.of(s -> s
                .index("city", "municipality", "region", "realestate")
                .query(query)
                .sort(SortOptions.of(so -> so
                                .field(f -> f
                                        .field("_score")
                                        .order(SortOrder.Desc)
                                )
                        ))
        );

        SearchResponse<JsonData> response = esClient.search(request, JsonData.class);

//        for (Hit<JsonData> hit : response.hits().hits()) {
//            System.out.println(hit.source().toJson());
//        }

        return response.hits().hits().stream()
                .map(hit -> {
                    String index = hit.index();
                    Object doc;
                    if ("city".equals(index)) {
                        doc = hit.source().to(CityDoc.class);
                    } else if ("municipality".equals(index)) {
                        doc = hit.source().to(MunicipalityDoc.class);
                    } else if ("region".equals(index)) {
                        doc = hit.source().to(RegionDoc.class);
                    }else if ("realestate".equals(index)) {
                        doc = hit.source().to(RealestateDoc.class);
                    }else {
                        doc = hit.source(); // Fallback if type is unknown
                    }

                    if (doc instanceof CityDoc) {
                        ((CityDoc) doc).setId(hit.id());
                    } else if (doc instanceof MunicipalityDoc) {
                        ((MunicipalityDoc) doc).setId(hit.id());
                    } else if (doc instanceof RegionDoc) {
                        ((RegionDoc) doc).setId(hit.id());
                    } else if (doc instanceof RealestateDoc) {
                        ((RealestateDoc) doc).setId(hit.id());
                    }
                    return doc;
                })
                .collect(Collectors.toList());

//        Query query = MatchQuery.of(m -> m
//                .query(queryString)
//                .field("fullAddress")
//                .fuzziness("auto")
//        )._toQuery();
//
//        // Execute the search
//        SearchResponse<RealestateDoc> response = esClient.search(s -> s
//                        .index("realestate")
//                        .query(query)  // Use the multi_match query
//                        .size(10) // Limit to 10 results
//                        .sort(SortOptions.of(so -> so
//                                .field(f -> f
//                                        .field("_score")
//                                        .order(SortOrder.Desc)
//                                )
//                        )),
//                RealestateDoc.class
//        );
//
//        List<Hit<RealestateDoc>> hits = response.hits().hits();
//
//        return hits.stream()
//                .map(hit -> {
//                    RealestateDoc doc = hit.source();
//                    if (doc != null) {
//                        doc.setId(hit.id());  // Manually set the ID from the hit
//                    }
//                    return doc;
//                })
//                .collect(Collectors.toList());


    }
}
