package com.gtj.spider.dao;

import com.gtj.spider.entity.AnimeEsIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * @author gtj
 */
public interface AnimeRepository extends ElasticsearchRepository<AnimeEsIndex, String>{

}
