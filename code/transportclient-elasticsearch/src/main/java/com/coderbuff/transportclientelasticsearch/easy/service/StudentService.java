package com.coderbuff.transportclientelasticsearch.easy.service;

import com.coderbuff.transportclientelasticsearch.easy.domain.StudentPO;
import org.elasticsearch.action.search.SearchRequestBuilder;

import java.util.List;

/**
 * Created by OKevin on 2019-07-01 22:32
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
     * 不分页查询
     * @param searchRequestBuilder 查询条件
     * @return StudentPO List
     */
    List<StudentPO> search(SearchRequestBuilder searchRequestBuilder);

}
