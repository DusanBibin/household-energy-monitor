package com.example.nvt.configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.*;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ElasticsearchIndexConfig {

    private final ElasticsearchClient esClient;
    private static final String INDEX_REALESTATE = "realestate";
    private static final String INDEX_CITY = "city";
    private static final String INDEX_MUNICIPALITY = "municipality";
    private static final String INDEX_REGION = "region";

    @PostConstruct
    public void initialize() throws IOException {


        initializeIndexes();
        System.out.println("iksde");

    }


    private void initializeIndexes()  throws IOException {
        boolean realestateExists = esClient.indices().exists(e -> e.index(INDEX_REALESTATE)).value();
        boolean cityExists = esClient.indices().exists(e -> e.index(INDEX_CITY)).value();
        boolean municipalityExists = esClient.indices().exists(e -> e.index(INDEX_MUNICIPALITY)).value();
        boolean regionExists = esClient.indices().exists(e -> e.index(INDEX_REGION)).value();

        if (realestateExists) {
            esClient.indices().delete(DeleteIndexRequest.of(d -> d.index(INDEX_REALESTATE)));
            System.out.println("Index realestate deleted.");
        } else {
            System.out.println("Index realestate does not exist.");
        }
        if (cityExists) {
            esClient.indices().delete(DeleteIndexRequest.of(d -> d.index(INDEX_CITY)));
            System.out.println("Index city deleted.");
        } else {
            System.out.println("Index city does not exist.");
        }
        if (municipalityExists) {
            esClient.indices().delete(DeleteIndexRequest.of(d -> d.index(INDEX_MUNICIPALITY)));
            System.out.println("Index municipality deleted.");
        } else {
            System.out.println("Index municipality does not exist.");
        }
        if (regionExists) {
            esClient.indices().delete(DeleteIndexRequest.of(d -> d.index(INDEX_REGION)));
            System.out.println("Index region deleted.");
        } else {
            System.out.println("Index region does not exist.");
        }



        System.out.println("DA LI SE OVO UPALILO ");




        CreateIndexResponse response = esClient.indices().create(c -> c
                        .index(INDEX_REALESTATE)
                        .settings(IndexSettings.of(s -> s
                                .analysis(a -> a
                                        .filter("autocomplete_filter", f -> f
                                                .definition(fd -> fd
                                                        .edgeNgram(ngram -> ngram
                                                                .minGram(1)
                                                                .maxGram(30)
                                                        )
                                                )
                                        ).filter("asciifolding_filter", f -> f
                                                .definition(fd -> fd
                                                        .asciifolding(asc -> asc
                                                                .preserveOriginal(true))))
                                        .analyzer("autocomplete_search", an -> an
                                                .custom(cust -> cust
                                                        .tokenizer("standard")
                                                        .filter("lowercase")
                                                )
                                        ).analyzer("autocomplete_index", an -> an
                                                .custom(cust -> cust
                                                        .tokenizer("standard")
                                                        .filter("lowercase", "autocomplete_filter", "asciifolding_filter")
                                                )
                                        )
                                )
                        ))
                        .mappings(m -> m.properties(Map.of(
                                        "location", new Property.Builder().geoPoint(new GeoPointProperty.Builder().build()).build(),
                                        "address", new Property.Builder().text(new TextProperty.Builder().analyzer("autocomplete_index").searchAnalyzer("autocomplete_search").build()).build(),
                                        "cityDocId", new Property.Builder().keyword(new KeywordProperty.Builder().build()).build(),
                                        "municipalityDocId", new Property.Builder().keyword(new KeywordProperty.Builder().build()).build(),
                                        "regionDocId", new Property.Builder().keyword(new KeywordProperty.Builder().build()).build(),
                                        "vacant", new Property.Builder().boolean_(new BooleanProperty.Builder().build()).build()
//                                "city", new Property.Builder().text(new TextProperty.Builder().analyzer("autocomplete_index").searchAnalyzer("autocomplete_search").build()).build(),
//                                "municipality", new Property.Builder().text(new TextProperty.Builder().analyzer("autocomplete_index").searchAnalyzer("autocomplete_search").build()).build(),
//                                "region", new Property.Builder().text(new TextProperty.Builder().analyzer("autocomplete_index").searchAnalyzer("autocomplete_search").build()).build(),
//                                "zipcode", new Property.Builder().text(new TextProperty.Builder().analyzer("autocomplete_index").searchAnalyzer("autocomplete_search").build()).build(),

                                )
                        ))
        );

        CreateIndexResponse responseCity = esClient.indices().create(c -> c
                .index(INDEX_CITY)
                .settings(IndexSettings.of(s -> s
                        .analysis(a -> a
                                .filter("autocomplete_filter", f -> f
                                        .definition(fd -> fd
                                                .edgeNgram(ngram -> ngram
                                                        .minGram(1)
                                                        .maxGram(30)
                                                )
                                        )
                                ).filter("asciifolding_filter", f -> f
                                        .definition(fd -> fd
                                                .asciifolding(asc -> asc
                                                        .preserveOriginal(true))))
                                .analyzer("autocomplete_search", an -> an
                                        .custom(cust -> cust
                                                .tokenizer("standard")
                                                .filter("lowercase")
                                        )
                                ).analyzer("autocomplete_index", an -> an
                                        .custom(cust -> cust
                                                .tokenizer("standard")
                                                .filter("lowercase", "autocomplete_filter", "asciifolding_filter")
                                        )
                                )
                        )
                ))
                .mappings(m -> m.properties(Map.of(
                                "city", new Property.Builder().text(new TextProperty.Builder().analyzer("autocomplete_index").searchAnalyzer("autocomplete_search").build()).build()
                        )
                ))
        );

        CreateIndexResponse responseMunicipality = esClient.indices().create(c -> c
                .index(INDEX_MUNICIPALITY)
                .settings(IndexSettings.of(s -> s
                        .analysis(a -> a
                                .filter("autocomplete_filter", f -> f
                                        .definition(fd -> fd
                                                .edgeNgram(ngram -> ngram
                                                        .minGram(1)
                                                        .maxGram(30)
                                                )
                                        )
                                ).filter("asciifolding_filter", f -> f
                                        .definition(fd -> fd
                                                .asciifolding(asc -> asc
                                                        .preserveOriginal(true))))
                                .analyzer("autocomplete_search", an -> an
                                        .custom(cust -> cust
                                                .tokenizer("standard")
                                                .filter("lowercase")
                                        )
                                ).analyzer("autocomplete_index", an -> an
                                        .custom(cust -> cust
                                                .tokenizer("standard")
                                                .filter("lowercase", "autocomplete_filter", "asciifolding_filter")
                                        )
                                )
                        )
                ))
                .mappings(m -> m.properties(Map.of(
                                "municipality", new Property.Builder().text(new TextProperty.Builder().analyzer("autocomplete_index").searchAnalyzer("autocomplete_search").build()).build()
                        )
                ))
        );

        CreateIndexResponse responseRegion = esClient.indices().create(c -> c
                .index(INDEX_REGION)
                .settings(IndexSettings.of(s -> s
                        .analysis(a -> a
                                .filter("autocomplete_filter", f -> f
                                        .definition(fd -> fd
                                                .edgeNgram(ngram -> ngram
                                                        .minGram(1)
                                                        .maxGram(30)
                                                )
                                        )
                                ).filter("asciifolding_filter", f -> f
                                        .definition(fd -> fd
                                                .asciifolding(asc -> asc
                                                        .preserveOriginal(true))))
                                .analyzer("autocomplete_search", an -> an
                                        .custom(cust -> cust
                                                .tokenizer("standard")
                                                .filter("lowercase")
                                        )
                                ).analyzer("autocomplete_index", an -> an
                                        .custom(cust -> cust
                                                .tokenizer("standard")
                                                .filter("lowercase", "autocomplete_filter", "asciifolding_filter")
                                        )
                                )
                        )
                ))
                .mappings(m -> m.properties(Map.of(
                                "region", new Property.Builder().text(new TextProperty.Builder().analyzer("autocomplete_index").searchAnalyzer("autocomplete_search").build()).build()
                        )
                ))
        );

        if (response.acknowledged()) {
            System.out.println("Index '" + INDEX_REALESTATE + "' created successfully.");
        } else {
            System.out.println("Failed to create index.");
        }

        if (responseCity.acknowledged()) {
            System.out.println("Index '" + INDEX_CITY + "' created successfully.");
        } else {
            System.out.println("Failed to create index.");
        }

        if (responseMunicipality.acknowledged()) {
            System.out.println("Index '" + INDEX_MUNICIPALITY + "' created successfully.");
        } else {
            System.out.println("Failed to create index.");
        }

        if (responseRegion.acknowledged()) {
            System.out.println("Index '" + INDEX_REGION+ "' created successfully.");
        } else {
            System.out.println("Failed to create index.");
        }
    }
}
