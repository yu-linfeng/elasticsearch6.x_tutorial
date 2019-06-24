# 简单搜索

> 众里寻他千百度

搜索是ES的核心，本节讲解一些基本的简单的搜索。

掌握ES搜索查询的RESTful的API犹如掌握关系型数据库的SQL语句，尽管Java客户端API为我们不需要我们去实际编写RESTful的API，但在生产环境中，免不了在线上执行查询语句做数据统计供产品经理等使用。

## 数据准备

首先创建一个名为user的Index，并创建一个student的Type，Mapping映射一共有如下几个字段：

1. 创建名为user的Index    ```PUT http://localhost:9200/user```

2. 创建名为student的Type，且指定字段name和address的分词器为```ik_smart```。

   ```json
   POST http://localhost:9200/user/student/_mapping
   {
   	"properties":{
   		"name":{
   			"type":"text",
   			"analyzer":"ik_smart"
   		},
   		"age":{
   			"type":"short"
   		},
   		"sex":{
   			"type":"text"
   		},
   		"address":{
   			"type":"text",
   			"analyzer":"ik_smart"
   		},
   		"created":{
   			"type":"date"
   		},
   		"modified":{
   			"type":"date"
   		},
   		"operator":{
   			"type":"text",
   			"analyzer":"ik_smart"
   		}
   	}
   }
   ```

经过上一章**分词**的学习我们把```text```类型都指定为```ik_smart```分词器。

插入以下数据。

```json
POST localhost:9200/user/student
{
    "name":"kevin",
    "age":25,
    "sex":"男",
    "address":"成都",
    "created":1561272578000,
    "modified":1561272578000,
    "operator":"ylf"
}
```

```json
POST localhost:9200/user/student
{
    "name":"kangkang",
    "age":26,
    "sex":"男",
    "address":"重庆",
    "created":1561186351000,
    "modified":1561186351000,
    "operator":"ylf"
}
```

```json
POST localhost:9200/user/student
{
    "name":"mike",
    "age":22,
    "sex":"男",
    "address":"北京",
    "created":1561272751000,
    "modified":1561272751000,
    "operator":"ylf"
}
```

```json
POST localhost:9200/user/student
{
    "name":"kevin2",
    "age":26,
    "sex":"男",
    "address":"成都",
    "created":1561272578000,
    "modified":1561272578000,
    "operator":"ylf"
}
```

```jso
POST localhost:9200/user/student
{
    "name":"kevin yu",
    "age":21,
    "sex":"男",
    "address":"成都",
    "created":1561272578000,
    "modified":1561272578000,
    "operator":"ylf"
}
```

## 简单搜索

### 无条件搜索

```GET http://localhost:9200/user/student/_search?pretty```

查看索引user的student类型数据，得到刚刚插入的数据返回:

### 单条件搜索

ES查询主要分为```term```精确搜索、```match```模糊搜索。

#### term精确搜索

我们用```term```搜索name为“kevin”的数据。

```json
POST http://localhost:9200/user/student/_search?pretty
{
	"query":{
		"term":{
			"name":"kevin"
		}
	}
}
```

既然```term```是精确搜索，按照非关系型数据库的理解来讲就等同于```=```，那么搜索结果也应该只包含1条数据。然而出乎意料的是，搜索结果出现了两条数据：name="kevin"和name="keivin yu"，这看起来似乎是进行的模糊搜索，但又没有搜索出name="kevin2"的数据。我们先继续观察```match```的搜索结果。

#### match模糊搜索

同样，搜索name为“kevin”的数据。

```json
POST http://localhost:9200/user/student/_search?pretty
{
    "query":{
        "match":{
            "name":"kevin"
        }
    }
}
```

```match```的搜索结果竟然仍然是两条数据：name="kevin"和name="keivin yu"。同样，name="kevin2"也没有出现在搜索结果中。



从结果可以看到，```term```查询就是SQL语句中的```=```。

我们再用```term```搜索address为“成都”的数据。

```json
POST http://localhost:9200/user/student/_search?pretty
{
	"query":{
		"term":{
			"address":"成都"
		}
	}
}
```

ES响应：

```json
{
    "took": 1,
    "timed_out": false,
    "_shards": {
        "total": 5,
        "successful": 5,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": 0,
        "max_score": null,
        "hits": []
    }
}
```

竟然没有一个搜索结果。这是为什么呢？记住，ES的搜索不能按照传统的非关系型数据库去思考。

原因在于，首先，address字段在映射Mapping中的是```text```类型（在开头创建user索引时我们并没有指定它的类型），这种类型在搜索时会被解析（analyzed），也就是**分词**。

```json
GET http://localhost:9200/user
```

