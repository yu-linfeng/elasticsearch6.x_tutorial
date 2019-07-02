package com.coderbuff.transportclientelasticsearch.common;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Created by OKevin on 2019-07-02 22:21
 */
@Setter
@Getter
@ToString
public class Page<T> {
    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 一页多少数据
     */
    private Integer pageSize;

    /**
     * 一共有多少数据
     */
    private Long count;

    /**
     * 数据
     */
    private List<T> data;

    private Page(Integer page, Integer pageSize, Long count, List<T> data) {
        this.page = page;
        this.pageSize = pageSize;
        this.count = count;
        this.data = data;
    }

    public static <T> Page<T> buildEmpty() {
        return new Page<>(0, 0, 0L, Lists.newArrayList());
    }

    public static <T> Page<T> build(Long count, List<T> data) {
        return new Page<>(0, 0, count, data);
    }
}
