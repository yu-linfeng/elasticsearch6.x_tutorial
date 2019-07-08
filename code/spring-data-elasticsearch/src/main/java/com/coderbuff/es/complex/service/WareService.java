package com.coderbuff.es.complex.service;

import com.coderbuff.es.complex.domain.WarePO;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.util.List;

/**
 * Created by OKevin on 2019-07-08 22:48
 */
public interface WareService {

    /**
     * 批量插入
     * @param warePOList WarePO List
     */
    void batchInsertWare(List<WarePO> warePOList);

    /**
     * 不分页搜索
     * @param searchQuery 查询条件
     * @return WarePO List
     */
    List<WarePO> search(SearchQuery searchQuery);
}
