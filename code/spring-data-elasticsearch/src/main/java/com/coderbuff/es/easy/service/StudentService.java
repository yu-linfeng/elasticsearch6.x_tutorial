package com.coderbuff.es.easy.service;

import com.coderbuff.es.easy.domain.StudentPO;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.util.List;

/**
 * Created by OKevin on 2019-06-26 23:43
 */
public interface StudentService {

    /**
     * 批量插入StudentPO
     * @param studentPOList  StudentPO List
     */
    void batchInsertStudentPO(List<StudentPO> studentPOList);

    /**
     * 批量修改StudentPO
     * @param studentPOList Student PO
     */
    void batchUpdateStudentPO(List<StudentPO> studentPOList);

    /**
     * 通过ES id批量删除
     * @param studentPOList StudentPO List
     */
    void batchDeleteStudentPO(List<StudentPO> studentPOList);

    /**
     * 不分页搜索
     * @param searchQuery 查询条件
     * @return 不分页数据
     */
    List<StudentPO> search(SearchQuery searchQuery);

    /**
     * 分页搜索
     * @param searchQuery 查询条件
     * @return 分页数据
     */
    Page<StudentPO> searchWithPage(SearchQuery searchQuery);
}
