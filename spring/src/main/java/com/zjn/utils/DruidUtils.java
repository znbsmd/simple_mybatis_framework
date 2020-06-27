package com.zjn.utils;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * @author zjn
 * @create 2020-05-28 15:46
 * @description
 */
public class DruidUtils {

    public DruidUtils() {
    }

    private static DruidDataSource druidDataSource = new DruidDataSource();

    static {
        druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        druidDataSource.setUrl("jdbc:mysql://localhost:3306/mybatis");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("123321");

    }

    public static DruidDataSource getDruidDataSource() {
        return druidDataSource;
    }
}
