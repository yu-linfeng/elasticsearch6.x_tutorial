package com.coderbuff.es.complex.service;

import com.coderbuff.es.complex.domain.EmployeePO;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.util.List;

/**
 * Created by OKevin on 2019-07-05
 */
public interface EmployeeService {

    /**
     * 批量插入
     * @param employeePOList Employee List
     */
    void batchInsertEmployee(List<EmployeePO> employeePOList);

    /**
     * 批量插入子文档
     * @param employeePOListSon Employee List
     */
    void batchInsertEmployeeSon(List<EmployeePO> employeePOListSon, String routing);

    /**
     * 不分页搜索
     * @param searchQuery 查询条件
     * @return 不分页数据
     */
    List<EmployeePO> search(SearchQuery searchQuery);

    /**
     * 分页搜索
     * @param searchQuery 查询条件
     * @return 分页数据
     */
    Page<EmployeePO> searchWithPage(SearchQuery searchQuery);
}
