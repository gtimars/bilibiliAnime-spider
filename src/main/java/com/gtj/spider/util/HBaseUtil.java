package com.gtj.spider.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.data.hadoop.hbase.TableCallback;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * hbase工具类
 *
 * @author gtj
 */
@Component
@Slf4j
public class HBaseUtil {
    @Autowired
    private HbaseTemplate hbaseTemplate;

    /**
     * 通过表名和key获取一行数据
     *
     * @param tableName 表名
     * @param rowName   行键
     */
    public Map<String, Object> getByRow(String tableName, String rowName) {
        return hbaseTemplate.get(tableName, rowName, new RowMapper<Map<String, Object>>() {
            @Override
            public Map<String, Object> mapRow(Result result, int i) throws Exception {
                List<Cell> ceList = result.listCells();
                Map<String, Object> map = new HashMap<String, Object>(16);
                if (ceList != null && ceList.size() > 0) {
                    for (Cell cell : ceList) {
                        map.put(Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength()),
                                Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
                    }
                }
                return map;
            }
        });
    }

    /**
     * 插入一条数据
     *
     * @param tableName  表名
     * @param rowkey     行键
     * @param familyName 列簇
     * @param qualifier  列
     * @param value      值
     * @return 空值
     */
    public Object insertOneColumn(String tableName, String rowkey, String familyName, String qualifier, String value) {
        if (StringUtils.isBlank(tableName) || StringUtils.isBlank(rowkey) || StringUtils.isBlank(qualifier) || value == null) {
            log.error("插入参数为空，tableName:{},rowkey:{},familyName:{},qualifier:{},value:{}",
                    tableName,rowkey,familyName,qualifier,value);
            return null;
        }
        try {
            hbaseTemplate.put(tableName, rowkey, familyName, qualifier, value.getBytes());
        } catch (Exception e) {
            log.error("插入记录失败：tableName:{},rowkey:{},familyName:{},qualifier:{},value:{}" ,
                                        tableName,rowkey,familyName,qualifier,value);
        }
        return null;
    }

    /**
     * 批量插入
     */
    public void batchInsert(final String tableName, final List<Put> putList) {
        hbaseTemplate.execute(tableName, new TableCallback<Object>() {
            @SuppressWarnings("deprecation")
            @Override
            public Object doInTable(HTableInterface table) throws Throwable {
                try {
                    table.put(putList);
                    //关闭表连接
                    //table.close();
                } catch (Exception e) {
                    log.error("批量插入失败，tableName:{}",tableName);
                }
                return null;
            }
        });
    }

    /**
     * 插入一行数据
     *
     * @param tableName 表名
     * @param put       插入信息
     */
    public void insertOneRow(final String tableName, final Put put) {
        hbaseTemplate.execute(tableName, new TableCallback<Object>() {
            @SuppressWarnings("deprecation")
            @Override
            public Object doInTable(HTableInterface table) throws Throwable {
                try {
                    table.put(put);
                    //关闭表连接
                    //table.close();
                } catch (Exception e) {
                    log.error("插入一行数据失败，tableName:{},row:{}",tableName,put.getRow());
                }
                return null;
            }
        });
    }

    /**
     * 扫描表
     *
     * @param tableName 表名
     * @param c 实体类
     * @param <T>   实体类类型
     * @return  表内存储的内容
     */
    public <T> List<T> searchAll(String tableName, final Class<T> c) {
        return hbaseTemplate.find(tableName, new Scan(), new RowMapper<T>() {
            @Override
            public T mapRow(Result result, int rowNum) throws Exception {
                T entity = c.newInstance();
                BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(entity);
                List<Cell> ceList = result.listCells();
                for (Cell cellItem : ceList) {
                    String cellName = new String(CellUtil.cloneQualifier(cellItem));
                    if (!"class".equals(cellName)) {
                        beanWrapper.setPropertyValue(cellName, new String(CellUtil.cloneValue(cellItem)));
                    }
                }
                return entity;
            }
        });
    }

    /**
     * 获取某一行某一列数据
     *
     * @param tableName 表名
     * @param rowkey    行键
     * @param family    列簇
     * @param column    列
     * @return  一列数据
     */
    public String getColumn(String tableName, String rowkey, String family, String column) {
        if (StringUtils.isBlank(tableName) || StringUtils.isBlank(family)
                || StringUtils.isBlank(rowkey) || StringUtils.isBlank(column)) {
            log.error("某一参数为空");
            return null;
        }
        return hbaseTemplate.get(tableName, rowkey, family, column, new RowMapper<String>() {
            public String mapRow(Result result, int rowNum) throws Exception {
                List<Cell> ceList = result.listCells();
                String res = null;
                //String res = "";
                if (ceList != null && ceList.size() > 0) {
                    for (Cell cell : ceList) {
                        res = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                    }
                }
                return res;
            }
        });
    }

    /**
     * 获取多版本数据
     *
     * @param tableName 表名
     * @param rowName   行键
     * @param familyName    列簇
     * @param qualifier 列
     * @param versionId 版本数量
     * @return  版本数据
     */
    public List<Map<String,Object>> getVersionData(final String tableName, final String rowName, final String familyName,
                                       final String qualifier, final Integer versionId) {
        return hbaseTemplate.execute(tableName, new TableCallback<List<Map<String, Object>>>() {
            @SuppressWarnings("deprecation")
            @Override
            public List<Map<String, Object>> doInTable(HTableInterface table) throws Throwable {
                Get get = new Get(rowName.getBytes());
                get.setMaxVersions(versionId); // 设置一次性获取多少个版本的数据
                get.addColumn(familyName.getBytes(), qualifier.getBytes());
                Result result = table.get(get);
                List<Cell> cells = result.listCells();
                String res = null;
                List<Map<String,Object>> list = new ArrayList<>();
                if (null != cells && !cells.isEmpty()) {
                    for (Cell ce : cells) {
                        Map<String, Object> map = new HashMap<>();
                        res = Bytes.toString(ce.getValueArray(),
                                ce.getValueOffset(),
                                ce.getValueLength());
                        //System.out.println("res:" + res + " timestamp:" + ce.getTimestamp());
                        map.put("res",res);
                        map.put("time",ce.getTimestamp());
                        list.add(map);
                    }
                }
                return list;
            }
        });
    }
}
