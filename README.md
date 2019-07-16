# 写在前面的话

[查看在线教程](https://yulinfeng.gitbooks.io/elasticsearch/content/)

[下载完整教程](https://github.com/yu-linfeng/elasticsearch6.x_tutorial/blob/master/ElasticSearch6.x%E5%AE%9E%E6%88%98%E6%95%99%E7%A8%8B.pdf)

[GitHub仓库](https://github.com/yu-linfeng/elasticsearch6.x_tutorial)

这个教程虽然名字叫做《ElasticSearch6.x实战教程》但实际上离“实战”，离“教程”还相去甚远。原本是打算基于ElasticSearch5.x，但写到“父-子关系文档”一章时发现5.x与6.x在父子文档关系上完全不同，其根本原因在于ElasticSearch正在逐步放弃一个索引（Index）建立多个类型（Type）。从ElasticSearch6.x开始，官方只允许一个索引（Index）建立一个类型（Type）。在未来的版本中，类型（Type）这一概念将会被完全移除。在询问身边的朋友所在公司使用的ElasticSearch版本大多是基于ElasticSearch6.x后，决定将基于ElasticSearch6.x开始ElasticSearch之旅。

说回离实战和教程相去甚远，第一，这个“教程”并不完美，并没有将ElasticSearch的魅力充分展现，仅仅展露了其“冰山一角”。第二，这个“教程”也并不深入，既没有“由浅入深”的层层递进，也没有“从入门到精通”的强大魄力。第三，说到“实战”，也没有引人入胜的“BAT大厂ES面经”，最后的实战章节也只能管中窥豹。

说了我自己写的那么多不好，是不是就立马放弃这个“教程”了呢？我想，把这个“教程”当做是学习ElasticSearch的第一步是没有错的。尽管有那么多的“缺点”，但这个“教程”我尽可能把每个执行的HTTP请求参数完整的记录下来，尽可能的思考可能遇到的实际场景，例如在“复杂搜索”章节中的“搜索精度”问题，在实际生活当中我们在搜索一个关键字时，给出关键字往往并不那么准确，但我们却希望搜索结果能准确。我们搜索“新希望牛奶”或者“牛奶新希望”时，绝不希望出现“春夏上新短袖”这一结果。

如果看到这个所谓的“教程”，不妨读下去，也许它就是你的ES启蒙小册。



OKevin

2019年7月9日凌晨 于成都