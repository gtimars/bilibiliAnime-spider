package com.gtj.spider.spider.pipeline;

import com.gtj.spider.dao.DetailEsRepository;
import com.gtj.spider.entity.DetailEsIndex;
import com.gtj.spider.entity.EpisodeDetail;
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
 * 番剧列表详细页持久化
 * @author gtj
 */
@Component
@Slf4j
public class AnimeEpisodePipeline implements Pipeline{
    @Autowired
    private HbaseService hbaseService;
    @Autowired
    private DetailEsRepository detailEsRepository;

    @Override
    public void process(ResultItems resultItems, Task task) {
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()){
            if(entry.getKey().equals("episodeDetail")){
                List<EpisodeDetail> episodeDetail = (List<EpisodeDetail>) entry.getValue();
                //在es中建立索引
                toES(episodeDetail);
                //存储在hbase
                storeToHbase(episodeDetail);
            }
        }
    }

    /**
     * 存储在hbase
     * @param episodeDetail
     */
    private void storeToHbase(List<EpisodeDetail> episodeDetail) {
        List<Put> putList = new ArrayList<>();
        for (EpisodeDetail episode : episodeDetail) {
            if(!episode.getAid().isEmpty()) {
                Put put = new Put(episode.getAid().getBytes());
                put.addColumn(HBaseTableConstants.EPISODE_DETAIL_FAMILY.getBytes(),"title".getBytes(),episode.getTitle().getBytes());
                put.addColumn(HBaseTableConstants.EPISODE_DETAIL_FAMILY.getBytes(),"pubdate".getBytes(),episode.getPtime().getBytes());
                put.addColumn(HBaseTableConstants.EPISODE_DETAIL_FAMILY.getBytes(),"videos".getBytes(),episode.getVideos().getBytes());

                put.addColumn(HBaseTableConstants.EPISODE_DETAIL_FAMILY.getBytes(),"ups".getBytes(),episode.getUps().getBytes());
                put.addColumn(HBaseTableConstants.EPISODE_DETAIL_FAMILY.getBytes(),"mid".getBytes(),episode.getMid().getBytes());

                put.addColumn(HBaseTableConstants.EPISODE_DETAIL_FAMILY.getBytes(),"coin".getBytes(),episode.getCoin().getBytes());
                put.addColumn(HBaseTableConstants.EPISODE_DETAIL_FAMILY.getBytes(),"danmaku".getBytes(),episode.getDanmaku().getBytes());
                put.addColumn(HBaseTableConstants.EPISODE_DETAIL_FAMILY.getBytes(),"favorite".getBytes(),episode.getFavorite().getBytes());
                put.addColumn(HBaseTableConstants.EPISODE_DETAIL_FAMILY.getBytes(),"like".getBytes(),episode.getLike().getBytes());
                put.addColumn(HBaseTableConstants.EPISODE_DETAIL_FAMILY.getBytes(),"view".getBytes(),episode.getView().getBytes());
                put.addColumn(HBaseTableConstants.EPISODE_DETAIL_FAMILY.getBytes(),"reply".getBytes(),episode.getReply().getBytes());
                put.addColumn(HBaseTableConstants.EPISODE_DETAIL_FAMILY.getBytes(),"share".getBytes(),episode.getShare().getBytes());

                putList.add(put);
            }
        }
        try {
            hbaseService.batchSave(HBaseTableConstants.EPISODE_DETAIL_TABLE, putList);
        } catch (Exception e) {
            log.error("存储hbase失败，{}",e);
        }
    }

    /**
     * 在es中建立索引并插入数据
     * @param episodeDetail
     */
    private void toES(List<EpisodeDetail> episodeDetail) {
        List<DetailEsIndex> detailEsIndexList = new ArrayList<>();
        for (EpisodeDetail episode : episodeDetail) {
            if(!episode.getAid().isEmpty()) {
                DetailEsIndex detailEsIndex = new DetailEsIndex();
                detailEsIndex.setAid(episode.getAid());
                detailEsIndex.setPtime(new Date((long) Integer.parseInt(episode.getPtime())*1000));
                detailEsIndex.setTitle(episode.getTitle());
                detailEsIndex.setUps(episode.getUps());
                detailEsIndex.setView(Integer.parseInt(episode.getView()));

                detailEsIndexList.add(detailEsIndex);
            }
        }
        try {
            detailEsRepository.saveAll(detailEsIndexList);
        } catch (Exception e) {
            log.error("存储es失败，{}",e);
        }
    }
}
