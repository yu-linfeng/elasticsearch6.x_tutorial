# Java客户端（下）

基于[Java客户端（上）](minimum_should_match)，本文不再赘述如何创建一个Spring Data ElasticSearch工程，也不再做过多文字叙述。更多的请一定配合源码使用，源码地址[https://github.com/yu-linfeng/elasticsearch6.x_tutorial/tree/master/code/spring-data-elasticsearch](https://github.com/yu-linfeng/elasticsearch6.x_tutorial/tree/master/code/spring-data-elasticsearch)，具体代码目录在```complex```包。

本章请一定结合代码重点关注如何如何通过Java API进行**父子文档的数据插入，以及查询。**

### 父子文档的数据插入

父子文档在ES中存储的格式实际上是以**键值对**方式存在，例如在定义映射Mapping时，我们将子文档定义为：

```json
{
    ......
    "level":{
        "type":"join",
        "relations":{
				    "superior":"staff",
            "staff":"junior"
        }
    }
    ......
}
```

在写入一条数据时：

```json
{
    ......
    "level":{
        "name":"staff",
        "parent":"1"
    }
    ......
}
```

对于于Java实体，我们可以把```level```字段设置为```Map<String, Object>```类型。关键注意的是，在使用Spring Data ElasticSearch时，我们不能直接调用```sava```或者```saveAll```方法。ES规定**父子文档必须属于同一分片**，也就是说在写入子文档时，需要定义```routing```参数。下面是代码节选：

```java
BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
bulkRequestBuilder.add(client.prepareIndex("company", "employee", employeePO.getId()).setRouting(routing).setSource(mapper.writeValueAsString(employeePO), XContentType.JSON)).execute().actionGet();
```

一定参考源码一起使用。

ES实在是一个非常强大的搜索引擎。能力有限，实在不能将所有的Java API一一举例讲解，如果你在编写代码时，遇到困难也请联系作者邮箱**hellobug at outlook.com**，或者通过公众号**coderbuff**，解答得了的一定解答，解答不了的一起解答。

