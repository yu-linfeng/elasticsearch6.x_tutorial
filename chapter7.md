# 父-子关系文档

> 打虎亲兄弟，上阵父子兵。

本章作为**复杂搜索**的铺垫，介绍父子文档是为了更好的介绍复杂场景下的ES操作。

在非关系型数据库数据库中，我们常常会有表与表的关联查询。例如学生表和成绩表的关联查询就能查出学会的信息和成绩信息。在ES中，父子关系文档就类似于表的关联查询。

## 背景

ES5.x开始借助父子关系文档实现多表关联查询，核心是一个索引Index下可以创建多个类型Type。但ES6.x开始只允许一个索引Index下创建一个类型Type，甚至在未来的版本中将会移除创建类型Type。为了继续支持多表关联查询，ES6.x推出了```join```新类型来支持父子关系文档的创建。

## 问题

假设现在有这样的需求场景：一个博客有多篇文章，文章有标题、内容、作者、日期等信息，同时一篇文章中会有评论，评论有评论的内容、作者、日期等信息，通过ES来存储博客的文章及评论信息。

此时文章本身就是"父"，而评论就是"子"，这类问题也可以通过```nested```嵌套对象实现，大部分情况下```netsted```嵌套对象和```parent-child```父子对象能够互相替代，但他们仍然不同的优缺点。下面将介绍这两种数据结构。

### nested嵌套对象

一篇文章的数据结构如下图所示：

```json
{
    "title":"ElasticSearch6.x实战教程",
    "author":"OKevin",
    "content":"这是一篇水文",
    "created":1562141626000,
    "comments":[{
        "name":"张三",
        "content":"写的真菜",
        "created":1562141689000
    },{
        "name":"李四",
        "content":"辣鸡",
        "created":1562141745000
    }]
}
```

通过RESTful API创建索引及定义映射结构：

```json
PUT http://localhost:9200/blog
{
	"mappings":{
		"article":{
			"properties":{
				"title":{
					"type":"text",
					"analyzer":"ik_smart",
					"fields":{
						"keyword":{
							"type":"keyword",
							"ignore_above":256
						}
					}
				},
				"author":{
					"type":"text",
					"analyzer":"ik_smart",
					"fields":{
						"keyword":{
							"type":"keyword",
							"ignore_above":256
						}
					}
				},
				"content":{
					"type":"text",
					"analyzer":"ik_smart"
				},
				"created":{
					"type":"date"
				},
				"comments":{
					"type":"nested",
					"properties":{
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
						"content":{
							"type":"text",
							"analyzer":"ik_smart",
							"fields":{
								"keyword":{
									"type":"keyword",
									"ignore_above":256
								}
							}
						},
						"created":{
							"type":"date"
						}
					}
				}
			}
		}
	}
}
```

插入数据：

```json
POST http://localhost:9200/blog/article
{
    "title":"ElasticSearch6.x实战教程",
    "author":"OKevin",
    "content":"这是一篇水文",
    "created":1562141626000,
    "comments":[{
        "name":"张三",
        "content":"写的真菜",
        "created":1562141689000
    },{
        "name":"李四",
        "content":"辣鸡",
        "created":1562141745000
    }]
}
```

```json
POST http://localhost:9200/blog/article
{
    "title":"ElasticSearch6.x从入门到放弃",
    "author":"OKevin",
    "content":"这是一篇ES从入门到放弃文章",
    "created":1562144089000,
    "comments":[{
        "name":"张三",
        "content":"我已入门",
        "created":1562144089000
    },{
        "name":"李四",
        "content":"我已放弃",
        "created":1562144089000
    }]
}
```

```json
POST http://localhost:9200/blog/article
{
    "title":"ElasticSearch6.x原理解析",
    "author":"专家",
    "content":"这是一篇ES原理解析的文章",
    "created":1562144089000,
    "comments":[{
        "name":"张三",
        "content":"牛逼，专家就是不一样",
        "created":1562144089000
    },{
        "name":"李四",
        "content":"大牛",
        "created":1562144089000
    }]
}
```

1. 查询作者为“OKevin”文章的所有评论（父查子）

```json
GET http://localhost:9200/blog/article/_search
{
	"query":{
		"bool":{
			"must":[{
				"match":{
					"author.keyword":"OKevin"
				}
			}]
		}
	}
}
```

ES结果返回2条作者为"OKevin"的全部数据。

2. 查询评论中含有“辣鸡”的文章（子查父）

```json
GET http://localhost:9200/blog/article/_search
{
	"query":{
		"bool":{
			"must":[{
				"match":{
					"author.keyword":"OKevin"
				}
			},{
				"nested":{
					"path":"comments",
					"query":{
						"bool":{
							"must":[{
								"match":{
									"comments.content":"辣鸡"
								}
							}]
						}
					}
				}
			}]
		}
	}
}
```

ES确实只返回了包含"辣鸡"的数据。

两次查询都直接返回了整个文档数据。

### parent-child父子文档

既然父子文档能实现表的关联查询，那它的数据结构就应该是这样：

文章数据结构

