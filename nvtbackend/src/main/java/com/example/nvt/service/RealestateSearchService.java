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

//        Query streetQuery = MatchQuery.of(m -> m
//                .field("street")
//                .query(string)
//                .fuzziness("AUTO")
//        )._toQuery();
//
//
//        SearchResponse<RealestateDoc> response = esClient.search(s -> s
//                        .index("realestate")
//                        .query(q -> q
//                                .bool(b -> b
//                                        .must(streetQuery)
//                                )
//                        ),
//                        RealestateDoc.class
//        );



//        Query fuzzyRegionQuery = MatchQuery.of(m -> m
//                .field("region")
//                .query(string)
//                .fuzziness("AUTO")
//        )._toQuery();
//
//        SearchResponse<RealestateDoc> response = esClient.search(s -> s
//                        .index("realestate")
//                        .query(q -> q
//                                .bool(b -> b
//                                        .should(fuzzyRegionQuery)
//                                )
//                        ),
//                RealestateDoc.class
//        );
//
//
//
//
//        List<Hit<RealestateDoc>> hits = response.hits().hits();
//
//        return hits.stream()
//                .map(hit -> {
//                    RealestateDoc doc = hit.source();
//                    if (doc != null) {
//                        doc.setId(hit.id());  // Manually set the id from the hit
//                    }
//                    return doc;
//                })
//                .collect(Collectors.toList());

//-----------------------OVO RADIIII---------------------------------
//        Query multiMatchQuery = MultiMatchQuery.of(m -> m
//                .query(queryString)
//                .fields("address", "city", "municipality", "region") // Add relevant fields
//                .fuzziness("AUTO")
//                .type(co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.MostFields)
//        )._toQuery();
//
//        // Execute the search
//        SearchResponse<RealestateDoc> response = esClient.search(s -> s
//                        .index("realestate")
//                        .query(q -> q
//                                .bool(b -> b
//                                        .should(multiMatchQuery)
//                                )
//                        )
//                        .size(10) // Limit to 10 results
//                        .sort(SortOptions.of(so -> so
//                                .field(f -> f
//                                        .field("_score")
//                                        .order(SortOrder.Desc)
//                                )
//                        )),
//                RealestateDoc.class
//        );
//-----------------------OVO RADIIII---------------------------------
//        Query matchAddress = MatchQuery.of(m -> m
//                .field("address")
//                .query(queryString)
//                .fuzziness("AUTO")
//        )._toQuery();
//
//        Query matchCity = MatchQuery.of(m -> m
//                .field("city")
//                .query(queryString)
//                .fuzziness("AUTO")
//        )._toQuery();
//
//        Query matchRegion = MatchQuery.of(m -> m
//                .field("region")
//                .query(queryString)
//                .fuzziness("AUTO")
//        )._toQuery();
//
//        Query matchMunicipality = MatchQuery.of(m -> m
//                .field("municipality")
//                .query(queryString)
//                .fuzziness("AUTO")
//        )._toQuery();
//
//        // Combine them into a bool query (should clause)
//        Query boolQuery = BoolQuery.of(b -> b
//                .should(matchAddress, matchCity, matchRegion, matchMunicipality)
//        )._toQuery();
//
//        // Execute the search
//        SearchResponse<RealestateDoc> response = esClient.search(s -> s
//                        .index("realestate")
//                        .query(boolQuery)
//                        .size(10) // Limit to 10 results
//                        .sort(SortOptions.of(so -> so
//                                .field(f -> f
//                                        .field("_score")
//                                        .order(SortOrder.Desc)
//                                )
//                        )),
//                RealestateDoc.class
//        );
//        List<Hit<RealestateDoc>> hits = response.hits().hits();

        Query multiMatchQuery = MultiMatchQuery.of(m -> m
                .query(queryString)
                .type(TextQueryType.MostFields)  // Equivalent to "type": "most_fields"
                .fields(Arrays.asList(
                        "address",
                        "city^2",          // Boost city field
                        "municipality^2",  // Boost municipality field
                        "region^14"        // Boost region field
                ))
                .fuzziness("0")  // Exact matching, no fuzziness
        )._toQuery();

        // Execute the search
        SearchResponse<RealestateDoc> response = esClient.search(s -> s
                        .index("realestate")
                        .query(multiMatchQuery)  // Use the multi_match query
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
