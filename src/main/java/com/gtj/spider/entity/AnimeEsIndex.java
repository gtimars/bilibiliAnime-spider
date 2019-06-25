package com.gtj.spider.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @author gtj
 */
@Data
@Document(indexName = "anime",type = "index")
public class AnimeEsIndex {
    @Id
    private String seasonId;    //番剧id

    @Field(type = FieldType.Keyword)
    private String finish; //是否完结

    @Field(type = FieldType.Keyword)
    private String isChina;    //是否国产

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;   //番剧名

    @Field(index = false,type = FieldType.Double)
    private Double score;   //评分

    @Field(index = false,type = FieldType.Integer)
    private Integer favorite;

    @Field(type = FieldType.Date, format = DateFormat.custom,pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd",timezone="GMT+8")
    private Date ptime;
}
