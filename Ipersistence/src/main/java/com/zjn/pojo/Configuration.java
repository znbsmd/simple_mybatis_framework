package com.zjn.pojo;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zjn
 * @create 2020-05-22 23:41
 * @description 基本配置类 （存放 连接资源 & 查询信息）
 */
public class Configuration {

    private DataSource dataSource;

    // 创建存放 查询具体信息的容器对象
    Map<String,MappedStatement> ms = new HashMap<>();

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map<String, MappedStatement> getMs() {
        return ms;
    }

    public void setMs(Map<String, MappedStatement> ms) {
        this.ms = ms;
    }
}
