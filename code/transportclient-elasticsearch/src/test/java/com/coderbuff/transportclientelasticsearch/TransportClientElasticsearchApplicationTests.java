package com.coderbuff.transportclientelasticsearch;

import com.coderbuff.transportclientelasticsearch.common.Page;
import com.coderbuff.transportclientelasticsearch.config.ElasticSearchClient;
import com.coderbuff.transportclientelasticsearch.easy.domain.StudentPO;
import com.coderbuff.transportclientelasticsearch.easy.service.StudentService;
import com.google.common.collect.Lists;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransportClientElasticsearchApplicationTests {

    @Autowired
    private StudentService studentService;

    @Autowired
    private ElasticSearchClient elasticSearchClient;

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
        studentService.batchInsertStudentPO(Lists.newArrayList(studentPOList));
    }

    /**
     * 无条件不分页按ID排序全量查询
     */
    @Test
    public void testNoConditionSearch() {
        SearchRequestBuilder searchRequestBuilder = elasticSearchClient.getClient()
                .prepareSearch("user")
                .setQuery(QueryBuilders.matchAllQuery())
                .addSort("id", SortOrder.ASC);
        List<StudentPO> studentPOList = studentService.search(searchRequestBuilder);
        for (int i = 0; i < studentPOList.size(); i++) {
            Assert.assertEquals(studentPOList.get(i).getId(), String.valueOf(i + 1));
        }
    }

    /**
     * 不分页term查询name="kevin"。
     * 期待结果：name=kevin和name=kevin yu。
     */
    @Test
    public void testTermQuerySearch() {
        SearchRequestBuilder searchRequestBuilder = elasticSearchClient.getClient()
                .prepareSearch("user")
                .setQuery(QueryBuilders.termQuery("name", "kevin"));
        List<StudentPO> studentPOList = studentService.search(searchRequestBuilder);
        System.out.println(studentPOList);
    }

    /**
     * 不分页match查询name="kevin"。
     * 期待结果：name=kevin和name=kevin yu。
     */
    @Test
    public void testMatchQuerySearch() {
        SearchRequestBuilder searchRequestBuilder = elasticSearchClient.getClient()
                .prepareSearch("user")
                .setQuery(QueryBuilders.matchQuery("name", "kevin"));
        List<StudentPO> studentPOList = studentService.search(searchRequestBuilder);
        System.out.println(studentPOList);
    }

    /**
     * 不分页term查询name.keyword字段（不分词）name="kevin yu"
     * 期待结果：name=kevin yu
     */
    @Test
    public void testTermQueryKeywordSearch() {
        SearchRequestBuilder searchRequestBuilder = elasticSearchClient.getClient()
                .prepareSearch("user")
                .setQuery(QueryBuilders.termQuery("name.keyword", "kevin yu"));
        List<StudentPO> studentPOList = studentService.search(searchRequestBuilder);
        System.out.println(studentPOList);
    }

    /**
     * 不分页match查询name.keyword字段（不分词）name="kevin yu"
     * 期待结果：name=kevin yu
     */
    @Test
    public void testMatchQueryKeywordSearch() {
        SearchRequestBuilder searchRequestBuilder = elasticSearchClient.getClient()
                .prepareSearch("user")
                .setQuery(QueryBuilders.matchQuery("name.keyword", "kevin yu"));
        List<StudentPO> studentPOList = studentService.search(searchRequestBuilder);
        System.out.println(studentPOList);
    }

    /**
     * 不分页wildcard查询name="kevin"
     * 期待结果：name=kevin，name=kevin2，name=kevin yu
     */
    @Test
    public void testWildcardSearch() {
        SearchRequestBuilder searchRequestBuilder = elasticSearchClient.getClient()
                .prepareSearch("user")
                .setQuery(QueryBuilders.wildcardQuery("name", "*kevin*"));
        List<StudentPO> studentPOList = studentService.search(searchRequestBuilder);
        System.out.println(studentPOList);
    }

    /**
     * 不分页fuzzy查询name="kevon"
     * 期待结果：name=kevin，name=kevin yu
     */
    @Test
    public void testFuzzySearch() {
        SearchRequestBuilder searchRequestBuilder = elasticSearchClient.getClient()
                .prepareSearch("user")
                .setQuery(QueryBuilders.fuzzyQuery("name", "kevon"));
        List<StudentPO> studentPOList = studentService.search(searchRequestBuilder);
        System.out.println(studentPOList);
    }

    /**
     * 不分页term多条件"and"查询name="kevin"且age=25
     * 期待结果：name=kevin
     */
    @Test
    public void testTermQueryAndSearch() {
        SearchRequestBuilder searchRequestBuilder = elasticSearchClient.getClient()
                .prepareSearch("user")
                .setQuery(QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery("name", "kevin"))
                        .must(QueryBuilders.termQuery("age", "25")));

        List<StudentPO> studentPOList = studentService.search(searchRequestBuilder);
        System.out.println(studentPOList);
    }

    /**
     * 不分页term多条件"or"查询name="kevin"或age=21
     * 期待结果：name=kevin，name=kevin yu
     */
    @Test
    public void testTermQueryOrSearch() {
        SearchRequestBuilder searchRequestBuilder = elasticSearchClient.getClient()
                .prepareSearch("user")
                .setQuery(QueryBuilders.boolQuery()
                        .should(QueryBuilders.termQuery("name", "kevin"))
                        .should(QueryBuilders.termQuery("age", "21")));
        List<StudentPO> studentPOList = studentService.search(searchRequestBuilder);
        System.out.println(studentPOList);
    }

    /**
     * 不分页term多条件"not"查询name!=kevin且age=25
     * 期待结果：name=kevin2
     */
    @Test
    public void testTermQueryNotSearch() {
        SearchRequestBuilder searchRequestBuilder = elasticSearchClient.getClient()
                .prepareSearch("user")
                .setQuery(QueryBuilders.boolQuery()
                        .mustNot(QueryBuilders.termQuery("name", "kevin"))
                        .must(QueryBuilders.termQuery("age", "25")));
        List<StudentPO> studentPOList = studentService.search(searchRequestBuilder);
        System.out.println(studentPOList);
    }

    /**
     * 不分页range范围查询age>25
     * 期待结果：name=kangkang
     */
    @Test
    public void testRangeGtQuerySearch() {
        SearchRequestBuilder searchRequestBuilder = elasticSearchClient.getClient()
                .prepareSearch("user")
                .setQuery(QueryBuilders.rangeQuery("age").gt("25"));
        List<StudentPO> studentPOList = studentService.search(searchRequestBuilder);
        System.out.println(studentPOList);
    }

    /**
     * 不分页range范围查询age>=21且age<26
     * 期待结果：name=kevin，name=mike，name=kevin2，name=kevin yu
     */
    @Test
    public void testRangeGteLtQuerySearch() {
        SearchRequestBuilder searchRequestBuilder = elasticSearchClient.getClient()
                .prepareSearch("user")
                .setQuery(QueryBuilders.rangeQuery("age").gte("21").lt("26"));
        List<StudentPO> studentPOList = studentService.search(searchRequestBuilder);
        System.out.println(studentPOList);
    }

    /**
     * 不分页range范围和term查询age>=21且age<26且name=kevin
     * 期待结果：name=kevin，name=kevin yu（因为是name字段，它会被分词。如果是name.keyword，则只有name=kevin结果）
     */
    @Test
    public void testRangeAndTermQuerySearch() {
        SearchRequestBuilder searchRequestBuilder = elasticSearchClient.getClient()
                .prepareSearch("user")
                .setQuery(QueryBuilders.boolQuery()
                        .must(QueryBuilders.rangeQuery("age").gte("21").lt("26"))
                        .must(QueryBuilders.termQuery("name", "kevin")));
        List<StudentPO> studentPOList = studentService.search(searchRequestBuilder);
        System.out.println(studentPOList);
    }

    /**
     * 不分页exists存在查询，存在name字段的数据
     * 期待结果：所有数据
     */
    @Test
    public void testExistsQuerySearch() {
        SearchRequestBuilder searchRequestBuilder = elasticSearchClient.getClient()
                .prepareSearch("user")
                .setQuery(QueryBuilders.existsQuery("name"));
        List<StudentPO> studentPOList = studentService.search(searchRequestBuilder);
        System.out.println(studentPOList);
    }

    /**
     * 不分页not exists不存在查询（以前是missing，后来missing被移除），不存在name字段的数据
     * 期待结果：没有数据
     */
    @Test
    public void testNotExistsQuerySearch() {
        SearchRequestBuilder searchRequestBuilder = elasticSearchClient.getClient()
                .prepareSearch("user")
                .setQuery(QueryBuilders.boolQuery()
                        .mustNot(QueryBuilders.existsQuery("name")));
        List<StudentPO> studentPOList = studentService.search(searchRequestBuilder);
        System.out.println(studentPOList);
    }

    /**
     * 分页match查询name=kevin且按id排序（默认升序）
     * 期待结果：name=kevin
     */
    @Test
    public void testMatchQueryPageableSearch() {
        int from = 0;
        int size = 1;
        SearchRequestBuilder searchRequestBuilder = elasticSearchClient.getClient()
                .prepareSearch("user")
                .setQuery(QueryBuilders.matchQuery("name", "kevin"))
                .addSort("id", SortOrder.ASC)
                .setFrom(from)
                .setSize(size);
        Page<StudentPO> result = studentService.searchWithPage(searchRequestBuilder);
        result.setPage(from + 1);
        result.setPageSize(size);
        System.out.println(result);
    }

    /**
     * 分页range范围查询age>=21且age<26，并按age降序排列
     * 期待结果：name=kevin2
     */
    @Test
    public void testMatchQueryPageableDescSearch() {
        int from = 0;
        int size = 1;
        SearchRequestBuilder searchRequestBuilder = elasticSearchClient.getClient()
                .prepareSearch("user")
                .setQuery(QueryBuilders.rangeQuery("age").gte("21").lt(26))
                .addSort("age", SortOrder.DESC)
                .setFrom(from)
                .setSize(size);
        Page<StudentPO> result = studentService.searchWithPage(searchRequestBuilder);
        result.setPage(from + 1);
        result.setPageSize(size);
        System.out.println(result);
    }

    /**
     * 测试批量删除
     */
    @Test
    public void testBatchDelete() {
        StudentPO studentPO = new StudentPO();
        studentPO.setId("3");
        studentService.batchDeleteStudentPO(Lists.newArrayList(studentPO));
    }
}


