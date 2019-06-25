package com.gtj.spider.controller;

import com.gtj.spider.entity.Anime;
import com.gtj.spider.service.HbaseService;
import com.gtj.spider.service.SpiderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author gtj
 */
@RestController
public class HbaseController {

    @Autowired
    private HbaseService hbaseService;
    @Autowired
    private SpiderService spiderService;

    @GetMapping("/scan/{}")
    public List<Anime> scan(@PathVariable("tableName") String tableName) {
        return hbaseService.scan(tableName);
    }

    @GetMapping("/get/versiondata")
    public List<Map<String,Object>> getVersion(@RequestParam("tableName") String tableName,
                                       @RequestParam("rowName")String rowName,
                                       @RequestParam("familyName")String familyName,
                                       @RequestParam("qualifier")String qualifier,
                                       @RequestParam(required = false, defaultValue = "7")Integer versionNum) {
        List<Map<String, Object>> versionData = hbaseService.getVersionData(tableName, rowName, familyName, qualifier, versionNum);

        List<Map<String,Object>> list = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取增量
        if (versionData.size()>1) {
            for(int i=versionData.size()-1;i > 0; i--) {
                Map<String,Object> map = new HashMap<>();
                Integer value = Integer.parseInt(versionData.get(i-1).get("res").toString())-
                        Integer.parseInt(versionData.get(i).get("res").toString());
                map.put("value",value);
                map.put("time",simpleDateFormat.format(new Date((long)versionData.get(i).get("time"))) +" ~ " + "\n" +
                        simpleDateFormat.format(new Date((long)versionData.get(i-1).get("time"))));

                list.add(map);
            }
        } else {
            Map<String,Object> map = new HashMap<>();
            map.put("value",versionData.get(0).get("res").toString());
            map.put("time","总播放量(没有其他版本数据)");
            list.add(map);
        }
        return list;
    }
}
