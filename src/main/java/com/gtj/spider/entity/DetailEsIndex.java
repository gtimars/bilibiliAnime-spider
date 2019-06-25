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
@Document(indexName = "animelist",type = "episode_detail")
public class DetailEsIndex implements Comparable<DetailEsIndex>{
    @Id
    private String aid;

    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String title;

    @Field(type = FieldType.Date, format = DateFormat.custom,pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date ptime;

    @Field(type = FieldType.Keyword)
    private String ups;

    @Field(index = false,type = FieldType.Integer)
    private Integer view;

    @Override
    public int compareTo(DetailEsIndex detail) {
        if(detail.getPtime().after(this.ptime)){
            return 1;
        }
        return -1;
    }
}
