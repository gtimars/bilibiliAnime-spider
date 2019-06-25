package com.gtj.spider.dao;

import com.gtj.spider.entity.DetailEsIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Collection;
import java.util.List;

/**
 * @author gtj
 */
public interface DetailEsRepository extends ElasticsearchRepository<DetailEsIndex,String>{

    /**
     * 通过aid列表查询
     * @param aids 剧集Id号
     * @return
     */
    List<DetailEsIndex> findByAidIn(Collection<String> aids);
}
