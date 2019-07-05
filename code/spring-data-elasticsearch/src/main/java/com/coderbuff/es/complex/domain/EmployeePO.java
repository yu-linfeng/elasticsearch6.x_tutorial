package com.coderbuff.es.complex.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;

import java.util.List;
import java.util.Map;

/**
 * ES mapping映射对应的PO
 * Created by OKevin on 2019-07-05
 */
@Getter
@Setter
@ToString
@Document(indexName = "company", type = "employee")
@Mapping(mappingPath = "employee_mapping.json")
public class EmployeePO {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别
     */
    private String sex;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 出生年月日
     */
    private String birthday;

    /**
     * 职位
     */
    private String position;

    /**
     * 上级、下级，父子关系文档
     */
    private Map<String, Object> level;

    /**
     * 所在部门
     */
    private List<String> departments;

    /**
     * 加入公司时间
     */
    private String joinTime;

    /**
     * 修改时间
     */
    private Long modified;

    /**
     * 创建时间
     */
    private Long created;
}
