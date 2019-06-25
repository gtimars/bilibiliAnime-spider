package com.gtj.spider.controller;

import com.gtj.spider.service.EsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author gtj
 */
@RestController
public class EsController {

    @Autowired
    private EsService esService;


    @GetMapping("/anime/score")
    public List<Map<Object, Object>> getPercent() {
        return esService.getScorePercent();
    }


    @GetMapping("/{index}/{field}/{size}/top")
    public List<Map<Object, Object>> getTopValue(@PathVariable("index") String index,
                                                 @PathVariable("field") String field,
                                                 @PathVariable("size") Integer size) {
        return esService.getTop(field, size, index);
    }


    @GetMapping("/api/anime_detail/{seasonId}")
    public Map<Object,Object> getValue(@PathVariable("seasonId") String seasonId) {
        return esService.getViewByAnimeIndex(seasonId);
    }
}
