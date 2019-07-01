package com.coderbuff.transportclientelasticsearch.common;

import com.coderbuff.transportclientelasticsearch.config.ElasticSearchClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.Requests;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by OKevin on 2019-06-30 13:42
 */
public abstract class AbstractElasticSearchDao implements ElasticSearchDao {

    @Autowired
    protected ElasticSearchClient elasticSearchClient;

    protected static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void createIndex(String index) {
        elasticSearchClient.getClient().admin().indices().prepareCreate(index).get();
    }

    @Override
    public void createType(String type) {

    }

    @Override
    public void createMapping(String index, String type, Map<String, Object> mapping) throws JsonProcessingException, ExecutionException, InterruptedException {
        String json = MAPPER.writeValueAsString(mapping);
        PutMappingRequest request = Requests.putMappingRequest(index).type(type).source(json);
        elasticSearchClient.getClient().admin().indices().putMapping(request).get();
    }
}
