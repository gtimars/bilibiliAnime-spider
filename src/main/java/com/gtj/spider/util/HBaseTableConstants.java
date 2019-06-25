package com.gtj.spider.util;

/**
 * hbase表信息
 * @author gtj
 */
public class HBaseTableConstants {
    /**
     * 分集列表页详细信息表和列簇
     */
    public static final String EPISODE_DETAIL_TABLE = "episode_detail";
    public static final String EPISODE_DETAIL_FAMILY = "detail_info";

    /**
     * 番剧索引页番剧信息表和列簇
     */
    public static final String ANIME_TABLE = "anime";
    public static final String ANIME_FAMILY = "anime_info";

    /**
     * 番剧的分集信息 表和列簇
     */
    public static final String ANIME_EPISODE_TABLE = "anime_episode";
    public static final String ANIME_EPISODE_FAMILY = "episode_info";
}
