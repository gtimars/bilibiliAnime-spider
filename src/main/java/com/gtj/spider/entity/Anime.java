package com.gtj.spider.entity;

import lombok.Data;

/**
 * @author gtj
 */
@Data
public class Anime {
    private String seasonId;    //番剧id

    private String finish; //是否完结

    private String isChina;    //是否国产

    private String title;   //番剧名

    private String indexShow;

    private String ptime;

    private String showUrl;

    private String pic; //图片

    private String score;   //评分

    private String coin;

    private String danmaku;

    private String favorite;

    private String view;
}
