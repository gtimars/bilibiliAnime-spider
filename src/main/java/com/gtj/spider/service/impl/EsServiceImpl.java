package com.gtj.spider.service.impl;

import com.gtj.spider.dao.AnimeEpisodeEsRepository;
import com.gtj.spider.dao.AnimeRepository;
import com.gtj.spider.dao.DetailEsRepository;
import com.gtj.spider.entity.AnimeEpisode;
import com.gtj.spider.entity.AnimeEsIndex;
import com.gtj.spider.entity.DetailEsIndex;
import com.gtj.spider.service.EsService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.SortBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.*;

/**
 * es操作实现类
 *
 * @author gtj
 */
@Service
@Slf4j
public class EsServiceImpl implements EsService {

    @Autowired
    AnimeRepository animeRepository;
    @Autowired
    DetailEsRepository detailEsRepository;
    @Autowired
    AnimeEpisodeEsRepository animeEpisodeEsRepository;
    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public List<Map<Object, Object>> getScorePercent() {
        List<Map<Object, Object>> list = new ArrayList<>();
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 不查询任何结果
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));
        //添加一个新的聚合，聚合类型为range，聚合字段为score
        //自定义评分区间为0(无评分)、0.1-7.9、8-8.4、8.5-8.9、9-9.5、9.5-10
        queryBuilder.addAggregation(
                AggregationBuilders.range("range").field("score").
                        addUnboundedFrom(0.0).addRange(0.1, 7.9).addRange(8.0, 8.4).
                        addRange(8.5, 8.9).addRange(9.0, 9.5).addRange(9.5, 10.0));

        AggregatedPage<AnimeEsIndex> aggPage = (AggregatedPage<AnimeEsIndex>) this.animeRepository.search(queryBuilder.build());

        Range agg = (Range) aggPage.getAggregations().get("range");
        for (Range.Bucket bucket : agg.getBuckets()) {
            Map<Object, Object> map = new HashMap<>();
            //区间
            map.put("score", bucket.getKeyAsString());
            //每个区间的数量
            map.put("value", bucket.getDocCount());
            list.add(map);
        }
        return list;
    }

    @Override
    public List<Map<Object, Object>> getTop(String field, Integer size, String index) {
        List<Map<Object, Object>> list = new ArrayList<>();

        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        //空查询
        builder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));

        builder.addAggregation(AggregationBuilders.topHits("top").
                size(size).sort(field, SortOrder.DESC));

        AggregatedPage<AnimeEsIndex> animePage = null;
        AggregatedPage<DetailEsIndex> detailPage = null;
        TopHits topHits = null;
        if (index.equals("anime")) {
            animePage = (AggregatedPage<AnimeEsIndex>) this.animeRepository.search(builder.build());
            topHits = animePage.getAggregations().get("top");
        } else if (index.equals("detail")) {
            detailPage = (AggregatedPage<DetailEsIndex>) this.detailEsRepository.search(builder.build());
            topHits = detailPage.getAggregations().get("top");
        }

        Iterator<SearchHit> iterator = topHits.getHits().iterator();
        while (iterator.hasNext()) {
            Map<Object, Object> map = new HashMap<>();
            SearchHit next = iterator.next();
            map.put("title", next.getSourceAsMap().get("title").toString());
            map.put("value", next.getSourceAsMap().get(field).toString());
            list.add(map);
        }
        return list;
    }

    @Override
    public Iterable<AnimeEsIndex> getAll() {
        return animeRepository.findAll();
    }

    @Override
    public Page<AnimeEsIndex> customSearch(String field, String value, Integer pageNum, Integer pageSize) {
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        if (field.equals("index") && value.equals("all")) {
            queryBuilder.withQuery(QueryBuilders.matchAllQuery());
        } else {
            // 添加基本分词查询
            queryBuilder.withQuery(QueryBuilders.matchQuery(field, value));
        }
        //根据时间字段降序
        queryBuilder.withSort(SortBuilders.fieldSort("ptime").order(SortOrder.DESC));
        queryBuilder.withPageable(PageRequest.of(pageNum, pageSize));

        // 搜索，获取结果
        Page<AnimeEsIndex> result = this.animeRepository.search(queryBuilder.build());
        return result;
    }

    @Override
    public Page<DetailEsIndex> searchTitleOrUps(String value, Integer pageNum, Integer pageSize) {

        String preTag = "<span style='color:red'>";//google的色值
        String postTag = "</span>";
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本分词查询
        queryBuilder.withQuery(
                QueryBuilders.boolQuery().
                        should(QueryBuilders.matchQuery("title", value)).
                        should(QueryBuilders.matchQuery("ups", value)));
        queryBuilder.withHighlightFields(new HighlightBuilder.Field("title").preTags(preTag).postTags(postTag),
                new HighlightBuilder.Field("ups").preTags(preTag).postTags(postTag));
        //按时间排序
        queryBuilder.withSort(SortBuilders.fieldSort("ptime").order(SortOrder.DESC));
        queryBuilder.withPageable(PageRequest.of(pageNum, pageSize));

        //不需要高亮
        Page<DetailEsIndex> result = this.detailEsRepository.search(queryBuilder.build());


//        // 搜索，获取结果(高亮)
//        Page<DetailEsIndex> result = elasticsearchTemplate.queryForPage(queryBuilder.build(), DetailEsIndex.class, new SearchResultMapper() {
//            @Override
//            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
//                List<DetailEsIndex> high = new ArrayList<>();
//                for (SearchHit searchHit : searchResponse.getHits()) {
//                    if (searchResponse.getHits().getHits().length <= 0) {
//                        return null;
//                    }
//                    DetailEsIndex detailEsIndex = new DetailEsIndex();
//                    //name or memoe
//                    HighlightField title = searchHit.getHighlightFields().get("title");
//                    if (title != null) {
//                        detailEsIndex.setTitle(title.fragments()[0].toString());
//                    } else {
//                        detailEsIndex.setTitle(String.valueOf(searchHit.getSourceAsMap().get("title")));
//                    }
//                    HighlightField ups = searchHit.getHighlightFields().get("ups");
//                    if (ups != null) {
//                        detailEsIndex.setUps(ups.fragments()[0].toString());
//                    } else {
//                        detailEsIndex.setUps(String.valueOf(searchHit.getSourceAsMap().get("ups")));
//                    }
//                    detailEsIndex.setView((Integer) searchHit.getSourceAsMap().get("view"));
//                    // detailEsIndex.setPtime((String.valueOf(searchHit.getSourceAsMap().get("ptime"))));
//                    high.add(detailEsIndex);
//                }
//                if (high.size() > 0) {
//                    return new AggregatedPageImpl<>((List<T>) high);
//                }
//                return null;
//            }
//        });
        return result;
    }

    @Override
    public Map<Object, Object> getViewByAnimeIndex(String seasonId) {
        Map<Object, Object> map = new LinkedHashMap<>();
        List<AnimeEpisode> bySeasonId = animeEpisodeEsRepository.findBySeasonId(seasonId);
        Collection<String> aidList = new ArrayList<>();
        if (bySeasonId.size() != 0) {
            map.put("title", bySeasonId.get(0).getTitle());
            for (AnimeEpisode animeEpisode : bySeasonId) {
                aidList.add(animeEpisode.getAid());
            }
            //根据id号查询分集信息
            List<DetailEsIndex> byAidIn = detailEsRepository.findByAidIn(aidList);
            List<DetailEsIndex> detailList = new ArrayList<>();

            for (DetailEsIndex s : byAidIn) {
                detailList.add(s);
            }
            //排序
            Collections.sort(detailList);
            map.put("value", detailList);
        } else {
            map = null;
        }
        return map;
    }
}
