# Java客户端（上）

ES提供了多种方式使用Java客户端：

- TransportClient，通过Socket方式连接ES集群，传输会对Java进行序列化
- RestClient，通过HTTP方式请求ES集群

目前常用的是```TransportClient```方式连接ES服务。但ES官方表示，在未来```TransportClient```会被永久移除，只保留```RestClient```方式。

同样，Spring Boot官方也提供了操作ES的方式```Spring Data ElasticSearch```。本章节将首先介绍基于Spring Boot所构建的工程通过```Spring Data ElasticSearch```操作ES，再介绍同样是基于Spring Boot所构建的工程，但使用ES提供的```TransportClient```操作ES。

## Spring Data ElasticSearch











## TransportClient



