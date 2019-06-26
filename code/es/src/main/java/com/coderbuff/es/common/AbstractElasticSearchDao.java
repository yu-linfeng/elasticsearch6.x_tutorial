package com.coderbuff.es.common;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.util.ArrayList;
import java.util.List;

/**
 * ES查询抽象类
 * Created by OKevin on 2019-06-26
 **/
public abstract class AbstractElasticSearchDao implements ElasticSearchDao {

    @Override
    public <T> PageResult<T> search(SearchRequestBuilder searchRequestBuilder, Class<T> klass) {
        SearchHits searchHits = searchRequestBuilder.get().getHits();
        if (searchHits.getTotalHits() > 0) {
            List<T> dataList = new ArrayList<>();
            for (SearchHit searchHit : searchHits) {
                T data = JSON.parseObject(searchHit.getSourceAsString(), klass);
                dataList.add(data);
            }
            return PageResult.build(searchHits.getTotalHits(), dataList);
        }
        return PageResult.buildEmpty();
    }
}
