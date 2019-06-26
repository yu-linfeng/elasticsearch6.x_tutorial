package com.coderbuff.es.easy;

import com.coderbuff.es.common.PageResult;
import com.coderbuff.es.easy.po.Student;
import com.coderbuff.es.easy.service.StudentElasticSearchDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by OKevin on 2019-06-26
 **/
public class StudentElasticSearchDaoTest {

    @Autowired
    private StudentElasticSearchDao studentElasticSearchDao;

    /**
     * 无条件分页查询
     * @return 查询结果
     */
    public PageResult<Student> queryStudent() {
        return null;
    }
}
