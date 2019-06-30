package com.coderbuff.transportclientelasticsearch.common;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by OKevin on 2019-06-30 13:38
 */
public interface ElasticSearchDao {

    /**
     * 创建Indx
     * @param index 索引名称
     */
    void createIndex(String index);

    /**
     * 创建Type
     * @param type 类型名称
     */
    void createType(String type);

    /**
     * 创建Mapping
     * @param mapping 映射
     */
    void createMapping(String index, String type, Map<String, Object> mapping) throws JsonProcessingException, ExecutionException, InterruptedException;

}
