package com.example.nvt.configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.analysis.TokenChar;
import co.elastic.clients.elasticsearch._types.mapping.*;
import co.elastic.clients.elasticsearch.core.search.CompletionContext;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.json.JsonpMapper;
import jakarta.annotation.PostConstruct;
import jakarta.json.stream.JsonGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ElasticsearchIndexConfig {

    private final ElasticsearchClient esClient;
    private static final String INDEX_NAME = "realestate";

    @PostConstruct
    public void createIndexIfNotExists() throws IOException {
        boolean indexExists = esClient.indices().exists(e -> e.index("realestate")).value();


        if (indexExists) {
            esClient.indices().delete(DeleteIndexRequest.of(d -> d.index("realestate")));
            System.out.println("Index realestate deleted.");
        } else {
            System.out.println("Index realestate does not exist.");
        }

        System.out.println("DA LI SE OVO UPALILO ");


        CreateIndexResponse response = esClient.indices().create(c -> c
                .index(INDEX_NAME)
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
                                                                .preserveOriginal(false))))
//                                .tokenizer("edge_ngram_tokenizer", t -> t
//                                        .definition(td -> td
//                                                .edgeNgram(e -> e
//                                                        .minGram(1)
//                                                        .maxGram(20)
//                                                        .tokenChars(List.of(TokenChar.Letter, TokenChar.Digit, TokenChar.Whitespace))
//                                                )
//
//                                        )
//                                )
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
                                "address", new Property.Builder().text(new TextProperty.Builder().analyzer("autocomplete_index").searchAnalyzer("autocomplete_search").build()).build(),
                                "city", new Property.Builder().text(new TextProperty.Builder().analyzer("autocomplete_index").searchAnalyzer("autocomplete_search").build()).build(),
                                "municipality", new Property.Builder().text(new TextProperty.Builder().analyzer("autocomplete_index").searchAnalyzer("autocomplete_search").build()).build(),
                                "region", new Property.Builder().text(new TextProperty.Builder().analyzer("autocomplete_index").searchAnalyzer("autocomplete_search").build()).build(),
                                "zipcode", new Property.Builder().text(new TextProperty.Builder().analyzer("autocomplete_index").searchAnalyzer("autocomplete_search").build()).build(),
                                "fullAddress", new Property.Builder().text(new TextProperty.Builder().analyzer("autocomplete_index").searchAnalyzer("autocomplete_search").build()).build()
                        )
                ))
        );

        if (response.acknowledged()) {
            System.out.println("Index '" + INDEX_NAME + "' created successfully.");
        } else {
            System.out.println("Failed to create index.");
        }

    }
}
//
//CreateIndexResponse response = esClient.indices().create(c -> c
//        .index(INDEX_NAME)
//        .settings(IndexSettings.of(s -> s
//                .analysis(a -> a
//                        .tokenizer("edge_ngram_tokenizer", t -> t
//                                .definition(td -> td
//                                        .edgeNgram(e -> e
//                                                .minGram(1)
//                                                .maxGram(20)
//                                                .tokenChars(List.of(TokenChar.Letter, TokenChar.Digit, TokenChar.Whitespace))
//                                        )
//
//                                )
//                        )
//                        .analyzer("autocomplete_analyzer", an -> an
//                                .custom(cust -> cust
//                                        .tokenizer("edge_ngram_tokenizer")
//                                        .filter("lowercase"))
//                        )
//                )
//        ))
//        .mappings(m -> m.properties(Map.of(
//                        "street", new Property.Builder().text(new TextProperty.Builder().analyzer("autocomplete_analyzer").build()).build(),
//                        "city", new Property.Builder().text(new TextProperty.Builder().analyzer("autocomplete_analyzer").build()).build(),
//                        "municipality", new Property.Builder().text(new TextProperty.Builder().analyzer("autocomplete_analyzer").build()).build(),
//                        "region", new Property.Builder().text(new TextProperty.Builder().analyzer("autocomplete_analyzer").build()).build()
//                )
//        ))
//);