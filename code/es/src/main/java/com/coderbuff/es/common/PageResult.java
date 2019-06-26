package com.coderbuff.es.common;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OKevin on 2019-06-26
 **/
@Getter
@Setter
public class PageResult<T> {
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

    private PageResult(Integer page, Integer pageSize, Long count, List<T> data) {
        this.page = page;
        this.pageSize = pageSize;
        this.count = count;
        this.data = data;
    }

    public static <T> PageResult<T> buildEmpty() {
        return new PageResult<T>(0, 0, 0L, new ArrayList<>());
    }

    public static <T> PageResult<T> build(Long count, List<T> data) {
        return new PageResult<>(0, 0, count, data);
    }
}
