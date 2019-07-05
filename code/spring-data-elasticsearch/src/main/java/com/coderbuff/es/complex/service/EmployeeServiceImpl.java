package com.coderbuff.es.complex.service;

import com.coderbuff.es.complex.dao.EmployeeRepository;
import com.coderbuff.es.complex.domain.EmployeePO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by OKevin on 2019-07-05
 */
@Service
public class EmployeeServiceImpl implements EmployeeService{

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public void batchInsertEmployee(List<EmployeePO> employeePOList) {
        employeeRepository.saveAll(employeePOList);
    }

    @Override
    public void batchInsertEmployeeSon(List<EmployeePO> employeePOListSon, String routing) {
        Client client = elasticsearchTemplate.getClient();
        ObjectMapper mapper = new ObjectMapper();
        try {
            for (EmployeePO employeePO : employeePOListSon) {
                BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
                bulkRequestBuilder.add(client.prepareIndex("company", "employee", employeePO.getId()).setRouting(routing).setSource(mapper.writeValueAsString(employeePO), XContentType.JSON)).execute().actionGet();
            }

        } catch (JsonProcessingException e) {
        }
    }

    @Override
    public List<EmployeePO> search(SearchQuery searchQuery) {
        return employeeRepository.search(searchQuery).getContent();
    }

    @Override
    public Page<EmployeePO> searchWithPage(SearchQuery searchQuery) {
        return employeeRepository.search(searchQuery);
    }
}
