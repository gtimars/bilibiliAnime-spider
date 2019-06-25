package com.gtj.spider.controller;

import com.gtj.spider.entity.AnimeEsIndex;
import com.gtj.spider.entity.DetailEsIndex;
import com.gtj.spider.service.EsService;
import com.gtj.spider.service.HbaseService;
import com.gtj.spider.util.HBaseTableConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


/**
 * @author gtj
 */
@Controller
public class AnimeController {

    @Autowired
    private HbaseService hbaseService;
    @Autowired
    private EsService esService;

    @RequestMapping("/index")
    public String index() {
        return "index";
    }

    /**
     * 日增量页面，返回某一集爬取的历史版本信息
     * @param tableName hbase表名
     * @param rowName   id
     * @param familyName    列簇
     * @param qualifier 列
     * @param versionNum    取出的版本数量
     * @return
     */
    @GetMapping("/view_version")
    public String echart(@RequestParam("tableName") String tableName,
                         @RequestParam("rowName")String rowName,
                         @RequestParam("familyName")String familyName,
                         @RequestParam("qualifier")String qualifier,
                         @RequestParam(required = false, defaultValue = "7")Integer versionNum) {
        return "version";
    }


    /**
     * 番剧列表页面，返回所有的番剧信息
     * @param model
     * @return
     */
    @GetMapping("/anime_all")
    public String list(Model model) {
        Iterable<AnimeEsIndex> all = esService.getAll();
        model.addAttribute("find",all);
        return "table/tables";
    }

    @GetMapping("/state/{field}/{value}")
    public String filter(Model model, @PathVariable("field") String field,
                         @PathVariable("value") String value,
                         @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                         @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        Page<AnimeEsIndex> result = esService.customSearch(field, value, pageNum,pageSize);
        model.addAttribute("pageNum",result.getNumber());
        model.addAttribute("pageSize",result.getSize());
        model.addAttribute("pages",result.getTotalPages());
        model.addAttribute("total",(int)result.getTotalElements());
        model.addAttribute("result",result.getContent());
        return "table/statetable";
    }

    @GetMapping("/search")
    public String search(Model model,
                         @RequestParam("query") String query,
                         @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                         @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        Page<DetailEsIndex> result = esService.searchTitleOrUps(query, pageNum, pageSize);
        model.addAttribute("search",result.getContent());
        model.addAttribute("pageNum",result.getNumber());
        model.addAttribute("pageSize",result.getSize());
        model.addAttribute("pages",result.getTotalPages());
        model.addAttribute("total",(int)result.getTotalElements());
        return "table/search";
    }


    /**
     * 某番剧信息页
     * @param model
     * @param seasonid
     * @return
     */
    @GetMapping("/anime_detail/{seasonid}")
    public String animeDetail(Model model,@PathVariable("seasonid") String seasonid) {
        //获取图片
        String pic = hbaseService.getCell(HBaseTableConstants.ANIME_TABLE,
                seasonid, HBaseTableConstants.ANIME_FAMILY, "pic");
        model.addAttribute("img",pic);
        return "anime";
    }

}
