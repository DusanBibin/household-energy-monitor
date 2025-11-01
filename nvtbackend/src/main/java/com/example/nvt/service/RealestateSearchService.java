package com.example.nvt.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationBuilders;
import co.elastic.clients.elasticsearch._types.aggregations.GeoTileGridBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.example.nvt.enumeration.FilterType;
import com.example.nvt.model.elastic.CityDoc;
import com.example.nvt.model.elastic.MunicipalityDoc;
import com.example.nvt.model.elastic.RealestateDoc;
import com.example.nvt.model.elastic.RegionDoc;
import com.example.nvt.repository.elastic.RealestateDocRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.AggregationContainer;
import org.springframework.data.elasticsearch.core.AggregationsContainer;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class RealestateSearchService {

    private final RealestateDocRepository realestateDocRepository;

    private final ElasticsearchClient esClient;

    public List<RealestateDoc> aggregate(
            double topLeftLon, double topLeftLat,
            double bottomRightLon, double bottomRightLat,
            FilterType filterType, String filterDocId, int zoomLevel) throws IOException {



        int targetMaxBuckets = 5000;
        int precision = 3 + zoomLevel;

        
        int lonTiles = (int) Math.pow(2, precision);
        int latTiles = (int) Math.pow(2, precision);

        double lonSpan = bottomRightLon - topLeftLon;
        double latSpan = topLeftLat - bottomRightLat;

        int estBuckets = (int) (lonTiles * (lonSpan / 360.0) * latTiles * (latSpan / 180.0));

        if (estBuckets > targetMaxBuckets) {
            precision = (int) (precision - Math.log(estBuckets / targetMaxBuckets) / Math.log(2));
            precision = Math.max(1, precision); 
        }

        final int prec = precision;







        GeoBoundingBoxQuery geoBoundingBoxQuery = QueryBuilders.geoBoundingBox()
                .field("location")
                .boundingBox(g -> g.coords(
                        c -> c
                                .top(topLeftLat)
                                .left(topLeftLon)
                                .bottom(bottomRightLat)
                                .right(bottomRightLon)
                ))
                .build();


        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool()
                .filter(f -> f.geoBoundingBox(geoBoundingBoxQuery))
                .must(m -> m.term( t -> t
                        .field("vacant")
                        .value(true)));
        if(filterType != null) {

            String fieldTypeStr;
            if(filterType.equals(FilterType.REGION)) fieldTypeStr = "regionDocId";
            else if(filterType.equals(FilterType.MUNICIPALITY)) fieldTypeStr = "municipalityDocId";
            else fieldTypeStr = "cityDocId";

            boolQueryBuilder.filter(f -> f
                    .term(
                            t -> t
                                    .field(fieldTypeStr)
                                    .value(filterDocId)
                    ));
        }

        BoolQuery boolQuery = boolQueryBuilder.build();

        Aggregation geoTileGridAggregation = Aggregation.of(a -> a
                .geotileGrid(g -> g
                        .field("location")
                        .precision(prec)
                        .size(150))

                .aggregations("top_realestates", agg -> agg
                        .topHits(th -> th
                                .size(1)
                                .source(src -> src
                                        .filter(f -> f
                                                .includes("id", "location", "address", "dbId", "vacant", "type"))
                                )
                        )
                )
        );



        SearchRequest request = SearchRequest.of( s-> s
                .index("realestate")
                .query( q-> q.bool(boolQuery))
                .aggregations("grid", geoTileGridAggregation)
                .size(0)
        );




        SearchResponse<RealestateDoc> response = esClient.search(request, RealestateDoc.class);

        List<GeoTileGridBucket> list = response.aggregations().get("grid").geotileGrid().buckets().array();
        List<RealestateDoc> realestateDocs = new ArrayList<>();
        for(GeoTileGridBucket b: list){

            RealestateDoc doc = b.aggregations().get("top_realestates").topHits().hits().hits().get(0).source().to(RealestateDoc.class);
            realestateDocs.add(doc);
        }
        return realestateDocs;
    }







    public void updateRealestateVacancy(Long dbId){
        Optional<RealestateDoc> optionalDoc = realestateDocRepository.findByDbId(dbId);

        if (optionalDoc.isPresent()) {
            RealestateDoc doc = optionalDoc.get();
            doc.setVacant(false);
            realestateDocRepository.save(doc);
        }
    }

    public List<Object> search(String queryString) throws IOException {


        BoolQuery.Builder queryBuilder = QueryBuilders.bool()
                .must(m -> m.multiMatch(MultiMatchQuery.of(q -> q
                        .query(queryString)
                        .fields("address", "city^2", "municipality^4", "region^7")
                        .type(TextQueryType.BestFields)
                        .fuzziness("1")
                        )
                )
        ).mustNot(m -> m.term( t -> t.field("vacant").value(false)));

        BoolQuery boolQuery = queryBuilder.build();


//        Query query = Query.of(q -> q
//                .multiMatch(MultiMatchQuery.of(m -> m
//                        .query(queryString)
//                        .fields("address", "city^2", "municipality^4", "region^7")
//                        .type(TextQueryType.BestFields)
//                        .fuzziness("1")
//                ))
//        );

        SearchRequest request = SearchRequest.of(s -> s
                .index("city", "municipality", "region", "realestate")
                .query(q -> q.bool(boolQuery))
                .sort(SortOptions.of(so -> so
                                .field(f -> f
                                        .field("_score")
                                        .order(SortOrder.Desc)
                                )
                        ))
        );

        SearchResponse<JsonData> response = esClient.search(request, JsonData.class);



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
