package com.gtj.spider.entity;

import lombok.Data;

/**
 * @author gtj
 */
@Data
public class EpisodeDetail {
    //基本信息
    private String aid;

    private String title;

    private String ptime;

    private String videos;
    //up主信息
    private String ups;
    private String mid;
    //热度信息
    private String coin;
    private String danmaku;
    private String favorite;
    private String like;
    private String view;
    private String reply;
    private String share;
}
