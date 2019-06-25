package com.gtj.spider.service;

/**
 * 爬虫实现接口
 * @author gtj
 */
public interface SpiderService {
    /**
     * 爬取番剧索引页和其分集信息
     */
    void startIndexSpider();

    /**
     * 爬取列表信息（连载列表、完结列表、国创列表）
     */
    void startListSpider();
}
