package com.coderbuff.es;

import com.coderbuff.es.easy.domain.StudentPO;
import com.coderbuff.es.easy.service.StudentService;
import com.google.common.collect.Lists;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringDataElasticsearchApplicationTests {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private StudentService studentService;

    /**
     * 测试创建Index，type和Mapping定义
     */
    @Test
    public void createIndex() {
        elasticsearchTemplate.createIndex(StudentPO.class);
        elasticsearchTemplate.putMapping(StudentPO.class);
    }

    /**
     * 测试批量插入
     */
    @Test
    public void testBatchInsert() {
        List<StudentPO> studentPOList = Lists.newArrayList();
        StudentPO studentPO1 = new StudentPO();
        studentPO1.setId("1");
        studentPO1.setName("kevin");
        studentPO1.setAge(25);

        StudentPO studentPO2 = new StudentPO();
        studentPO2.setId("2");
        studentPO2.setName("kangkang");
        studentPO2.setAge(26);

        StudentPO studentPO3 = new StudentPO();
        studentPO3.setId("3");
        studentPO3.setName("mike");
        studentPO3.setAge(22);

        StudentPO studentPO4 = new StudentPO();
        studentPO4.setId("4");
        studentPO4.setName("kevin2");
        studentPO4.setAge(25);

        StudentPO studentPO5 = new StudentPO();
        studentPO5.setId("5");
        studentPO5.setName("kevin yu");
        studentPO5.setAge(21);
        studentPOList.add(studentPO1);
        studentPOList.add(studentPO2);
        studentPOList.add(studentPO3);
        studentPOList.add(studentPO4);
        studentPOList.add(studentPO5);
        studentService.batchInsertStudentPO(studentPOList);
    }

    /**
     * 无条件不分页按ID排序全量查询
     */
    @Test
    public void testNoConditionSearch() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withSort(SortBuilders.fieldSort("id")).build();
        List<StudentPO> studentPOList = studentService.search(searchQuery);
        for (int i = 0; i < studentPOList.size(); i++) {
            Assert.assertEquals(studentPOList.get(i).getId(), String.valueOf(i+1));
        }
    }

    /**
     * 不分页term查询name="kevin"。
     * 期待结果：name=kevin和name=kevin yu。
     */
    @Test
    public void testTermQuerySearch() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("name", "kevin")).build();
        System.out.println(studentService.search(searchQuery));
    }

    /**
     * 不分页match查询name="kevin"。
     * 期待结果：name=kevin和name=kevin yu。
     */
    @Test
    public void testMatchQuerySearch() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("name", "kevin")).build();
        System.out.println(studentService.search(searchQuery));
    }

    /**
     * 不分页term查询name.keyword字段（不分词）name="kevin yu"
     * 期待结果：name=kevin yu
     */
    @Test
    public void testTermQueryKeywordSearch() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("name.keyword", "kevin yu")).build();
        System.out.println(studentService.search(searchQuery));
    }

    /**
     * 不分页match查询name.keyword字段（不分词）name="kevin yu"
     * 期待结果：name=kevin yu
     */
    @Test
    public void testMatchQueryKeywordSearch() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("name.keyword", "kevin yu")).build();
        System.out.println(studentService.search(searchQuery));
    }


    /**
     * 不分页wildcard查询name="kevin"
     * 期待结果：name=kevin，name=kevin2，name=kevin yu
     */
    @Test
    public void testWildcardSearch() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.wildcardQuery("name", "*kevin*")).build();
        System.out.println(studentService.search(searchQuery));
    }

    /**
     * 不分页fuzzy查询name="kevon"
     * 期待结果：name=kevin，name=kevin yu
     */
    @Test
    public void testFuzzySearch() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.fuzzyQuery("name", "kevon")).build();
        System.out.println(studentService.search(searchQuery));
    }

    /**
     * 不分页term多条件"and"查询name="kevin"且age=25
     * 期待结果：name=kevin
     */
    @Test
    public void testTermQueryAndSearch() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery("name", "kevin"))
                        .must(QueryBuilders.termQuery("age", 25))).build();
        System.out.println(studentService.search(searchQuery));
    }

    /**
     * 不分页term多条件"or"查询name="kevin"或age=21
     * 期待结果：name=kevin，name=kevin yu
     */
    @Test
    public void testTermQueryOrSearch() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.boolQuery()
                        .should(QueryBuilders.termQuery("name", "kevin"))
                        .should(QueryBuilders.termQuery("age", 21))).build();
        System.out.println(studentService.search(searchQuery));
    }

    /**
     * 不分页term多条件"not"查询name!=kevin且age=25
     * 期待结果：name=kevin2
     */
    @Test
    public void testTermQueryNotSearch() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.boolQuery()
                        .mustNot(QueryBuilders.termQuery("name", "kevin"))
                        .must(QueryBuilders.termQuery("age", 25))).build();
        System.out.println(studentService.search(searchQuery));
    }

    /**
     * 不分页range范围查询age>25
     * 期待结果：name=kangkang
     */
    @Test
    public void testRangeGtQuerySearch() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.rangeQuery("age").gt(25)).build();
        System.out.println(studentService.search(searchQuery));
    }

    /**
     * 不分页range范围查询age>=21且age<26
     * 期待结果：name=kevin，name=mike，name=kevin2，name=kevin yu
     */
    @Test
    public void testRangeGteLtQuerySearch() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.rangeQuery("age").gte(21).lt(26)).build();
        System.out.println(studentService.search(searchQuery));
    }

    /**
     * 不分页range范围和term查询age>=21且age<26且name=kevin
     * 期待结果：name=kevin，name=kevin yu（因为是name字段，它会被分词。如果是name.keyword，则只有name=kevin结果）
     */
    @Test
    public void testRangeAndTermQuerySearch() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.boolQuery()
                        .must(QueryBuilders.rangeQuery("age").gte(21).lt(26))
                        .must(QueryBuilders.termQuery("name", "kevin"))).build();
        System.out.println(studentService.search(searchQuery));
    }

    /**
     * 不分页exists存在查询，存在name字段的数据
     * 期待结果：所有数据
     */
    @Test
    public void testExistsQuerySearch() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.existsQuery("name")).build();
        System.out.println(studentService.search(searchQuery));
    }

    /**
     * 不分页not exists不存在查询（以前是missing，后来missing被移除），不存在name字段的数据
     * 期待结果：没有数据
     */
    @Test
    public void testNotExistsQuerySearch() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.boolQuery()
                        .mustNot(QueryBuilders.existsQuery("name"))).build();
        System.out.println(studentService.search(searchQuery));
    }

    /**
     * 分页match查询name=kevin且按id排序（默认升序）
     * 期待结果：name=kevin
     */
    @Test
    public void testMatchQueryPageableSearch() {
        Pageable pageable = PageRequest.of(0, 1);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("name", "kevin"))
                .withSort(SortBuilders.fieldSort("id"))
                .withPageable(pageable).build();
        System.out.println(studentService.search(searchQuery).get(0));
    }

    /**
     * 分页range范围查询age>=21且age<26，并按age降序排列
     * 期待结果：name=kevin2
     */
    @Test
    public void testMatchQueryPageableDescSearch() {
        Pageable pageable = PageRequest.of(0, 1, new Sort(Sort.Direction.DESC, "age"));
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.rangeQuery("age").gte(21).lt(26))
                .withSort(SortBuilders.fieldSort("age"))
                .withPageable(pageable).build();
        System.out.println(studentService.search(searchQuery).get(0));
    }

    /**
     * 测试批量更新
     */
    @Test
    public void testBatchUpdate() {
        StudentPO studentPO = new StudentPO();
        studentPO.setId("4");
        studentPO.setName("kevin2");
        studentPO.setAge(1);
        studentService.batchUpdateStudentPO(Lists.newArrayList(studentPO));
    }

    /**
     * 测试批量删除
     */
    @Test
    public void testBatchDelete() {
        StudentPO studentPO = new StudentPO();
        studentPO.setId("2");
        studentService.batchDeleteStudentPO(Lists.newArrayList(studentPO));
    }
}
