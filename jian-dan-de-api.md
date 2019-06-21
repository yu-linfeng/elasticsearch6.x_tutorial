# 简单的API

ES提供了多种操作数据的方式，其中较为常见的方式就是RESTful风格的API。

简单的体验

利用Postman发起HTTP请求（当然也可以在命令行中使用curl命令）。

## 索引Index

### 创建索引

创建一个名叫`demo`的索引：

`PUT http://localhost:9200/demo`

ES响应：

`{`

`"acknowledged": true,`

`"shards_acknowledged": true,`

`"index": "demo"`

`}`

在创建索引时，可指定主分片和分片副本的数量：

`PUT http://localhost:9200/demo`

`{`

`"settings":{`

`"number_of_shards":1,`

`"number_of_replicas":1`

`}`

`}`

ES响应：

`{`

`"acknowledged": true,`

`"shards_acknowledged": true,`

`"index": "demo"`

`}`

### 查看指定索引

`GET http://localhost:9200/demo`

ES响应：

`{`

`"demo": {`

`"aliases": {},`

`"mappings": {},`

`"settings": {`

`"index": {`

`"creation_date": "1561110747038",`

`"number_of_shards": "1",`

`"number_of_replicas": "1",`

`"uuid": "kjPqDUt6TMyywg1P7qgccw",`

`"version": {`

`"created": "5060499"`

`},`

`"provided_name": "demo"`

`}`

`}`

`}`

`}`

### 查询ES中的索引

查询ES中索引情况：

`GET http://localhost:9200/_cat/indices?v`

ES响应：

| health | status | index | uuid | pri | rep | docs.count | docs.deleted | store.size | pri.store.size |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| yellow | open | demo | wqkto5CCTpWNdP3HGpLfxA | 5 | 1 | 0 | 0 | 810b | 810b |
| yellow | open | .kibana | pwKW9hJyRkO7\_pE0MNE05g | 1 | 1 | 1 | 0 | 3.2kb | 3.2kb |

可以看到当前ES中一共有2个索引，一个是我们刚创建的`demo`，另一个是kibana创建的索引`.kibana`。表格中有一些信息代表了索引的一些状态。

health：健康状态，red表示不是所有的主分片都可用，即**部分主分片可用**。yellow表示主分片可用备分片不可用，常常是单机ES的健康状态，greens表示所有的主分片和备分片都可用。（官方对集群健康状态的说明，[https://www.elastic.co/guide/en/elasticsearch/guide/master/cluster-health.html](https://www.elastic.co/guide/en/elasticsearch/guide/master/cluster-health.html)）

status：索引状态，open表示打开可对索引中的文档数据进行读写，close表示关闭此时索引占用的内存会被释放，但是此时索引不可进行读写操作。

index：索引

uuid：索引标识

pri：索引的主分片数量

rep：索引的分片副本数量，1表示有一个分片副本（有多少主分片就有多少备分片，此处表示5个备分片）。

docs.count：文档数量

docs.deleted：被删除的文档数量

store.size：索引大小

pri.store.size：主分片占用的大小

### 删除索引

删除`demo`索引

`DELETE http://localhost:9200/demo`

ES响应：

`{`

`"acknowledged": true`

`}`

## 类型Type



