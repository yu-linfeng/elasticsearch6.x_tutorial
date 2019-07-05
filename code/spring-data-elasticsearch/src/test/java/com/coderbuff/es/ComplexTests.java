package com.coderbuff.es;

import com.coderbuff.es.complex.domain.EmployeePO;
import com.coderbuff.es.complex.service.EmployeeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
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

    /**
     * 测试创建Index，type和Mapping定义
     */
    @Test
    public void createIndex() {
        elasticsearchTemplate.createIndex(EmployeePO.class);
        elasticsearchTemplate.putMapping(EmployeePO.class);
    }

    @Test
    public void testBatchInsert() throws JsonProcessingException {
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

        employeeService.batchInsertEmployeeSon(employeePOList, "2");
    }

    /**
     * 不分页查询数组类型departments=["研发部","市场部"]
     * 期望结果：name=李四
     */
    public void testArrayMatchQuery() {
        SearchQuery searchQuery
    }
}
