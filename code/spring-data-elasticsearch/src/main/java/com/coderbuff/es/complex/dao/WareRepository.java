package com.coderbuff.es.complex.dao;

import com.coderbuff.es.complex.domain.WarePO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by OKevin on 2019-07-08 22:45
 */
@Repository
public interface WareRepository extends ElasticsearchRepository<WarePO, String> {

}
