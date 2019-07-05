package com.coderbuff.es.complex.dao;

import com.coderbuff.es.complex.domain.EmployeePO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by OKevin on 2019-06-26 23:45
 */
@Repository
public interface EmployeeRepository extends ElasticsearchRepository<EmployeePO, String> {
}
