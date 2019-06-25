package com.gtj.spider.spider.process;

import com.gtj.spider.entity.EpisodeDetail;
import com.gtj.spider.util.SpiderConstants;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;

import java.util.ArrayList;
import java.util.List;

/**
 * 列表信息页解析
 * @author gtj
 */
@Slf4j
public class AnimeEpisodeProcess implements PageProcessor{
    public static int COUNT = 0;
    public static int SPIDER_COUNT = 0;

    private Site site = Site.me().setCycleRetryTimes(3).setRetryTimes(3).setSleepTime(2000).
            setTimeOut(20000).setCharset("UTF-8").
            addHeader("Accept-Encoding"," gzip, deflate, br").
            addHeader("Accept-Language","zh-CN,zh;q=0.9").
            addHeader("Cache-Control","no-cache").addHeader("Connection","keep-alive").
            addHeader("Host","api.bilibili.com").
            setUserAgent(SpiderConstants.USER_AGENT);

    @Override
    public void process(Page page) {
        if(page.getStatusCode()==200) {
            if(page.getJson().jsonPath("$.code").get().equals("0")) {
                List<EpisodeDetail> episodeDetailList = new ArrayList<>();

                if(!page.getJson().jsonPath("$.data.archives[*]").all().isEmpty()){
                    for(String info: page.getJson().jsonPath("$.data.archives[*]").all()) {
                        EpisodeDetail episodeDetail = new EpisodeDetail();
                        //基本信息
                        episodeDetail.setAid(new JsonPathSelector("$.aid").select(info));
                        episodeDetail.setVideos(new JsonPathSelector("$.videos").select(info));
                        episodeDetail.setPtime(new JsonPathSelector("$.pubdate").select(info));
                        episodeDetail.setTitle(new JsonPathSelector("$.title").select(info));
                        //up
                        episodeDetail.setMid(new JsonPathSelector("$.owner.mid").select(info));
                        episodeDetail.setUps(new JsonPathSelector("$.owner.name").select(info));
                        //热度信息
                        episodeDetail.setCoin(new JsonPathSelector("$.stat.coin").select(info));
                        episodeDetail.setDanmaku(new JsonPathSelector("$.stat.danmaku").select(info));
                        episodeDetail.setFavorite(new JsonPathSelector("$.stat.favorite").select(info));
                        episodeDetail.setLike(new JsonPathSelector("$.stat.like").select(info));
                        episodeDetail.setReply(new JsonPathSelector("$.stat.reply").select(info));
                        episodeDetail.setShare(new JsonPathSelector("$.stat.share").select(info));
                        episodeDetail.setView(new JsonPathSelector("$.stat.view").select(info));

                        episodeDetailList.add(episodeDetail);
                        SPIDER_COUNT++;
                    }
                    page.putField("episodeDetail",episodeDetailList);
                } else {
                    log.error("页面{}解析出错",page.getUrl().get());
                    page.setDownloadSuccess(false);
                }

                //如果是第一页，则获取所有列表页
                if(page.getUrl().regex("pn=1&").match()) {
                    //番剧总数
                    int pageCount = Integer.parseInt(page.getJson().jsonPath("$.data.page.count").get());
                    //每页的数量
                    int size = Integer.parseInt(page.getJson().jsonPath("$.data.page.size").get());
                    for (int i = 2; i <= (pageCount % size == 0 ? pageCount / size : pageCount / size + 1); i++) {
                        page.addTargetRequest(page.getUrl().get().replace("pn=1", "pn=" + String.valueOf(i)));
                    }
                    COUNT += pageCount;
                }
            }else {
                log.error("页面出错或不存在,URL:{}",page.getUrl().get());
            }
        } else {
            log.error("状态码出错，{}",page.getStatusCode());
            page.setDownloadSuccess(false);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }
}
