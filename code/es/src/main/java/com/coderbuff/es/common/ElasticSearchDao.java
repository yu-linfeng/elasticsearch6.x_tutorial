package com.coderbuff.es.common;

import org.elasticsearch.action.search.SearchRequestBuilder;

/**
 * ES查询
 * Created by OKevin on 2019-06-26
 **/
public interface ElasticSearchDao {
    /**
     * 搜索
     * @param searchRequestBuilder 搜索条件构造类
     * @param <T> 搜索类型
     * @return 搜索数据
     */
    <T> PageResult<T> search(SearchRequestBuilder searchRequestBuilder, Class<T> klass);
}
