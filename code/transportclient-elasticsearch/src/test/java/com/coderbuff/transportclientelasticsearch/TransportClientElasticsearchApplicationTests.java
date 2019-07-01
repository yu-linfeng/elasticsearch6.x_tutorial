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
            Assert.assertEquals(studentPOList.get(i).getId(), String.valueOf(i+1));
        }
    }

}
