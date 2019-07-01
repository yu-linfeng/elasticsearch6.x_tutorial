package com.coderbuff.transportclientelasticsearch.easy.service;

import com.coderbuff.transportclientelasticsearch.easy.dao.StudentRepository;
import com.coderbuff.transportclientelasticsearch.easy.domain.StudentPO;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by OKevin on 2019-07-01 22:30
 */
@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public void batchInsertStudentPO(List<StudentPO> studentPOList) {
        studentRepository.batchInsert(studentPOList);
    }

    @Override
    public void batchUpdateStudentPO(List<StudentPO> studentPOList) {
        studentRepository.batchUpdate(studentPOList);
    }

    @Override
    public void batchDeleteStudentPO(List<StudentPO> studentPOList) {
        studentRepository.batchDelete(studentPOList);
    }

    @Override
    public List<StudentPO> search(SearchRequestBuilder searchRequestBuilder) {
        return studentRepository.search(searchRequestBuilder);
    }
}
