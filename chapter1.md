# 准备工作

> 工欲善其事必先利其器

## ElasticSearch安装

ElasticSearch6.3.2下载地址（Linux、mac OS、Windows通用，下载zip包即可）：[https://www.elastic.co/cn/downloads/past-releases/elasticsearch-6-3-2](https://www.elastic.co/cn/downloads/past-releases/elasticsearch-6-3-2)。ES历史版本下载页面：[https://www.elastic.co/cn/downloads/past-releases\#elasticsearch](https://www.elastic.co/cn/downloads/past-releases#elasticsearch)。

在正式安装前，你需要确保你的系统已配置JDK8环境。

### mac OS

在上述下载地址下载完elasticsearch-6.3.2.tar.gz后，首先在当前登录用户的```home```下创建一个```Settings```目录，通过```tar -zxvf elasticsearch-6.3.2.tar.gz```解压到当前目录。

进入```elasticsearch-6.3.2.tar.gz```目录，执行```./bin/elasticsearch```命令，等待一小段时间，通过浏览器访问`http://localhost:9200/?pretty`出现以下响应：

```json
{              
    "name": "x4x7wWJ",              
    "cluster_name": "elasticsearch",              
    "cluster_uuid": "sJ6LTYJ1TDmtR1kzl0M2Ig",              
    "version": {              
        "number": "6.3.2",              
        "build_hash": "8bbedf5",              
        "build_date": "2017-10-31T18:55:38.105Z",              
        "build_snapshot": false,              
        "lucene_version": "6.6.1"              
    },              
    "tagline": "You Know, for Search"              
}
```

### Linux

Linux的安装过程和Linux相同。

**ES需要使用普通用户安装、启动，如果你是root用户，需要先创建一个用户，用普通用户而不是root用户启动ES。**