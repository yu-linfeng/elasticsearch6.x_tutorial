# 准备工作

## 环境准备

工欲善其事必先利其器，既然是ElasticSearch第一步必然是ES的安装。

ElasticSearch5.6下载地址（Linux、mac OS、Windows通用，下载zip包即可）：[https://www.elastic.co/cn/downloads/past-releases/elasticsearch-5-6-0](https://www.elastic.co/cn/downloads/past-releases/elasticsearch-5-6-0)。ES历史版本下载页面：[https://www.elastic.co/cn/downloads/past-releases\#elasticsearch](https://www.elastic.co/cn/downloads/past-releases#elasticsearch)。

在正式安装前，你需要确保你的系统已配置JDK8环境。

### Linux

ES需要使用普通用户安装、启动，如果你是root用户，需要先创建一个用户，暂且取名“elastic”。

### mac OS

mac OS的安装过程和Linux类似，

### Windows

本教程的系统环境为Linux或mac OS。

进入ES目录执行`./bin/elasticsearch`命令，通过浏览器访问出现以下响应：

`{    
    "name": "x4x7wWJ",    
    "cluster_name": "elasticsearch",    
    "cluster_uuid": "sJ6LTYJ1TDmtR1kzl0M2Ig",    
    "version": {    
        "number": "5.6.4",    
        "build_hash": "8bbedf5",    
        "build_date": "2017-10-31T18:55:38.105Z",    
        "build_snapshot": false,    
        "lucene_version": "6.6.1"    
    },    
    "tagline": "You Know, for Search"    
}`

运行正常。

ES已经安装完成，已经可以开始对它一顿操作。不过，在实际生产环境中，光能运行ES还不够，还需要监控它，知道它目前的运行状态等等，Kibana就是这样一个辅助工具。

## Kibana安装

Kibana5.6下载地址：[https://www.elastic.co/cn/downloads/past-releases/kibana-5-6-4](https://www.elastic.co/cn/downloads/past-releases/kibana-5-6-4)，注意Kibana版本最好和ES版本对应。Kibana历史版本下载页面：[https://www.elastic.co/cn/downloads/past-releases\#kibana](https://www.elastic.co/cn/downloads/past-releases#kibana)。

为了方便，也请将Kibana解压到`~/`目录，进入Kibana安装目录`config`，编辑`kibana.yaml`文件，修改下面几项：  
`server.port: 5601  
server.host: "localhost"    #localhost就只允许本机访问，修改为0.0.0.0则允许客户端访问  
elasticsearch.url: "http://localhost:9200"    #ES服务器地址`

进入Kibana目录执行`./bin/kibana`命令，通过浏览器访问出现以下页面：

//插入图片



