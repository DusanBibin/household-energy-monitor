package com.example.nvt.configuration;
import co.elastic.clients.elasticsearch.core.PingRequest;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.*;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
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

    private final ApplicationArguments args;

    @PostConstruct
    public void initialize() throws IOException, InterruptedException {


        initializeIndexes();
        // waitForElasticsearch();
        // System.out.println("jebo te ja");
        // if (this.args.containsOption("initMode")) {
        //     System.out.println("izesmi");
        //     initializeIndexes();
        //     System.out.println("iksde");
        // }


    }


    private void waitForElasticsearch() throws InterruptedException {
        boolean connected = false;
        while (!connected) {
            try {
                BooleanResponse response = esClient.ping();
                if (response.value()) {
                    connected = true;
                    System.out.println("Elasticsearch is available!");
                } else {
                    System.out.println("Elasticsearch ping failed, retrying...");
                    Thread.sleep(2000);
                }
            } catch (Exception e) {
                System.out.println("Elasticsearch not ready yet, retrying in 2s...");
                Thread.sleep(2000);
            }
        }
    }


    public void initializeIndexes()  throws IOException {

        boolean realestateExists = esClient.indices().exists(e -> e.index(INDEX_REALESTATE)).value();
        boolean cityExists = esClient.indices().exists(e -> e.index(INDEX_CITY)).value();
        boolean municipalityExists = esClient.indices().exists(e -> e.index(INDEX_MUNICIPALITY)).value();
        boolean regionExists = esClient.indices().exists(e -> e.index(INDEX_REGION)).value();
    
        // --- Realestate index ---
        if (!realestateExists) {
            createRealestateIndex();
            System.out.println("Index realestate created.");
        } else {
            if (args.containsOption("initMode")) {
                esClient.deleteByQuery(d -> d
                    .index(INDEX_REALESTATE)
                    .query(q -> q.matchAll(m -> m))
                    .refresh(true)
                );
                System.out.println("Index realestate emptied.");
            } else {
                System.out.println("Index realestate already exists, keeping data.");
            }
        }
    
        // --- City index ---
        if (!cityExists) {
            createCityIndex();
            System.out.println("Index city created.");
        } else {
            if (args.containsOption("initMode")) {
                esClient.deleteByQuery(d -> d
                    .index(INDEX_CITY)
                    .query(q -> q.matchAll(m -> m))
                    .refresh(true)
                );
                System.out.println("Index city emptied.");
            } else {
                System.out.println("Index city already exists, keeping data.");
            }
        }
    
        // --- Municipality index ---
        if (!municipalityExists) {
            createMunicipalityIndex();
            System.out.println("Index municipality created.");
        } else {
            if (args.containsOption("initMode")) {
                esClient.deleteByQuery(d -> d
                    .index(INDEX_MUNICIPALITY)
                    .query(q -> q.matchAll(m -> m))
                    .refresh(true)
                );
                System.out.println("Index municipality emptied.");
            } else {
                System.out.println("Index municipality already exists, keeping data.");
            }
        }
    
        // --- Region index ---
        if (!regionExists) {
            createRegionIndex();
            System.out.println("Index region created.");
        } else {
            if (args.containsOption("initMode")) {
                esClient.deleteByQuery(d -> d
                    .index(INDEX_REGION)
                    .query(q -> q.matchAll(m -> m))
                    .refresh(true)
                );
                System.out.println("Index region emptied.");
            } else {
                System.out.println("Index region already exists, keeping data.");
            }
        }
    }




    private void createRealestateIndex() throws IOException {
        esClient.indices().create(c -> c
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
                "address", new Property.Builder().text(new TextProperty.Builder()
                    .analyzer("autocomplete_index").searchAnalyzer("autocomplete_search").build()).build(),
                "cityDocId", new Property.Builder().keyword(new KeywordProperty.Builder().build()).build(),
                "municipalityDocId", new Property.Builder().keyword(new KeywordProperty.Builder().build()).build(),
                "regionDocId", new Property.Builder().keyword(new KeywordProperty.Builder().build()).build(),
                "vacant", new Property.Builder().boolean_(new BooleanProperty.Builder().build()).build()
            )))
        );
    }
    
    private void createCityIndex() throws IOException {
        esClient.indices().create(c -> c
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
                "city", new Property.Builder().text(new TextProperty.Builder()
                    .analyzer("autocomplete_index").searchAnalyzer("autocomplete_search").build()).build()
            )))
        );
    }
    
    private void createMunicipalityIndex() throws IOException {
        esClient.indices().create(c -> c
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
                "municipality", new Property.Builder().text(new TextProperty.Builder()
                    .analyzer("autocomplete_index").searchAnalyzer("autocomplete_search").build()).build()
            )))
        );
    }
    
    private void createRegionIndex() throws IOException {
        esClient.indices().create(c -> c
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
                "region", new Property.Builder().text(new TextProperty.Builder()
                    .analyzer("autocomplete_index").searchAnalyzer("autocomplete_search").build()).build()
            )))
        );
    }




    
}
