# 复杂搜索

> 黑夜给了我黑色的眼睛，我却用它寻找光明。

经过了解简单的API和简单搜索，已经基本上能应付大部分的使用场景。可是非关系型数据库数据的文档数据往往又多又杂，各种各样冗余的字段，组成了一条"记录"。复杂的数据结构，带来的就是复杂的搜索。所以在进入本章节前，我们要构建一个尽可能"复杂"的数据结构。

## 场景

存储一个公司的员工，员工信息包含姓名、工号、性别、出生年月日、岗位、上级、下级、所在部门、进入公司时间、修改时间、创建时间。其中员工工号作为主键ID全局唯一，员工只有一个直属上级，但有多个下级，可以通过父子文档实现。员工有可能属于多个部门（特别是领导可能兼任多个部门的负责人）。

## 数据结构

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
						"superior":"junior"
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

## 数据

接下来是构造数据，我们构造几条关键数据。

- 张三是公司的董事长，他是最大的领导，不属于任何部门。
- 李四的上级是张三，他的下级是王五、赵六、孙七、周八，他同时是市场部和研发部的负责人，也就是隶属于市场部和研发部。
- 王五、赵六的上级是张三，他没有下级，他隶属于市场部。
- 孙七、周八的上级是李四，他没有下级，他隶属于研发部。

更为全面直观的数据如下表所示：

| 姓名 | 工号 | 性别 | 出生年月日 | 岗位       | 上级 | 下级                   | 部门           | 进入公司时间 | 修改时间      | 创建时间      |
| ---- | ---- | ---- | ---------- | ---------- | ---- | ---------------------- | -------------- | ------------ | ------------- | ------------- |
| 张三 | 1    | 男   | 1970-01-01 | 董事长     | /    | 李四                   | /              | 1990-01-01   | 1562167817000 | 1562167817000 |
| 李四 | 2    | 男   | 1980-04-03 | 总经理     | 张三 | 王五、赵六、孙七、周八 | 市场部、研发部 | 2001-02-02   | 1562167817000 | 1562167817000 |
| 王五 | 3    | 女   | 1992-09-01 | 销售       | 李四 | /                      | 市场部         | 2010-07-01   | 1562167817000 | 1562167817000 |
| 赵六 | 4    | 男   | 1990-10-10 | 销售       | 李四 | /                      | 市场部         | 2010-08-08   | 1562167817000 | 1562167817000 |
| 孙七 | 5    | 男   | 1993-12-10 | 前端工程师 | 李四 | /                      | 研发部         | 2016-07-01   | 1562167817000 | 1562167817000 |
| 周八 | 6    | 男   | 1994-05-11 | Java工程师 | 李四 | /                      | 研发部         | 2018-03-10   | 1562167817000 | 1562167817000 |

插入6条数据：

```json
POST http://localhost:9200/company/employee/1
{
	"id":"1",
	"name":"张三",
	"sex":"男",
	"birthdaty":"1970-01-01",
	"position":"董事长",
	"level":"superior",
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
	"birthdaty":"1980-04-03",
	"position":"总经理",
	"level":{
    "name":"junior",
    "parent":1
  },
  "departments":["市场部","研发部"],
	"join_time":"2001-02-02",
	"modified":"1562167817000",
	"created":"1562167817000"
}
```

```json
POST http://localhost:9200/company/employee/3?routing=2
{
	"id":"3",
	"name":"王五",
	"sex":"女",
	"birthdaty":"1990-10-10",
	"position":"销售",
	"level":{
    "name":"junior",
    "parent":2
  },
  "departments":["市场部"],
	"join_time":"2010-07-01",
	"modified":"1562167817000",
	"created":"1562167817000"
}
```

```json
POST http://localhost:9200/company/employee/4?routing=2
{
	"id":"4",
	"name":"赵六",
	"sex":"男",
	"birthdaty":"1992-09-01",
	"position":"销售",
	"level":{
    "name":"junior",
    "parent":2
  },
  "departments":["市场部"],
	"join_time":"2010-08-08",
	"modified":"1562167817000",
	"created":"1562167817000"
}
```

```json
POST http://localhost:9200/company/employee/5?routing=2
{
	"id":"5",
	"name":"孙七",
	"sex":"男",
	"birthdaty":"1993-12-10",
	"position":"前端工程师",
	"level":{
    "name":"junior",
    "parent":2
  },
  "departments":["研发部"],
	"join_time":"2016-07-01",
	"modified":"1562167817000",
	"created":"1562167817000"
}
```

```json
POST http://localhost:9200/company/employee/6?routing=2
{
	"id":"6",
	"name":"周八",
	"sex":"男",
	"birthdaty":"1994-05-11",
	"position":"Java工程师",
	"level":{
    "name":"junior",
    "parent":2
  },
  "departments":["研发部"],
	"join_time":"2018-03-10",
	"modified":"1562167817000",
	"created":"1562167817000"
}
```

## 搜索

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

*被搜索的字段是一个数组类型，但对查询语句并没有特殊的要求，ES结果返回3条数据。

2. 查询在研发部且在市场部的员工

```json

{
	"query":{
		"match":{
			"departments":"研发部"
		}
	}
}
```

















