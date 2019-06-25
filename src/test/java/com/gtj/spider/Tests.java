package com.gtj.spider;

import com.gtj.spider.dao.DetailEsRepository;
import com.gtj.spider.entity.AnimeEsIndex;
import com.gtj.spider.dao.AnimeRepository;
import com.gtj.spider.entity.DetailEsIndex;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.range.InternalRange;
import org.elasticsearch.search.aggregations.bucket.terms.DoubleTerms;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.percentiles.Percentile;
import org.elasticsearch.search.aggregations.metrics.percentiles.PercentilesAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.percentiles.tdigest.InternalTDigestPercentiles;
import org.elasticsearch.search.aggregations.metrics.tophits.InternalTopHits;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ScrolledPage;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.junit4.SpringRunner;
import org.elasticsearch.search.aggregations.bucket.range.Range;

import java.text.NumberFormat;
import java.util.*;

/**
 * @author gtj
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class Tests {

    @Autowired
    AnimeRepository animeRepository;
    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    DetailEsRepository detailEsRepository;

    @Test
    public void search() {
        Iterable<AnimeEsIndex> favorite = animeRepository.findAll(Sort.by("favorite").ascending());
        for (AnimeEsIndex animeEsIndex : favorite) {
            System.out.println(animeEsIndex.getFavorite());
        }
    }

    @Test
    public void top() {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();

        builder.withSourceFilter(new FetchSourceFilter(new String[]{""},null));

        builder.addAggregation(AggregationBuilders.topHits("top").
                size(10).sort("favorite", SortOrder.DESC));

        AggregatedPage<AnimeEsIndex> aggPage =(AggregatedPage<AnimeEsIndex>)this.animeRepository.search(builder.build());

        TopHits topHits =(TopHits)aggPage.getAggregations().get("top");

        Iterator<SearchHit> iterator = topHits.getHits().iterator();
        while (iterator.hasNext()){
//            for(Object value: iterator.next().getSortValues()) {
//                System.out.println(value.toString());
//            }
            SearchHit next = iterator.next();
            System.out.println(next.getSourceAsMap().get("title").toString());
            System.out.println(next.getSourceAsMap().get("favorite").toString());
        }
    }

    @Test
    public void searchPage() {
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        // 添加基本分词查询
        queryBuilder.withQuery(QueryBuilders.matchQuery("finish", "0"));
        queryBuilder.withPageable(PageRequest.of(7,10));
        // 搜索，获取结果
        ScrolledPage<AnimeEsIndex> result = (ScrolledPage<AnimeEsIndex>) this.animeRepository.search(queryBuilder.build());

        System.out.println("总条数 = " + result.getTotalElements());
        // 总页数
        System.out.println("总页数 = " + result.getTotalPages());
        // 当前页
        System.out.println("当前页：" + result.getNumber());

        System.out.println("每页大小：" + result.getSize());

        for (AnimeEsIndex animeEsIndex : result) {
           // System.out.println(animeEsIndex.getTitle());
        }
    }

}