```json
{
    "title":"ElasticSearch6.x实战教程",
    "author":"OKevin",
    "content":"这是一篇实战教程",
    "created":1562141626000,
    "comments":[]
}
```

评论数据结构

```json
{
    "name":"张三",
    "content":"写的真菜",
    "created":1562141689000
}
```

ES6.x以前是将这两个结构分别存储在两个类型Type中关联(这看起来更接近关系型数据库表与表的关联查询)，但在ES6.x开始一个索引Index只能创建一个类型Type，要再想实现表关联查询，就意味着需要把上述两张表揉在一起，ES6.x由此定义了一个新的数据类型——```join```。

通过RESTful API创建索引及定义映射结构：

```json
{
	"mappings":{
		"article":{
			"properties":{
				"title":{
					"type":"text",
					"analyzer":"ik_smart",
					"fields":{
						"keyword":{
							"type":"keyword",
							"ignore_above":256
						}
					}
				},
				"author":{
					"type":"text",
					"analyzer":"ik_smart",
					"fields":{
						"keyword":{
							"type":"keyword",
							"ignore_above":256
						}
					}
				},
				"content":{
					"type":"text",
					"analyzer":"ik_smart"
				},
				"created":{
					"type":"date"
				},
				"comments":{
					"type":"join",
					"relations":{
						"article":"comment"
					}
				}
			}
		}
	}
}
```

重点关注其中的"comments"字段，可以看到类型定义为```join```，relations定义了谁是父谁是子，"article":"comment"表示article是父comment是子。

父子文档的插入是父与子分别插入(因为可以理解为把多个表塞到了一张表里)。

插入父文档：

```json
POST http://localhost:9200/blog/article/1
{
    "title":"ElasticSearch6.x实战教程",
    "author":"OKevin",
    "content":"这是一篇水文",
    "created":1562141626000,
    "comments":"article"
}
```

```json
POST http://localhost:9200/blog/article/2
{
    "title":"ElasticSearch6.x从入门到放弃",
    "author":"OKevin",
    "content":"这是一篇ES从入门到放弃文章",
    "created":1562144089000,
    "comments":"article"
}
```

```json
POST http://localhost:9200/blog/article/3
{
    "title":"ElasticSearch6.x原理解析",
    "author":"专家",
    "content":"这是一篇ES原理解析的文章",
    "created":1562144089000,
    "comments":"article"
}
```

插入子文档：

```json
POST http://localhost:9200/blog/article/4?routing=1
{
    "name":"张三",
    "content":"写的真菜",
    "created":1562141689000,
    "comments":{
    	"name":"comment",
    	"parent":1
    }
}
```

```json
POST http://localhost:9200/blog/article/5?routing=1
{
    "name":"李四",
    "content":"辣鸡",
    "created":1562141745000,
    "comments":{
    	"name":"comment",
    	"parent":1
    }
}
```

```json
POST http://localhost:9200/blog/article/6?routing=2
{
    "name":"张三",
    "content":"我已入门",
    "created":1562144089000,
    "comments":{
    	"name":"comment",
    	"parent":2
    }
}
```

```json
POST http://localhost:9200/blog/article/7?routing=2
{
    "name":"李四",
    "content":"我已放弃",
    "created":1562144089000,
    "comments":{
    	"name":"comment",
    	"parent":2
    }
}
```

```json
POST http://localhost:9200/blog/article/8?routing=3
{
    "name":"张三",
    "content":"牛逼，专家就是不一样",
    "created":1562144089000,
    "comments":{
    	"name":"comment",
    	"parent":3
    }
}
```

```json
POST http://localhost:9200/blog/article/9?routing=3
{
    "name":"李四",
    "content":"大牛",
    "created":1562144089000,
    "comments":{
    	"name":"comment",
    	"parent":3
    }
}
```

如果查询索引数据会发现一共有9条数据，并不是```nested```那样将"评论"嵌套"文章"中的。

1. 查询作者为“OKevin”文章的所有评论（父查子）

```json
GET http://localhost:9200/blog/article/_search
{
	"query":{
		"has_parent":{
			"parent_type":"article",
			"query":{
				"match":{
					"author.keyword":"OKevin"
				}
			}
		}
	}
}
```

ES只返回了comment评论结构中的数据，而不是全部包括文章数据也返回。这是嵌套对象查询与父子文档查询的区别之一——**子文档可以单独返回**。

2. 查询评论中含有“辣鸡”的文章（子查父）

```json
GET http://localhost:9200/blog/artice/_search
{
	"query":{
		"has_child":{
			"type":"comment",
			"query":{
				"match":{
					"content":"辣鸡"
				}
			}
		}
	}
}
```

ES同样也只返回了父文档的数据，而没有子文档(评论)的数据。

```nested```嵌套对象和```parent-child```父子文档之间最大的区别，嵌套对象中的"父子"是一个文档数据，而父子文档的中的"父子"是两个文档数据。这意味着嵌套对象中如果涉及对嵌套文档的操作会对整个文档造成影响（重新索引，但查询快），包括修改、删除、查询。而父子文档子文档或者父文档本身就是**独立**的文档，对子文档或者父文档的操作并不会相互影响（不会重新索引，查询相对慢）。