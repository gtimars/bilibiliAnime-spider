package com.gtj.spider.spider.pipeline;

import com.gtj.spider.dao.AnimeEpisodeEsRepository;
import com.gtj.spider.entity.Anime;
import com.gtj.spider.entity.AnimeEpisode;
import com.gtj.spider.entity.AnimeEsIndex;
import com.gtj.spider.dao.AnimeRepository;
import com.gtj.spider.service.HbaseService;
import com.gtj.spider.util.HBaseTableConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Put;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 番剧索引页及其分集信息持久化
 * @author gtj
 */
@Component
@Slf4j
public class AnimeIndexPipeline implements Pipeline{

    @Autowired
    private HbaseService hbaseService;
    @Autowired
    private AnimeRepository animeRepository;
    @Autowired
    private AnimeEpisodeEsRepository animeEpisodeEsRepository;

    @Override
    public void process(ResultItems resultItems, Task task) {
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            if(entry.getKey().equals("anime")) {
                storeAnime((Anime) entry.getValue());
            }
            if(entry.getKey().equals("episode")) {
                storeEpisode((List<AnimeEpisode>) entry.getValue());
            }
        }
    }

    /**
     * 存储番剧信息
     * @param anime 番剧实体信息
     */
    private void storeAnime(Anime anime) {
        if(!anime.getSeasonId().isEmpty()){
            Put put = new Put(anime.getSeasonId().getBytes());
            put.addColumn(HBaseTableConstants.ANIME_FAMILY.getBytes(),"title".getBytes(),anime.getTitle().getBytes());
            put.addColumn(HBaseTableConstants.ANIME_FAMILY.getBytes(),"showUrl".getBytes(),anime.getShowUrl().getBytes());
            put.addColumn(HBaseTableConstants.ANIME_FAMILY.getBytes(),"ptime".getBytes(),anime.getPtime().getBytes());
            put.addColumn(HBaseTableConstants.ANIME_FAMILY.getBytes(),"pic".getBytes(),anime.getPic().getBytes());
            put.addColumn(HBaseTableConstants.ANIME_FAMILY.getBytes(),"score".getBytes(),anime.getScore().getBytes());
            put.addColumn(HBaseTableConstants.ANIME_FAMILY.getBytes(),"indexShow".getBytes(),anime.getIndexShow().getBytes());
            put.addColumn(HBaseTableConstants.ANIME_FAMILY.getBytes(),"isChina".getBytes(),anime.getIsChina().toString().getBytes());
            put.addColumn(HBaseTableConstants.ANIME_FAMILY.getBytes(),"finish".getBytes(),anime.getFinish().toString().getBytes());

            put.addColumn(HBaseTableConstants.ANIME_FAMILY.getBytes(),"coin".getBytes(),anime.getCoin().getBytes());
            put.addColumn(HBaseTableConstants.ANIME_FAMILY.getBytes(),"danmaku".getBytes(),anime.getDanmaku().getBytes());
            put.addColumn(HBaseTableConstants.ANIME_FAMILY.getBytes(),"favorite".getBytes(),anime.getFavorite().getBytes());
            put.addColumn(HBaseTableConstants.ANIME_FAMILY.getBytes(),"view".getBytes(),anime.getView().getBytes());

            hbaseService.saveOneRow(HBaseTableConstants.ANIME_TABLE, put);
            //es索引
            AnimeEsIndex animeEsIndex = new AnimeEsIndex();
            animeEsIndex.setSeasonId(anime.getSeasonId());
            animeEsIndex.setTitle(anime.getTitle());
            animeEsIndex.setFavorite(Integer.parseInt(anime.getFavorite()));
            animeEsIndex.setFinish(anime.getFinish());
            animeEsIndex.setPtime(new Date((long) Integer.parseInt(anime.getPtime()) *1000));
            if(anime.getScore().equals("")) {
                animeEsIndex.setScore(0.0);
            }else {
                animeEsIndex.setScore(Double.parseDouble(anime.getScore()));
            }

            animeEsIndex.setIsChina(anime.getIsChina());
            animeRepository.save(animeEsIndex);
        }
    }

    /**
     * 存储番剧分集信息
     * @param animeEpisodes 分集实体信息
     */
    private void storeEpisode(List<AnimeEpisode> animeEpisodes) {
        List<Put> putList = new ArrayList<>();
        for (AnimeEpisode animeEpisode : animeEpisodes) {
            if(!animeEpisode.getAid().isEmpty()) {
                Put put = new Put(animeEpisode.getAid().getBytes());
                put.addColumn(HBaseTableConstants.ANIME_EPISODE_FAMILY.getBytes(), "seasonId".getBytes(),animeEpisode.getSeasonId().getBytes());
                put.addColumn(HBaseTableConstants.ANIME_EPISODE_FAMILY.getBytes(), "title".getBytes(),animeEpisode.getTitle().getBytes());
                put.addColumn(HBaseTableConstants.ANIME_EPISODE_FAMILY.getBytes(), "episode".getBytes(),animeEpisode.getEpisode().getBytes());

                if(animeEpisode.getShowUrl()==null) {
                    put.addColumn(HBaseTableConstants.ANIME_EPISODE_FAMILY.getBytes(), "showUrl".getBytes(),"".getBytes());
                }else {
                    put.addColumn(HBaseTableConstants.ANIME_EPISODE_FAMILY.getBytes(), "showUrl".getBytes(),animeEpisode.getShowUrl().getBytes());
                }

                putList.add(put);
            }
        }
        hbaseService.batchSave(HBaseTableConstants.ANIME_EPISODE_TABLE,putList);
        animeEpisodeEsRepository.saveAll(animeEpisodes);
    }
}
