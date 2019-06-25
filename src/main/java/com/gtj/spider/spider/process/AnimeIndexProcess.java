package com.gtj.spider.spider.process;

import com.gtj.spider.entity.Anime;
import com.gtj.spider.entity.AnimeEpisode;
import com.gtj.spider.util.SpiderConstants;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;

import java.util.*;

/**
 * 番剧索引页和分集信息解析
 * @author gtj
 */
@Slf4j
public class AnimeIndexProcess implements PageProcessor {

    public static int COUNT = 0;
    public static int INDEX_COUNT = 0;

    private Site site = Site.me().setCycleRetryTimes(3).setRetryTimes(3).setSleepTime(2000).
            setTimeOut(20000).setCharset("UTF-8").
            setUserAgent(SpiderConstants.USER_AGENT);

    @Override
    public void process(Page page) {
        if(page.getStatusCode()==200){
            if (page.getJson().jsonPath("$.code").get().equals("0")) {
                //获取索引列表页
                if (page.getUrl().regex("page=1&").match()) {
                    processTargetUrl(page);
                }

                //索引列表页解析
                if (page.getUrl().regex(SpiderConstants.ANIME_INDEX_URL).match()) {
                    processIndex(page);
                }

                //番剧信息解析
                if (page.getUrl().regex(SpiderConstants.ANIME_SEASON_URL).match()) {
                    processAnime(page);
                }

                //分集信息获取
                if (page.getUrl().regex(SpiderConstants.ANIME_EPISODE_URL).match()) {
                    processEpisode(page);
                }
            } else {
                log.error("页面出错或不存在，URL:{}",page.getUrl().get());
            }
        } else {
            log.error("页面{}返回状态码错误",page.getUrl().get());
            page.setDownloadSuccess(false);
        }
    }

    /**
     * 将解析列表页放入队列
     * @param page 解析页
     */
    private void processTargetUrl(Page page) {
        //番剧总数
        int total = Integer.parseInt(page.getJson().jsonPath("$.result.page.total").get());
        //每页的数量
        int size = Integer.parseInt(page.getJson().jsonPath("$.result.page.size").get());
        for (int i = 2; i <= (total % size == 0 ? total / size : total / size + 1); i++) {
            page.addTargetRequest(page.getUrl().get().replace("page=1", "page=" + String.valueOf(i)));
        }
        COUNT += total;
    }

    /**
     * 解析番剧索引列表页
     * @param page 解析页
     */
    private void processIndex(Page page) {
        for (String data : page.getJson().jsonPath("$.result.data[*]").all()) {
            if (!data.isEmpty()) {
                //附带参数传入下一个url
                Map<String, Object> map = new HashMap<>();
                map.put("title", new JsonPathSelector("$.title").select(data));
                map.put("finish", new JsonPathSelector("$.is_finish").select(data));
                try {
                    map.put("index",new JsonPathSelector("$.index_show").select(data));
                } catch (Exception e) {
                    map.put("index","");
                }
                try {
                    map.put("score", new JsonPathSelector("$.order.score").select(data));
                } catch (Exception e) {
                    map.put("score","");
                }
                map.put("pic", new JsonPathSelector("$.cover").select(data));
                map.put("showUrl", new JsonPathSelector("$.link").select(data));
                map.put("seasonId", new JsonPathSelector("$.season_id").select(data));
                map.put("pubdate",new JsonPathSelector("$.order.pub_date").select(data));
                //番剧信息url
                String season_url = SpiderConstants.ANIME_SEASON_URL + SpiderConstants.ANIME_SEASON_PARAMETER.
                        replace("${season_id}", new JsonPathSelector("$.season_id").select(data));
                if (page.getUrl().regex("season_type=1").match()) {
                    season_url = season_url.replace("${season_type}", "1");
                    map.put("isChina", "0");
                } else {
                    season_url = season_url.replace("${season_type}", "4");
                    map.put("isChina", "1");
                }

                Request request = new Request(season_url);
                request.setExtras(map);
                page.addTargetRequest(request);
            }
        }

    }

