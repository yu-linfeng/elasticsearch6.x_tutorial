package com.coderbuff.es.easy.service;

import com.coderbuff.es.easy.dao.StudentRepository;
import com.coderbuff.es.easy.domain.StudentPO;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created by OKevin on 2019-06-26 23:44
 */
@Service
public class StudentServiceImpl implements StudentService{

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public void batchInsertStudentPO(List<StudentPO> studentPOList) {
        studentRepository.saveAll(studentPOList);
    }

    @Override
    public void batchUpdateStudentPO(List<StudentPO> studentPOList) {
        for (StudentPO studentPO : studentPOList) {
            Optional<StudentPO> studentPOOpt = studentRepository.findById(studentPO.getId());
            if (studentPOOpt.isPresent()) {
                studentRepository.save(studentPO);
            }
        }
    }

    @Override
    public void batchDeleteStudentPO(List<StudentPO> studentPOList) {
        studentRepository.deleteAll(studentPOList);
    }

    @Override
    public List<StudentPO> search(SearchQuery searchQuery) {
        return studentRepository.search(searchQuery).getContent();
    }

    @Override
    public Page<StudentPO> searchWithPage(SearchQuery searchQuery) {
        return studentRepository.search(searchQuery);
    }
}
