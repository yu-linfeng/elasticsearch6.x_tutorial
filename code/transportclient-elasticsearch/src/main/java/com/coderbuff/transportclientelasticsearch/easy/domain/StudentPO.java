package com.coderbuff.transportclientelasticsearch.easy.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by OKevin on 2019-06-30 13:34
 */
@Setter
@Getter
@ToString
public class StudentPO {
    private String id;
    private String name;
    private Integer  age;
}