    /**
     * 番剧热度解析
     * @param page 解析页
     */
    private void processAnime(Page page) {
        Anime anime = new Anime();
        if(page.getRequest().getExtra("score").toString().equals("")) {
            anime.setScore("");
        }else {
            anime.setScore(page.getRequest().getExtra("score").toString().replace("分", ""));
        }
        anime.setPic(page.getRequest().getExtra("pic").toString());
        anime.setTitle(page.getRequest().getExtra("title").toString());
        anime.setShowUrl(page.getRequest().getExtra("showUrl").toString());
        anime.setSeasonId(page.getRequest().getExtra("seasonId").toString());
        anime.setPtime(page.getRequest().getExtra("pubdate").toString());
        anime.setIndexShow(page.getRequest().getExtra("index").toString());
        anime.setFinish(page.getRequest().getExtra("finish").toString());
        anime.setIsChina(page.getRequest().getExtra("isChina").toString());

        //热度参数
        anime.setCoin(page.getJson().jsonPath("$.result.coins").get());
        anime.setDanmaku(page.getJson().jsonPath("$.result.danmakus").get());
        anime.setFavorite(page.getJson().jsonPath("$.result.favorites").get());
        anime.setView(page.getJson().jsonPath("$.result.views").get());

        //存储番剧信息
        page.putField("anime", anime);
        INDEX_COUNT++;

        Map<String, Object> map = new HashMap<>();
        map.put("seasonId", anime.getSeasonId());
        map.put("title", anime.getTitle());
        map.put("url",anime.getShowUrl());

        //分集信息url
        Request request = new Request(SpiderConstants.ANIME_EPISODE_URL + "season_id=" + anime.getSeasonId());
        request.setExtras(map);
        page.addTargetRequest(request);
    }

    /**
     * 分集信息
     * @param page
     */
    private void processEpisode(Page page) {

        List<AnimeEpisode> episode_list = new ArrayList<>();
        List<String> aidList = new ArrayList<>();
        List<String> episodes = null;
        try {
            episodes = page.getJson().jsonPath("$.result.main_section.episodes[*]").all();
        } catch (Exception e) {
            try {
                episodes = page.getJson().jsonPath("$.result.section[*].episodes[*]").all();
            } catch (Exception e1) {
                episodes = page.getJson().jsonPath("$.result.section").all();
            }
        }

        if(!episodes.isEmpty()){
            for (String episode : episodes) {
                AnimeEpisode animeEpisode = new AnimeEpisode();
                animeEpisode.setAid(new JsonPathSelector("$.aid").select(episode));
                animeEpisode.setEpisode(new JsonPathSelector("$.title").select(episode));
                animeEpisode.setShowUrl(new JsonPathSelector("$.share_url").select(episode));
                animeEpisode.setSeasonId(page.getRequest().getExtra("seasonId").toString());
                animeEpisode.setTitle(page.getRequest().getExtra("title").toString());
                episode_list.add(animeEpisode);
                aidList.add(animeEpisode.getAid());
            }
        }
        //去除重复aid，修改其字段内容
        for (String aid : aidList) {
            if(Collections.frequency(aidList,aid) > 1){
                Iterator<AnimeEpisode> it = episode_list.iterator();
                while(it.hasNext()){
                    if(it.next().getAid().equals(aid)){
                        it.remove();
                    }
                }
                AnimeEpisode repeatAnmie = new AnimeEpisode();
                repeatAnmie.setAid(aid);
                repeatAnmie.setEpisode("合集");
                repeatAnmie.setSeasonId(page.getRequest().getExtra("seasonId").toString());
                repeatAnmie.setShowUrl(page.getRequest().getExtra("url").toString());
                repeatAnmie.setTitle(page.getRequest().getExtra("title").toString());
                episode_list.add(repeatAnmie);
            }
        }
       page.putField("episode",episode_list);
    }

    @Override
    public Site getSite() {
        return site;
    }

}
