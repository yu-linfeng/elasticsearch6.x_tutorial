package com.coderbuff.transportclientelasticsearch;

import com.coderbuff.transportclientelasticsearch.config.ElasticSearchClient;
import com.coderbuff.transportclientelasticsearch.easy.dao.StudentRepository;
import com.coderbuff.transportclientelasticsearch.easy.domain.StudentPO;
import com.coderbuff.transportclientelasticsearch.easy.service.StudentService;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
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
}


