package com.coderbuff.es;

import com.coderbuff.es.complex.domain.EmployeePO;
import com.coderbuff.es.complex.domain.WarePO;
import com.coderbuff.es.complex.service.EmployeeService;
import com.coderbuff.es.complex.service.WareService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.join.query.JoinQueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.sort.SortBuilders;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.util.List;
import java.util.Map;

/**
 * 复杂搜索测试类
 * Created by OKevin on 2019-07-05
 */
public class ComplexTests extends SpringDataElasticsearchApplicationTests{

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private WareService wareService;

    /**
     * 测试创建Index，type和Mapping定义
     */
    @Test
    public void createIndex() {
        elasticsearchTemplate.createIndex(EmployeePO.class);
        elasticsearchTemplate.putMapping(EmployeePO.class);
    }

    @Test
    public void testBatchInsert() {
        List<EmployeePO> employeePOList = Lists.newArrayList();
        //父文档
        EmployeePO zhangsan = new EmployeePO();
        zhangsan.setId("1");
        zhangsan.setName("张三");
        zhangsan.setSex("男");
        zhangsan.setAge(49);
        zhangsan.setBirthday("1970-01-01");
        zhangsan.setPosition("董事长");
        Map<String, Object> zhangsanLevel = Maps.newHashMap();
        zhangsanLevel.put("name", "superior");
        zhangsan.setLevel(zhangsanLevel);
        zhangsan.setJoinTime("1990-01-01");
        zhangsan.setModified(System.currentTimeMillis());
        zhangsan.setCreated(System.currentTimeMillis());
        employeeService.batchInsertEmployee(Lists.newArrayList(zhangsan));

        EmployeePO lisi = new EmployeePO();
        lisi.setId("2");
        lisi.setName("李四");
        lisi.setSex("男");
        lisi.setAge(39);
        lisi.setBirthday("1980-04-03");
        lisi.setPosition("总经理");
        Map<String, Object> lisiLevel = Maps.newHashMap();
        lisiLevel.put("name", "staff");
        lisiLevel.put("parent", "1");
        lisi.setLevel(lisiLevel);
        List<String> lisiDepartments = Lists.newArrayList("市场部", "研发部");
        lisi.setDepartments(lisiDepartments);
        lisi.setJoinTime("2001-02-02");
        lisi.setModified(System.currentTimeMillis());
        lisi.setCreated(System.currentTimeMillis());
        employeePOList.add(lisi);

        //子文档
        EmployeePO wangwu = new EmployeePO();
        wangwu.setId("3");
        wangwu.setName("王五");
        wangwu.setSex("女");
        wangwu.setAge(27);
        wangwu.setBirthday("1992-09-01");
        wangwu.setPosition("销售");
        Map<String, Object> wangwuLevel = Maps.newHashMap();
        wangwuLevel.put("name", "junior");
        wangwuLevel.put("parent", "2");
        wangwu.setLevel(wangwuLevel);
        List<String> wangwuDepartments = Lists.newArrayList("市场部");
        wangwu.setDepartments(wangwuDepartments);
        wangwu.setJoinTime("2010-07-01");
        wangwu.setModified(System.currentTimeMillis());
        wangwu.setCreated(System.currentTimeMillis());
        employeePOList.add(wangwu);

        EmployeePO zhaoliu = new EmployeePO();
        zhaoliu.setId("4");
        zhaoliu.setName("赵六");
        zhaoliu.setSex("男");
        zhaoliu.setAge(29);
        zhaoliu.setBirthday("1990-10-10");
        zhaoliu.setPosition("销售");
        Map<String, Object> zhaoliuLevel = Maps.newHashMap();
        zhaoliuLevel.put("name", "junior");
        zhaoliuLevel.put("parent", "2");
        zhaoliu.setLevel(zhaoliuLevel);
        List<String> zhaoliuDepartments = Lists.newArrayList("市场部");
        zhaoliu.setDepartments(zhaoliuDepartments);
        zhaoliu.setJoinTime("2010-08-08");
        zhaoliu.setModified(System.currentTimeMillis());
        zhaoliu.setCreated(System.currentTimeMillis());
        employeePOList.add(zhaoliu);

        EmployeePO sunqi = new EmployeePO();
        sunqi.setId("5");
        sunqi.setName("孙七");
        sunqi.setSex("男");
        sunqi.setAge(26);
        sunqi.setBirthday("1993-12-10");
        sunqi.setPosition("前端工程师");
        Map<String, Object> sunqiLevel = Maps.newHashMap();
        sunqiLevel.put("name", "junior");
        sunqiLevel.put("parent", "2");
        sunqi.setLevel(sunqiLevel);
        List<String> sunqiDepartments = Lists.newArrayList("研发部");
        sunqi.setDepartments(sunqiDepartments);
        sunqi.setJoinTime("2016-07-01");
        sunqi.setModified(System.currentTimeMillis());
        sunqi.setCreated(System.currentTimeMillis());
        employeePOList.add(sunqi);

        EmployeePO zhouba = new EmployeePO();
        zhouba.setId("6");
        zhouba.setName("周八");
        zhouba.setSex("男");
        zhouba.setAge(28);
        zhouba.setBirthday("1994-05-11");
        zhouba.setPosition("Java工程师");
        Map<String, Object> zhoubaLevel = Maps.newHashMap();
        zhoubaLevel.put("name", "junior");
        zhoubaLevel.put("parent", "2");
        zhouba.setLevel(zhoubaLevel);
        List<String> zhoubaDepartments = Lists.newArrayList("研发部");
        zhouba.setDepartments(zhoubaDepartments);
        zhouba.setJoinTime("2018-03-10");
        zhouba.setModified(System.currentTimeMillis());
        zhouba.setCreated(System.currentTimeMillis());
        employeePOList.add(zhouba);

        employeeService.batchInsertEmployeeSon(employeePOList, "1");
    }

