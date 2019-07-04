# 复杂搜索

> 黑夜给了我黑色的眼睛，我却用它寻找光明。

经过了解简单的API和简单搜索，已经基本上能应付大部分的使用场景。可是非关系型数据库数据的文档数据往往又多又杂，各种各样冗余的字段，组成了一条"记录"。复杂的数据结构，带来的就是复杂的搜索。所以在进入本章节前，我们要构建一个尽可能"复杂"的数据结构。

下面分为两个场景，场景1偏向数据结构上的复杂并且介绍**聚合查询**、**指定字段返回**、**深分页**，场景2偏向搜索精度上的复杂。

## 场景1

存储一个公司的员工，员工信息包含姓名、工号、性别、出生年月日、岗位、上级、下级、所在部门、进入公司时间、修改时间、创建时间。其中员工工号作为主键ID全局唯一，员工只有一个直属上级，但有多个下级，可以通过父子文档实现。员工有可能属于多个部门（特别是领导可能兼任多个部门的负责人）。

### 数据结构

创建索引并定义映射结构：

```json
PUT http://localhost:9200/company
{
	"mappings":{
		"employee":{
			"properties":{
				"id":{
					"type":"keyword"
				},
				"name":{
					"type":"text",
					"analyzer":"ik_smart",
					"fields":{
						"keyword":{
							"type":"keyword",
							"ignore_above":256
						}
					}
				},
				"sex":{
					"type":"keyword"
				},
        "age":{
          "type":"integer"
				},
				"birthday":{
					"type":"date"
				},
				"position":{
					"type":"text",
					"analyzer":"ik_smart",
					"fields":{
						"keyword":{
							"type":"keyword",
							"ignore_above":256
						}
					}
				},
				"level":{
					"type":"join",
					"relations":{
						"superior":"staff",
            "staff":"junior"
					}
				},
				"departments":{
					"type":"text",
					"analyzer":"ik_smart",
					"fields":{
						"keyword":{
							"type":"keyword",
							"ignore_above":256
						}
					}
				},
				"join_time":{
					"type":"date"
				},
				"modified":{
					"type":"date"
				},
				"created":{
					"type":"date"
				}
			}
		}
	}
}
```

### 数据

接下来是构造数据，我们构造几条关键数据。

- 张三是公司的董事长，他是最大的领导，不属于任何部门。
- 李四的上级是张三，他的下级是王五、赵六、孙七、周八，他同时是市场部和研发部的负责人，也就是隶属于市场部和研发部。
- 王五、赵六的上级是张三，他没有下级，他隶属于市场部。
- 孙七、周八的上级是李四，他没有下级，他隶属于研发部。

更为全面直观的数据如下表所示：

| 姓名 | 工号 | 性别 | 年龄 | 出生年月日 | 岗位       | 上级 | 下级                   | 部门           | 进入公司时间 | 修改时间      | 创建时间      |
| ---- | ---- | ---- | ---- | ---------- | ---------- | ---- | ---------------------- | -------------- | ------------ | ------------- | ------------- |
| 张三 | 1    | 男   | 49   | 1970-01-01 | 董事长     | /    | 李四                   | /              | 1990-01-01   | 1562167817000 | 1562167817000 |
| 李四 | 2    | 男   | 39   | 1980-04-03 | 总经理     | 张三 | 王五、赵六、孙七、周八 | 市场部、研发部 | 2001-02-02   | 1562167817000 | 1562167817000 |
| 王五 | 3    | 女   | 27   | 1992-09-01 | 销售       | 李四 | /                      | 市场部         | 2010-07-01   | 1562167817000 | 1562167817000 |
| 赵六 | 4    | 男   | 29   | 1990-10-10 | 销售       | 李四 | /                      | 市场部         | 2010-08-08   | 1562167817000 | 1562167817000 |
| 孙七 | 5    | 男   | 26   | 1993-12-10 | 前端工程师 | 李四 | /                      | 研发部         | 2016-07-01   | 1562167817000 | 1562167817000 |
| 周八 | 6    | 男   | 25   | 1994-05-11 | Java工程师 | 李四 | /                      | 研发部         | 2018-03-10   | 1562167817000 | 1562167817000 |

插入6条数据：

```json
POST http://localhost:9200/company/employee/1?routing=1
{
	"id":"1",
	"name":"张三",
	"sex":"男",
  "age":49,
	"birthday":"1970-01-01",
	"position":"董事长",
	"level":{
    "name":"superior"
  },
	"join_time":"1990-01-01",
	"modified":"1562167817000",
	"created":"1562167817000"
}
```

