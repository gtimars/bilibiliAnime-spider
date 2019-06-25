# 番剧爬虫
使用webmagic框架爬取了b站的番剧信息，包括索引页（番剧索引页、国产索引页）、列表页（连载、完结、国创）
# 概述
+ 使用webmagic作为爬虫框架，使用hbase存储数据，hbase可以具有多个版本的数据，很适合对历史数据进行分析，比如每日的播放量等，同时将需要索引的字段放入elasticserach中
+ 共爬取了3400条番剧信息，2.2W左右条番剧分集信息，5.5W不到的列表页数据
+ 可视化  
追番人数最多的番剧：  
![favorite](https://raw.githubusercontent.com/gtimars/bilibiliAnime-spider/master/img/favorite.png)    
播放量(大部分都是合集，毕竟集数多)和评分区间:  
![viewAndScore](https://raw.githubusercontent.com/gtimars/bilibiliAnime-spider/master/img/view%2Cscore.png)   
列表（可选择连载、完结、国创分类，也可以对列表页信息进行搜索）  
![list](https://raw.githubusercontent.com/gtimars/bilibiliAnime-spider/master/img/list.png)  
详细页（番剧中每个分集播放量等一些信息是从各个列表页爬取来的，可能有些番剧的分集信息不在列表中，比如某些番剧还是处于未开播的状态，还有是以合集的形式出现）    
![detail](https://raw.githubusercontent.com/gtimars/bilibiliAnime-spider/master/img/detail.png)  
