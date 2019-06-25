package com.gtj.spider.service.impl;

import com.gtj.spider.spider.pipeline.AnimeEpisodePipeline;
import com.gtj.spider.spider.pipeline.AnimeIndexPipeline;
import com.gtj.spider.spider.process.AnimeEpisodeProcess;
import com.gtj.spider.spider.process.AnimeIndexProcess;
import com.gtj.spider.service.SpiderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * 爬虫实现类
 *
 * @author gtj
 */
@Service
@Slf4j
public class SpiderServiceImpl implements SpiderService {

    private static final String ANIME_TARGET_URL = "https://bangumi.bilibili.com/media/web_api/search/result?page=1&season_type=1&pagesize=30";

    private static final String ANIME_CHINA_TARGET_URL = "https://bangumi.bilibili.com/media/web_api/search/result?page=1&season_type=4&pagesize=30";

    private static final String SERIAL_LIST_TARGET_URL = "http://api.bilibili.com/x/web-interface/newlist?rid=33&type=0&pn=1&ps=50";

    private static final String END_LIST_TARGET_URL = "http://api.bilibili.com/x/web-interface/newlist?rid=32&type=0&pn=1&type=0&ps=50";

    private static final String CHINA_LIST_TARGET_URL = "http://api.bilibili.com/x/web-interface/newlist?rid=153&type=0&pn=1&ps=50";

    @Autowired
    private AnimeIndexPipeline animeIndexPipeline;
    @Autowired
    private AnimeEpisodePipeline animeEpisodePipeline;

    /*
    代理
     */
    public HttpClientDownloader proxy() {
        // 代理服务器
        final String proxyHost = "http-dyn.abuyun.com";
        final Integer proxyPort = 9020;
        // 代理隧道验证信息
        final String proxyUser = "";
        final String proxyPass = "";

        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new Proxy(proxyHost, proxyPort, proxyUser, proxyPass)));

        return httpClientDownloader;
    }

    @Override
    public void startIndexSpider() {
        Spider indexSpider = Spider.create(new AnimeIndexProcess()).addPipeline(animeIndexPipeline).
                setDownloader(proxy()).thread(7);

        indexSpider.setSpiderListeners(spiderListeners(indexSpider));
        //番剧索引页爬取
        indexSpider.addUrl(ANIME_TARGET_URL).run();
        indexSpider.addUrl(ANIME_CHINA_TARGET_URL).run();

        log.info("共有{}条番剧信息，解析成功{}条",
                AnimeIndexProcess.COUNT,AnimeIndexProcess.INDEX_COUNT);
    }

    @Override
    public void startListSpider() {
        Spider episodeListSpider = Spider.create(new AnimeEpisodeProcess()).addPipeline(animeEpisodePipeline).
                setDownloader(proxy()).thread(4);
        episodeListSpider.setSpiderListeners(spiderListeners(episodeListSpider));
        //列表页爬取
        episodeListSpider.addUrl(SERIAL_LIST_TARGET_URL).run();
        episodeListSpider.addUrl(END_LIST_TARGET_URL).run();
        episodeListSpider.addUrl(CHINA_LIST_TARGET_URL).run();

        log.info("共有{}条分集数据，解析成功{}条数据",
                AnimeEpisodeProcess.COUNT,AnimeEpisodeProcess.SPIDER_COUNT);
    }

    private static List<SpiderListener> spiderListeners(final Spider spider) {
        List<SpiderListener> spiderListeners = new ArrayList<>();
        spiderListeners.add(new SpiderListener() {
            @Override
            public void onSuccess(Request request) {

            }
            @Override
            public void onError(Request request) {
                Integer cycleTriedTimes = (Integer) request.getExtra(Request.CYCLE_TRIED_TIMES);
                if(cycleTriedTimes == null) {
                    cycleTriedTimes = 1;
                    request.putExtra(Request.CYCLE_TRIED_TIMES, cycleTriedTimes);
                    spider.addRequest(request);
                }else if(cycleTriedTimes < 3) {
                    cycleTriedTimes += 1;
                    request.putExtra(Request.CYCLE_TRIED_TIMES, cycleTriedTimes);
                    spider.addRequest(request);
                }else {
                    log.error("超过重试次数,{}",request.getUrl());
                }
            }
        });
        return spiderListeners;
    }
}