```json
POST http://localhost:9200/company/employee/2?routing=1
{
	"id":"2",
	"name":"李四",
	"sex":"男",
  "age":39,
	"birthday":"1980-04-03",
	"position":"总经理",
	"level":{
    "name":"staff",
    "parent":"1"
  },
  "departments":["市场部","研发部"],
	"join_time":"2001-02-02",
	"modified":"1562167817000",
	"created":"1562167817000"
}
```

```json
POST http://localhost:9200/company/employee/3?routing=1
{
	"id":"3",
	"name":"王五",
	"sex":"女",
  "age":27,
	"birthday":"1992-09-01",
	"position":"销售",
	"level":{
    "name":"junior",
    "parent":"2"
  },
  "departments":["市场部"],
	"join_time":"2010-07-01",
	"modified":"1562167817000",
	"created":"1562167817000"
}
```

```json
POST http://localhost:9200/company/employee/4?routing=1
{
	"id":"4",
	"name":"赵六",
	"sex":"男",
  "age":29,
	"birthday":"1990-10-10",
	"position":"销售",
	"level":{
    "name":"junior",
    "parent":"2"
  },
  "departments":["市场部"],
	"join_time":"2010-08-08",
	"modified":"1562167817000",
	"created":"1562167817000"
}
```

```json
POST http://localhost:9200/company/employee/5?routing=1
{
	"id":"5",
	"name":"孙七",
	"sex":"男",
  "age":26,
	"birthday":"1993-12-10",
	"position":"前端工程师",
	"level":{
    "name":"junior",
    "parent":"2"
  },
  "departments":["研发部"],
	"join_time":"2016-07-01",
	"modified":"1562167817000",
	"created":"1562167817000"
}
```

```json
POST http://localhost:9200/company/employee/6?routing=1
{
	"id":"6",
	"name":"周八",
	"sex":"男",
  "age":28,
	"birthday":"1994-05-11",
	"position":"Java工程师",
	"level":{
    "name":"junior",
    "parent":"2"
  },
  "departments":["研发部"],
	"join_time":"2018-03-10",
	"modified":"1562167817000",
	"created":"1562167817000"
}
```

### 搜索

1. 查询研发部的员工

```json
GET http://localhost:9200/company/employee/_search
{
	"query":{
		"match":{
			"departments":"研发部"
		}
	}
}
```

2. 查询在研发部且在市场部的员工

```json
GET http://localhost:9200/company/employee/_search
{
    "query": {
        "bool":{
        	"must":[{
        		"match":{
        			"departments":"市场部"
        		}
        	},{
        		"match":{
        			"departments":"研发部"
        		}
        	}]
        }
    }
}
```

*被搜索的字段是一个数组类型，但对查询语句并没有特殊的要求。

3. 查询name="张三"的直接下属。

```json
GET http://localhost:9200/company/employee/_search
{
    "query": {
        "has_parent":{
        	"parent_type":"superior",
        	"query":{
        		"match":{
        			"name":"张三"
        		}
        	}
        }
    }
}
```

4. 查询name="李四"的直接下属。

```json
GET http://localhost:9200/company/employee/_search

{
    "query": {
        "has_parent":{
        	"parent_type":"staff",
        	"query":{
        		"match":{
        			"name":"李四"
        		}
        	}
        }
    }
}
```

5. 查询name="王五"的直接上级。

```json
GET http://localhost:9200/company/employee/_search
{
    "query": {
        "has_child":{
        	"type":"junior",
        	"query":{
        		"match":{
        			"name":"王五"
        		}
        	}
        }
    }
}
```

#### 聚合查询

ES中的聚合查询类似MySQL中的聚合函数(avg、max等)，例如计算员工的平均年龄。

```json
GET http://localhost:9200/company/employee/_search?pretty
{
    "size": 0,
    "aggs": {
        "avg_age": {
            "avg": {
                "field": "age"
            }
        }
    }
}
```

#### 指定字段查询

指定字段返回值在查询结果中指定需要返回的字段。例如只查询张三的生日。

```json
GET http://localhost:9200/company/employee/_search?pretty
{
    "_source":["name","birthday"],
    "query":{
    	"match":{
    		"name":"张三"
    	}
    }
}
```

### 深分页

ES的深分页是一个老生常谈的问题。用过ES的都知道，ES默认查询深度不能超过10000条，也就是page * pageSize < 10000。如果需要查询超过1万条的数据，要么通过设置最大深度，要么通过```scroll```滚动查询。如果调整配置，即使能查出来，性能也会很差。但通过```scroll```滚动查询的方式带来的问题就是只能进行"上一页"、"下一页"的操作，而不能进行页码跳转。

```scroll```原理简单来讲，就是一批一批的查，上一批的最后一个数据，作为下一批的第一个数据，直到查完所有的数据。

首先需要初始化查询

