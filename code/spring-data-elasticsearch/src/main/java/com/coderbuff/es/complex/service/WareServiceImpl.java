package com.coderbuff.es.complex.service;

import com.coderbuff.es.complex.dao.EmployeeRepository;
import com.coderbuff.es.complex.dao.WareRepository;
import com.coderbuff.es.complex.domain.WarePO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by OKevin on 2019-07-08 22:54
 */
@Service
public class WareServiceImpl implements WareService {

    @Autowired
    private WareRepository wareRepository;

    @Override
    public void batchInsertWare(List<WarePO> warePOList) {
        wareRepository.saveAll(warePOList);
    }

    @Override
    public List<WarePO> search(SearchQuery searchQuery) {
        return wareRepository.search(searchQuery).getContent();
    }
}