ES响应（截取address字段部分）：

```json
"address": {
  "type": "text",
  "fields": {
    "keyword": {
      "type": "keyword",
      "ignore_above": 256
    }
  }
}
```

可以看到在address字段类型下有一个```fields```字段，其中定义了```keyword```，这个定义实则表示```不分词```（也可以在定义Mapping字段时，直接将字段定义为```keyword```）。ES默认将会把“成都”分词为：“成”和“都”，如果才有上述term搜索“成都”，ES认为并不存在address包含“成都”的文档数据（默认搜索的是分词后的数据）。如果想让ES不分词，直接搜索，则采用以下搜索条件：

```json
POST http://localhost:9200/user/student/_search?pretty
{
	"query":{
		"term":{
			"address.keyword":{
				"value":"成都"
			}
		}
	}
}
```

此时搜索结果符合预期。

显然，就算分词，实际上将“成都”分为“成”和“都”并不符合我们的预期，这涉及中文分词，在下章中将会详解介绍中文分词插件——ik。

#### match模糊搜索

我们用```match```模糊查询name为“kevin”的数据。

```json
POST http://localhost:9200/user/student/_search?pretty
{
	"query":{
		"match":{
			"name":"kevin"
		}
	}
}
```

ES响应：

```json
{
    "took": 3,
    "timed_out": false,
    "_shards": {
        "total": 5,
        "successful": 5,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": 1,
        "max_score": 0.6931472,
        "hits": [
            {
                "_index": "user",
                "_type": "student",
                "_id": "AWuDGVMYrDrjUjCfDce3",
                "_score": 0.6931472,
                "_source": {
                    "name": "kevin",
                    "age": 25,
                    "sex": "男",
                    "address": "成都",
                    "created": 1561272578000,
                    "modified": 1561272578000,
                    "operator": "ylf"
                }
            }
        ]
    }
}
```

结果竟然不包含name=kevin2的数据，```match```难道不是应该像是SQL中的```like```一样的模糊搜索吗？

我们此时再插入一条name为”kevin yu“的数据，插入完成后，再执行上述```match```语句。

ES响应：

```json
{
    "took": 4,
    "timed_out": false,
    "_shards": {
        "total": 5,
        "successful": 5,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": 2,
        "max_score": 0.6931472,
        "hits": [
            {
                "_index": "user",
                "_type": "student",
                "_id": "AWuDGVMYrDrjUjCfDce3",
                "_score": 0.6931472,
                "_source": {
                    "name": "kevin",
                    "age": 25,
                    "sex": "男",
                    "address": "成都",
                    "created": 1561272578000,
                    "modified": 1561272578000,
                    "operator": "ylf"
                }
            },
            {
                "_index": "user",
                "_type": "student",
                "_id": "AWuDRO2VrDrjUjCfDcfA",
                "_score": 0.25811607,
                "_source": {
                    "name": "kevin yu",
                    "age": 23,
                    "sex": "男",
                    "address": "成都",
                    "created": 1561275440000,
                    "modified": 1561275440000,
                    "operator": "ylf"
                }
            }
        ]
    }
}
```

神奇的是，刚刚插入的”kevin yu“此时通过```match```查询出现在了查询结果中，而“kevin2”仍然没有出现在搜索结果中。

原因就在于ES中的```match```查询并不是我们所认为SQL中的```like```查询，这仍然涉及到ES对搜索词字段的**分词**结果，上述查询name字段对于kevin2数据，ES认为它是一个词，kevin yuES认为是“kevin”和“yu”两个词。此时对kevin进行match查询时，它认为kevin2和kevin并不是一个词，所以不会出现在搜索结果中。而对于“kevin yu”被拆分成了“kevin”和“yu”，match本身就是模糊搜索，所以出现在了搜索结果中。

关于分词，下个章节会更为详细的讲解，因为分词的问题，搜索结果会出现牛头不对马嘴的结果（搜索面粉出现洗面奶）。

但是我们还是先一睹为快，确认下“kevin2”和“kevin yu”，ES对它是如何分词的：

```json
POST http://localhost:9200/user/_analyze
{
	"field":"name",
	"text":"kevin2"
}
```

ES结果：

```json
{
    "tokens": [
        {
            "token": "kevin2",
            "start_offset": 0,
            "end_offset": 6,
            "type": "<ALPHANUM>",
            "position": 0
        }
    ]
}
```

可以看到“kevin2“确实被ES认为是一个单词，也就是它和”kevin“沾不上任何关系，没有出现在搜索结果中。

```json
POST http://localhost:9200/user/_analyze
{
	"field":"name",
	"text":"kevin yu"
}
```

ES结果：