```json
GET http://localhost:9200/company/employee/_search?scroll=1m
{
	"query":{
		"match_all":{}
	},
	"size":1,
	"_source": ["id"]
}
```

像普通查询结果一样进行查询，url中的scroll=1m指的是游标查询的过期时间为1分钟，每次查询就会更新，设置过长占会用过多的时间。

 接下来就可以通过上述API返回的```_scroll_id```进行滚动查询，假设上面的结果返回```"_scroll_id": "DnF1ZXJ5VGhlbkZldGNoBQAAAAAAAAFBFk1pNzdFUVhDU3hxX3VtSVFUdDJBWlEAAAAAAAABQhZNaTc3RVFYQ1N4cV91bUlRVHQyQVpRAAAAAAAAAUMWTWk3N0VRWENTeHFfdW1JUVR0MkFaUQAAAAAAAAFEFk1pNzdFUVhDU3hxX3VtSVFUdDJBWlEAAAAAAAABRRZNaTc3RVFYQ1N4cV91bUlRVHQyQVpR"```。

```json
GET http://localhost:9200/_search/scroll
{
    "scroll":"1m",
    "scroll_id": "DnF1ZXJ5VGhlbkZldGNoBQAAAAAAAAFBFk1pNzdFUVhDU3hxX3VtSVFUdDJBWlEAAAAAAAABQhZNaTc3RVFYQ1N4cV91bUlRVHQyQVpRAAAAAAAAAUMWTWk3N0VRWENTeHFfdW1JUVR0MkFaUQAAAAAAAAFEFk1pNzdFUVhDU3hxX3VtSVFUdDJBWlEAAAAAAAABRRZNaTc3RVFYQ1N4cV91bUlRVHQyQVpR"
}
```

这种方式有一个小小的弊端，如果超过过期时间就不能继续往下查询，这种查询适合一次全量查询所有数据。但现实情况有可能是用户在一个页面停留很长时间，再点击上一页或者下一页，此时超过过期时间页面不能再进行查询。所以还有另外一种方式，范围查询。

#### 另一种深分页

假设员工数据中的工号ID是按递增且唯一的顺序，那么我们可以通过范围查询进行分页。

例如，按ID递增排序，第一查询ID>0的数据，数据量为1。

```json
GET http://localhost:9200/company/employee/_search
{
	"query":{
		"range":{
			"id":{
				"gt":0
			}
		}
	},
	"size":1,
	"sort":{
		"id":{
			"order":"asc"
		}
	}
}
```

此时返回ID=1的1条数据，我们再继续查询ID>1的数据，数据量仍然是1。

```json
GET http://localhost:9200/company/employee/_search
{
	"query":{
		"range":{
			"id":{
				"gt":1
			}
		}
	},
	"size":1,
	"sort":{
		"id":{
			"order":"asc"
		}
	}
}
```

这样我们同样做到了深分页的查询，并且没有过期时间的限制。

## 场景2

存储商品数据，根据商品名称搜索商品，要求准确度高，不能搜索洗面奶结果出现面粉。

由于这个场景主要涉及的是搜索的精度问题，所以并不会有复杂的数据结构，只有一个title字段。

定义一个只包含title字段且分词器默认为```standard```的索引：

```json
PUT http://localhost:9200/ware_index
{
    "mappings": {
        "ware": {
            "properties": {
            	"title":{
            		"type":"text"
            	}
            }
        }
    }
}
```

插入两条数据：

```json
POST http://localhost:9200/ware_index/ware
{
	"title":"洗面奶"
}
```

```json
POST http://localhost:9200/ware_index/ware
{
	"title":"面粉"
}
```

搜索关键字"洗面奶"：

```json
POST http://localhost:9200/ware_index/ware/_search
{
	"query":{
		"match":{
			"title":"洗面奶"
		}
	}
}
```

搜索结果出现了"洗面奶"和"面粉"两个风马牛不相及的结果，这显然不符合我们的预期。

原因在**分词**一章中已经说明，```text```类型默认分词器为```standard```，它会将中文字符串一个字一个字拆分，也就是将"洗面奶"拆分成了"洗"、"面"、"奶"，将"面粉"拆分成了"面"、"粉"。而```match```会将搜索的关键词拆分，也就拆分成了"洗"、"面"、"奶"，最后两个"面"都能匹配上，也就出现了上述结果。所以对于中文的字符串搜索我们需要指定分词器，而常用的分词器是```ik_smart```，它会按照最大粒度拆分，如果采用```ik_max_word```它会将词按照最小粒度拆分，也有可能造成上述结果。

```DELETE http://localhost:9200/ware_index```删除索引，重新创建并指定title字段的分词器为```ik_smart```。``

```json
PUT http://localhost:9200/ware_index

```















