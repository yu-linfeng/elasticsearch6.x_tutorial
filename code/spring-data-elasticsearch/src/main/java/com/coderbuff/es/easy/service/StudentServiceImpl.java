package com.coderbuff.es.easy.service;

import com.coderbuff.es.easy.dao.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by OKevin on 2019-06-26 23:44
 */
@Service
public class StudentServiceImpl implements StudentService{

    @Autowired
    private StudentRepository studentRepository;

}
