package com.gtj.spider.spider.task;

import com.gtj.spider.service.SpiderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author gtj
 */
@Component
public class ScheduleSpider {

    @Autowired
    private SpiderService spiderService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void indexTask() {
        spiderService.startIndexSpider();
    }

    @Async
    @Scheduled(cron = "0 0 0 * * ?")
    public void listTask() {
        spiderService.startListSpider();
    }
}
