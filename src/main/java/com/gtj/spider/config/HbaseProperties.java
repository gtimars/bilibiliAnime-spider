package com.gtj.spider.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @author gtj
 */
@ConfigurationProperties(prefix = "hbase")
@Data
public class HbaseProperties {
    private Map<String, String> config;
}