    /**
     * 不分页查询数组类型departments=["研发部","市场部"]
     * 期望结果：name=李四
     */
    @Test
    public void testArrayMatchQuery() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("departments", "研发部"))
                        .must(QueryBuilders.matchQuery("departments", "市场部"))).build();
        System.out.println(employeeService.search(searchQuery));
    }

    /**
     * 父子关系文档，父查子，查询name="张三"的下级
     * 期望结果：name=李四
     */
    @Test
    public void testFartherMatchQuery() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(
                        JoinQueryBuilders.hasParentQuery("superior",
                                QueryBuilders.matchQuery("name", "张三"), true))
                .build();
        System.out.println(employeeService.search(searchQuery));
    }

    /**
     * 父子关系文档，子查父，查询name="王五"的上级
     * 期望结果：name=李四
     */
    @Test
    public void testSonMatchQuery() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(
                        JoinQueryBuilders.hasChildQuery("junior",
                                QueryBuilders.matchQuery("name", "王五"), ScoreMode.None))
                .build();
        System.out.println(employeeService.search(searchQuery));
    }

    /**
     * TODO 聚合函数
     */
    @Test
    public void testAvgAgeQuery() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .addAggregation(AggregationBuilders.avg("avg_age").field("age")).build();
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery,
                SearchResponse::getAggregations);
        aggregations.asMap().get("avg_age");
        System.out.println(employeeService.search(searchQuery));
    }

    /**
     * 指定字段返回，查询name="张三"，指定返回姓名和出生年月日
     */
    @Test
    public void testReturnFieldMatchQuery() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withSourceFilter(new FetchSourceFilter(new String[]{"name", "birthday"}, new String[]{""}))
                .withQuery(QueryBuilders.matchQuery("name", "张三")).build();
        System.out.println(employeeService.search(searchQuery));
    }

    /**
     * 通过范围进行深分页全量查询
     *
     */
    @Test
    public void testRangeDeepPageMatchQuery() {
        List<EmployeePO> employeePOList = Lists.newArrayList();
        do {
            String id = "";
            if (employeePOList.isEmpty()) {
                id = "0";
            } else {
                id = employeePOList.get(employeePOList.size() - 1).getId();
            }
            Pageable page = PageRequest.of(0, 1, new Sort(Sort.Direction.ASC, "id"));
            SearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.rangeQuery("id").gt(id))
                    .withPageable(page)
                    .withSort(SortBuilders.fieldSort("id")).build();
            employeePOList = employeeService.search(searchQuery);
            System.out.println(employeePOList);
        } while (!employeePOList.isEmpty());
    }


    /******************************
     * 精度查询                    *
     ******************************
     */
    @Test
    public void createWareIndex() {
        elasticsearchTemplate.createIndex(WarePO.class);
        elasticsearchTemplate.putMapping(WarePO.class);
    }

    @Test
    public void testBatchInsertWare() {
        List<WarePO> warePOList = Lists.newArrayList();

        WarePO ware1 = new WarePO();
        ware1.setId("1");
        ware1.setTitle("新希望牛奶");
        warePOList.add(ware1);

        WarePO ware2 = new WarePO();
        ware2.setId("2");
        ware2.setTitle("春秋上新短袖");
        warePOList.add(ware2);

        wareService.batchInsertWare(warePOList);
    }

    /**
     * matchQuery，查询title="新希望牛奶"
     * 需求希望搜索结果只包含"新希望牛奶"，
     * 但实际的搜索结果包含"新希望牛奶"、"春秋上新短袖"
     */
    @Test
    public void testMatchQuery() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("title", "新希望牛奶")).build();
        System.out.println(wareService.search(searchQuery));
    }

    /**
     * matchPhrase短语查询，搜索关键字"新希望牛奶"
     * 需求希望搜索结果只包含"新希望牛奶"，
     * 搜索结果确实只包含新希望牛奶。
     */
    @Test
    public void testMatchPhraseQuery() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchPhraseQuery("title", "新希望牛奶")).build();
        System.out.println(wareService.search(searchQuery));
    }

    /**
     * matchPhrase短语查询，搜索关键字"牛奶 新希望"
     * 需求希望搜索结果只包含"新希望牛奶"，
     * 搜索不包含任何结果。
     */
    @Test
    public void testMatchPhraseQuery2() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchPhraseQuery("title", "牛奶 新希望")).build();
        System.out.println(wareService.search(searchQuery));
    }

    /**
     * matchPhrasePrefix短语前缀匹配查询，搜索关键字"牛奶 新希望"
     * 需求希望搜索结果只包含"新希望牛奶"，
     * 搜索不包含任何结果。
     */
    @Test
    public void testMatchPhrasePrefixQuery() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchPhrasePrefixQuery("title", "牛奶 新希望")).build();
        System.out.println(wareService.search(searchQuery));
    }

    /**
     * minimum_should_match最低匹配度查询，搜索关键字"牛奶 新希望"
     * 需求希望搜索结果只包含"新希望牛奶"，
     * 搜索结果只有title="新希望牛奶"。
     */
    @Test
    public void testMinimumShouldMatchQuery() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("title", "新希望牛奶").minimumShouldMatch("80%")).build();
        System.out.println(wareService.search(searchQuery));
    }

    /**
     * minimum_should_match最低匹配度查询，搜索关键字"牛奶 新希望"
     * 需求希望搜索结果只包含"牛奶 新希望"，
     * 搜索结果只有title="新希望牛奶"。
     */
    @Test
    public void testMinimumShouldMatchQuery2() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("title", "牛奶 新希望").minimumShouldMatch("80%")).build();
        System.out.println(wareService.search(searchQuery));
    }
}
