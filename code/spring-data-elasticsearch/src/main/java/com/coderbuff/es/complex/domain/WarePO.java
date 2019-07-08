package com.coderbuff.es.complex.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * ES mapping映射对应的PO
 * Created by OKevin on 2019-07-08
 **/
@Setter
@Getter
@ToString
@Document(indexName = "ware_index", type = "ware")
public class WarePO {

    /**
     * 主键ID
     */
    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    /**
     * 名称
     */
    @Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
    private String title;
}
