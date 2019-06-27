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
    "age":25
}
```

```json
POST localhost:9200/user/student
{
    "name":"kangkang",
    "age":26
}
```

```json
POST localhost:9200/user/student
{
    "name":"mike",
    "age":22
}
```

```json
POST localhost:9200/user/student
{
    "name":"kevin2",
    "age":25
}
```

```jso
POST localhost:9200/user/student
{
    "name":"kevin yu",
    "age":21
}
```

## 按查询条件数量维度

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

原因在于```term```和```match```的精确和模糊针对的是**搜索词**而言，**```term```搜索不会将搜索词进行分词后再搜索，而```match```则会将搜索词进行分词后再搜索**。例如，我们对name="kevin yu"进行搜索，由于```term```搜索不会对搜索词进行搜索，所以它进行检索的是"kevin yu"这个整体，而```match```搜索则会对搜索词进行分词搜索，所以它进行检索的是包含"kevin"和"yu"的数据。而name字段是```text```类型，且它是按照```ik_smart```进行分词，就算是"kevin yu"这条数据由于被分词后变成了"kevin"和"yu"，所以```term```搜索不到任何结果。

如果一定要用```term```搜索name="kevin yu"，结果出现"kevin yu"，办法就是在定义映射Mapping时就为该字段设置一个```keyword```类型。

为了下文的顺利进行，删除```DELETE http:localhost:9200/user/student```重新按照开头创建索引以及插入数据吧。唯一需要修改的是在定义映射Mapping时，name字段修改为如下所示：

```json
{
    "properties":{
		  "name":{
			  "type":"text",
			  "analyzer":"ik_smart",
			  "fields":{
				  "keyword":{
					  "type":"keyword",
            "ignore_abore":256
				  }
			  }
		  },
    "age":{
        "type":integer
    }
	}
}
```

待我们重新创建好索引并插入数据后，此时再按照```term```搜索name="kevin yu"。

```json
POST http://localhost:9200/user/student/_search
{
	"query":{
		"term":{
			"name.keyword":"kevin yu"
		}
	}
}
```

返回一条name="kevin yu"的数据。按照```match```搜索同样出现name="kevin yu"，因为name.keyword无论如何都不会再分词。

_在已经建立索引且定义好映射Mapping的情况下，如果直接修改name字段，此时能修改成功，但是却无法进行查询，这与ES底层实现有关，如果一定要修改要么是新增字段，要么是重建索引。_

所以，与其说```match```是模糊搜索，倒不如说它是**分词搜索**，因为它会将搜索关键字分词；与其将```term```称之为模糊搜索，倒不如称之为**不分词搜索**，因为它不会将搜索关键字分词。

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

ES返回结果包括name="kevin"，name="kevin2"，name="kevin yu"。

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

ES返回结果包括name="kevin"，name="kevin yu"。

### 多条件搜索

上文介绍了单个条件下的简单搜索，并且介绍了相关的精确和模糊搜索（分词与不分词）。这部分将介绍多个条件下的简单搜索。

当搜索需要多个条件时，条件与条件之间的关系有”与“，”或“，“非”，正如非关系型数据库中的”and“，”or“，“not”。

在ES中表示”与“关系的是关键字```must```，表示”或“关系的是关键字```should```，还有表示表示”非“的关键字```must_not```。

```must```、```should```、```must_not```在ES中称为```bool```查询。当有多个查询条件进行组合查询时，此时需要上述关键字配合上文提到的```term```，```match```等。

1. 精确查询（```term```，搜索关键字不分词）name="kevin"**且**age="25"的学生。

```json
POST http://localhost:9200/user/student/_search?pretty
{
	"query":{
		"bool":{
			"must":[{
				"term":{
					"name.keyword":"kevin"
				}
			},{
				"term":{
					"age":25
				}
			}]
		}
	}
}
```

返回name="kevin"且age="25"的数据。

2. 精确查询（```term```，搜索关键字不分词）name="kevin"**或**age="21"的学生。

```json
POST http://localhost:9200/user/student/_search?pretty
{
	"query":{
		"bool":{
			"should":[{
				"term":{
					"name.keyword":"kevin"
				}
			},{
				"term":{
					"age":21
				}
			}]
		}
	}
}
```

返回name="kevin"，age=25和name="kevin yu"，age=21的数据

3. 精确查询（```term```，搜索关键字不分词）name!="kevin"**且**age="25"的学生。

```json
POST http://localhost:9200/user/student/_search?pretty
{
	"query":{
		"bool":{
			"must":[{
				"term":{
					"age":25
				}
			}],
			"must_not":[{
				"term":{
					"name.keyword":"kevin"
				}
			}]
		}
	}
}
```

返回name="kevin2"的数据。

如果查询条件中同时包含```must```、```should```、```must_not```，那么它们三者是"且"的关系

多条件查询中查询逻辑(```must```、```should```、```must_not```)与查询精度(```term```、```match```)配合能组合成非常丰富的查询条件。

## 按等值、范围查询维度

上文中讲到了精确查询、模糊查询，已经"且"，"或"，"非"的查询。基本上都是在做**等值查询**，实际查询中还包括，范围（大于小于）查询（```range```）、存在查询（```exists```）、~~~不存在查询（```missing```）~~。

### 范围查询

范围查询关键字```range```，它包括大于```gt```、大于等于```gte```、小于```lt```、小于等于```lte```。

1. 查询age>25的学生。

```json
POST http://localhost:9200/user/student/_search?pretty
{
	"query":{
		"range":{
			"age":{
				"gt":25
			}
		}
	}
}
```

返回name="kangkang"的数据。

2. 查询age >= 21且age < 26的学生。

```json
POST http://localhost:9200/user/search/_search?pretty
{
	"query":{
		"range":{
			"age":{
				"gte":21,
				"lt":25
			}
		}
	}
}
```

_查询age >= 21 且 age < 26且name="kevin"的学生_

```json
POST http://localhost:9200/user/search/_search?pretty
{
	"query":{
		"bool":{
			"must":[{
				"term":{
					"name":"kevin"
				}
			},{
				"range":{
					"age":{
						"gte":21,
						"lt":25
					}
				}
			}]
		}
	}
}
```

### 存在查询

存在查询意为查询是否存在某个字段。

查询存在name字段的数据。

```json
POST http://localhost:9200/user/student/_search?pretty
{
	"query":{
		"exists":{
			"field":"name"
		}	
	}
}
```

### 不存在查询

不存在查询顾名思义查询不存在某个字段的数据。在以前ES有```missing```表示查询不存在的字段，后来的版本中由于```must not```和```exists```可以组合成```missing```，故去掉了```missing```。

查询不存在name字段的数据。

```json
POST http://localhost:9200/user/student/_search?pretty
{
	"query":{
		"bool":{
			"must_not":{
				"exists":{
					"field":"name"
				}
			}
		}	
	}
}
```

## 分页搜索

谈到ES的分页永远都绕不开**深分页**的问题。但在本章中暂时避开这个问题，只说明在ES中如何进行分页查询。

ES分页查询包含```from```和```size```关键字，```from```表示起始值，```size```表示一次查询的数量。

1. 查询数据的总数

```json
POST http://localhost:9200/user/student/_search?pretty
```

返回文档总数。

2. 分页（一页包含1条数据）模糊查询(```match```，搜索关键字不分词)name="kevin"

```json
POST http://localhost:9200/user/student/_search?pretty
{
	"query":{
		"match":{
			"name":"kevin"
		}
	},
	"from":0,
	"size":1
}
```

结合文档总数即可返回简单的分页查询。

分页查询中往往我们也需要对数据进行排序返回，MySQL中使用```order by```关键字，ES中使用```sort```关键字指定排序字段以及降序升序。

1. 分页（一页包含1条数据）查询age >= 21且age <=26的学生，按年龄降序排列。

```json
POST http://localhost:9200/user/student/_search?pretty
{
	"query":{
		"range":{
			"age":{
				"gte":21,
				"lte":26
			}
		}
	},
	"from":0,
	"size":1,
	"sort":{
		"age":{
			"order":"desc"
		}
	}
}
```

ES默认升序排列，如果不指定排序字段的排序），则```sort```字段可直接写为```"sort":"age"```。

