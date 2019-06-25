package com.gtj.spider.dao;

import com.gtj.spider.entity.AnimeEpisode;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @author gtj
 */
public interface AnimeEpisodeEsRepository extends ElasticsearchRepository<AnimeEpisode,String>{

    List<AnimeEpisode> findBySeasonId(String seasonId);
}
