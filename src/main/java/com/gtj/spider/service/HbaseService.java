package com.gtj.spider.service;

import com.gtj.spider.entity.Anime;
import org.apache.hadoop.hbase.client.Put;

import java.util.List;
import java.util.Map;

/**
 * hbase service接口
 * @author gtj
 */
public interface HbaseService {

    /**
     * 通过表和rowkey查找
     * @param tableName
     * @param row
     * @return
     */
    Map<String, Object> queryByRow(String tableName, String row);

    /**
     * 批量存储
     * @param tableName
     * @param putList
     */
    void batchSave(String tableName, List<Put> putList);

    /**
     * 存储一条记录
     * @param tableName
     * @param rowkey
     * @param familyName
     * @param qualifier
     * @param value
     */
    void saveOneColumn(String tableName, String rowkey, String familyName, String qualifier, String value);

    /**
     * 存储一行数据
     * @param tableName
     * @param put
     */
    void saveOneRow(String tableName, Put put);

    /**
     * 获取多个版本的数据
     * @param tableName
     * @param rowName
     * @param familyName
     * @param qualifier
     * @return
     */
    List<Map<String,Object>> getVersionData(String tableName, String rowName, String familyName, String qualifier, Integer versionId);

    /**
     * 扫描全表
     * @param tableName
     * @return
     */
    List<Anime> scan(String tableName);

    /**
     * 获取某一单元格的值
     * @param tableName
     * @param rowkey
     * @param family
     * @param column
     * @return
     */
    String getCell(String tableName, String rowkey, String family, String column);
}
