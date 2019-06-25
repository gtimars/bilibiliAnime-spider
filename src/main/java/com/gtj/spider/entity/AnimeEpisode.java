package com.gtj.spider.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author gtj
 */
@Data
@Document(indexName = "episode",type = "episode")
public class AnimeEpisode {
    @Id
    private String aid; //视频编号

    @Field(type = FieldType.Keyword)
    private String seasonId;    //所属番剧

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;   //番剧名

    @Field(index = false,type = FieldType.Keyword)
    private String episode; //分集

    @Field(index = false,type = FieldType.Keyword)
    private String showUrl;    //观看地址
}