```json
{
    "tokens": [
        {
            "token": "kevin",
            "start_offset": 0,
            "end_offset": 5,
            "type": "<ALPHANUM>",
            "position": 0
        },
        {
            "token": "yu",
            "start_offset": 6,
            "end_offset": 8,
            "type": "<ALPHANUM>",
            "position": 1
        }
    ]
}
```

可以看到”kevin yu“被分词为了”kevin“和”yu“，被认为是和”kevin“有关系的，所以出现在了搜索结果中。

```match```查询还有很多更为高级的查询方式：```match_phrase```短语查询，```match_phrase_prefix```短语匹配查询，```multi_match```多字段查询等。将在复杂搜索一章中详细介绍。

#### 类似like的模糊搜索

```wildcard```通配符查询。

```json
POST http://localhost:9200/user/student/_search?pretty
{
  "query": {
    "wildcard": {
      "name": "*kevin*"
    }
  }
}
```

ES响应：

```json
{
    "took": 104,
    "timed_out": false,
    "_shards": {
        "total": 5,
        "successful": 5,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": 3,
        "max_score": 1,
        "hits": [
            {
                "_index": "user",
                "_type": "student",
                "_id": "AWuDOpofrDrjUjCfDce-",
                "_score": 1,
                "_source": {
                    "name": "kevin2",
                    "age": 25,
                    "sex": "男",
                    "address": "成都",
                    "created": 1561274770000,
                    "modified": 1561274770000,
                    "operator": "ylf"
                }
            },
            {
                "_index": "user",
                "_type": "student",
                "_id": "AWuDGVMYrDrjUjCfDce3",
                "_score": 1,
                "_source": {
                    "name": "kevin",
                    "age": 25,
                    "sex": "男",
                    "address": "成都",
                    "created": 1561272578000,
                    "modified": 1561272578000,
                    "operator": "ylf"
                }
            },
            {
                "_index": "user",
                "_type": "student",
                "_id": "AWuDRO2VrDrjUjCfDcfA",
                "_score": 1,
                "_source": {
                    "name": "kevin yu",
                    "age": 23,
                    "sex": "男",
                    "address": "成都",
                    "created": 1561275440000,
                    "modified": 1561275440000,
                    "operator": "ylf"
                }
            }
        ]
    }
}
```

可以看到，ES搜索结果中出现了包含”kevin“的name文档。

#### fuzzy更智能的模糊搜索

fuzzy也是一个模糊查询，它看起来更加”智能“。它类似于搜狗输入法中允许语法错误，但仍能搜出你想要的结果。例如，我们查询name等于”kevin“的文档时，不小心输成了”kevon“，它仍然能查询出结构。

```json
POST http://localhost:9200/user/student/_search?pretty
{
  "query": {
    "fuzzy": {
      "name": "kevin"
    }
  }
}
```

ES响应：

```json
{
    "took": 8,
    "timed_out": false,
    "_shards": {
        "total": 5,
        "successful": 5,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": 2,
        "max_score": 0.55451775,
        "hits": [
            {
                "_index": "user",
                "_type": "student",
                "_id": "AWuDGVMYrDrjUjCfDce3",
                "_score": 0.55451775,
                "_source": {
                    "name": "kevin",
                    "age": 25,
                    "sex": "男",
                    "address": "成都",
                    "created": 1561272578000,
                    "modified": 1561272578000,
                    "operator": "ylf"
                }
            },
            {
                "_index": "user",
                "_type": "student",
                "_id": "AWuDRO2VrDrjUjCfDcfA",
                "_score": 0.20649284,
                "_source": {
                    "name": "kevin yu",
                    "age": 23,
                    "sex": "男",
                    "address": "成都",
                    "created": 1561275440000,
                    "modified": 1561275440000,
                    "operator": "ylf"
                }
            }
        ]
    }
}
```

### 多条件搜索

上文介绍了单个条件下的简单搜索，并且介绍了相关的精确和模糊搜索。这部分将介绍多个条件下的简单搜索。

当搜索需要多个条件时，条件与条件之间的关系有”与“，”或“，“非”，正如非关系型数据库中的”and“，”or“，“not”。

在ES中表示”与“关系的是关键字——```must```，表示”或“关系的是关键字——```should```，还有表示表示”非“的关键字——```must_not```。

```must```、```should```、```must_not```在ES中称为```bool```查询。当只有多个查询条件进行组合查询时，此时需要上述关键字配合上文提到的```term```，```match```等。

例1：精确查询name="kevin"且address="成都"的学生。



{
  "query": {
    "bool":{
    	"must":[{
    		"term":{
    			"name":"kevin"
    		}
    	},{
    		"term":{
    			"address.keyword":"成都"
    		}
    	}]
    }
  }
}













