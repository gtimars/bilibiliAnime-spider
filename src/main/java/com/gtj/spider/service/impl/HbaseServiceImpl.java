package com.gtj.spider.service.impl;

import com.gtj.spider.entity.Anime;
import com.gtj.spider.service.HbaseService;
import com.gtj.spider.util.HBaseUtil;
import org.apache.hadoop.hbase.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * hbase操作实现类
 * @author gtj
 */
@Service
public class HbaseServiceImpl implements HbaseService {

    @Autowired
    private HBaseUtil hBaseUtil;

    @Override
    public Map<String, Object> queryByRow(String tableName, String row) {
        return hBaseUtil.getByRow(tableName, row);
    }

    @Override
    public void batchSave(String tableName, List<Put> putList) {
        hBaseUtil.batchInsert(tableName,putList);
    }

    @Override
    public void saveOneColumn(String tableName, String rowkey, String familyName, String qualifier, String value) {
        hBaseUtil.insertOneColumn(tableName,rowkey,familyName,qualifier, value);
    }

    @Override
    public void saveOneRow(String tableName, Put put) {
        hBaseUtil.insertOneRow(tableName,put);
    }

    @Override
    public List<Map<String,Object>> getVersionData(String tableName, String rowName, String familyName, String qualifier, Integer versionId) {
        return hBaseUtil.getVersionData(tableName, rowName, familyName, qualifier,versionId);
    }

    @Override
    public List<Anime> scan(String tableName) {
        return hBaseUtil.searchAll(tableName, Anime.class);
    }

    @Override
    public String getCell(String tableName, String rowkey, String family, String column) {
        return hBaseUtil.getColumn(tableName, rowkey, family, column);
    }
}
