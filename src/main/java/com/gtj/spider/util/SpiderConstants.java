package com.gtj.spider.util;

/**
 * 爬虫网页参数
 * @author gtj
 */
public class SpiderConstants {

    public final static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36";
    /**
     * 番剧索引url和参数
     */
    public final static String ANIME_INDEX_URL = "https://bangumi.bilibili.com/media/web_api/search/result?";
    public final static String ANIME_INDEX_URL_PARAMETER = "page=${page}&season_type=${season_type}&pagesize=${size}";

    /**
     * 番剧总体热度URL和参数
     */
    public final static String ANIME_SEASON_URL = "https://bangumi.bilibili.com/ext/web_api/season_count?";
    public final static String ANIME_SEASON_PARAMETER = "season_id=${season_id}&season_type=${season_type}";

    /**
     * 番剧分集信息
     */
    public final static String ANIME_EPISODE_URL = "https://api.bilibili.com/pgc/web/season/section?";

    /**
     * 分集列表页和参数
     */
    public final static String ANIME_EPISODE_LIST_URL = "http://api.bilibili.com/x/web-interface/newlist?";
    public final static String ANIME_EPISODE_LIST_PARAMETER = "rid=${rid}&pn=${pn}&ps=${ps}";
}
