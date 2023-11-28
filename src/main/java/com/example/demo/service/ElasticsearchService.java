package com.example.demo.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ElasticsearchService {

    private final ElasticsearchClient client;

    public ElasticsearchService(ElasticsearchClient client) {
        this.client = client;
    }

    public void createIndex(String index) throws ElasticsearchException, IOException {
        this.client.indices().create(request -> request.index(index));
    }

    public void indexDocument(String index, HashMap<String, String> document) throws IOException {
        this.client.index(i -> i.index(index).document(document));
    }

    public Map searchDocument(String index, String documentId) throws IOException {
        final var result = this.client.search(searchRequest -> searchRequest
                .index(index)
                .query(queryBuilder ->
                        queryBuilder.match(matchQBuilder ->
                                matchQBuilder.field("id")
                                        .query(documentId))), Map.class
        );

        return result.hits().hits().stream()
                .map(Hit::source)
                .findFirst()
                .orElse(Map.of());
    }
}