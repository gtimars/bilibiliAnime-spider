package com.gtj.spider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author gtj
 */
@SpringBootApplication
@EnableScheduling
public class SpiderApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpiderApplication.class,args);
    }
}
