package com.example.demo.resource;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import com.example.demo.service.ElasticsearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping
public class ElasticsearchController {

    private final ElasticsearchService elasticsearchService;
    private static final Logger LOG = LoggerFactory.getLogger(ElasticsearchService.class);

    @Autowired
    public ElasticsearchController(ElasticsearchService elasticsearchService) {
        this.elasticsearchService = elasticsearchService;
    }

    @PostMapping("/index")
    public void createIndex(@RequestBody String indexName) {
        try {
            elasticsearchService.createIndex(indexName);
        } catch (ElasticsearchException e) {
            if (Objects.equals(e.response().error().type(), "resource_already_exists_exception")) {
                LOG.error("Index with name " + indexName + " already exists");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An index with this name already exists");
            }
        } catch (IOException e) {
            LOG.error("Unsuccessful ES index with message: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "There has been a problem with the internal database.");
        }
    }

    @PutMapping("/index/{indexName}/document")
    public void createDocument(@PathVariable String indexName, @RequestBody HashMap<String, String> document) {
        try {
            elasticsearchService.indexDocument(indexName, document);
        } catch (IOException e) {
            LOG.error("Unsuccessful ES index with message: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "There has been a problem with the internal database.");
        }
    }

    @GetMapping("/index/{indexName}/document/{documentId}")
    public Map searchDocument(@PathVariable String indexName, @PathVariable String documentId) {
        try {
            return elasticsearchService.searchDocument(indexName, documentId);
        } catch (IOException e) {
            LOG.error("Unsuccessful ES search with message: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "There has been a problem with the internal database.");
        }
    }
}