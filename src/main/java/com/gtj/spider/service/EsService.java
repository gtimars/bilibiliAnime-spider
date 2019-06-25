package com.gtj.spider.service;

import com.gtj.spider.entity.AnimeEsIndex;
import com.gtj.spider.entity.DetailEsIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;

import java.util.List;
import java.util.Map;

/**
 * es实现接口
 * @author gtj
 */
public interface EsService {

    /**
     * 获取每个评分区间的数量
     * @return
     */
    List<Map<Object,Object>> getScorePercent();

    /**
     * 获取某字段的top k
     * @param field
     * @return
     */
    List<Map<Object,Object>> getTop(String field, Integer size, String index);

    /**
     * 查找所有
     * @return
     */
    Iterable<AnimeEsIndex> getAll();

    /**
     * 自定义查询
     * @param field
     * @param value
     * @return
     */
    Page<AnimeEsIndex> customSearch(String field, String value, Integer pageNum, Integer pageSize);

    /**
     * 根据标题或up主进行搜索
     * @param value 值
     * @param pageNum   页码
     * @param pageSize  每页数量
     * @return
     */
    Page<DetailEsIndex> searchTitleOrUps(String value, Integer pageNum, Integer pageSize);

    /**
     * 根据番剧Id号获取该番剧每一集的总播放量
     * @param seasonId
     * @return
     */
   Map<Object,Object> getViewByAnimeIndex(String seasonId);
}
