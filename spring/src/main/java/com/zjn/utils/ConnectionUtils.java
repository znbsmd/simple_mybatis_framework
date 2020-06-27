package com.zjn.utils;


import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author zjn
 * @create 2020-05-28 16:03
 * @description
 */
public class ConnectionUtils {

    private ThreadLocal<Connection> threadLocal = new ThreadLocal<>();


    public Connection getCurrentThreadConn() throws SQLException {

        Connection connection = threadLocal.get();
//        System.out.println(connection);
        if(connection == null) {

            System.out.println("get connection");
            // 从连接池拿连接并绑定到线程
            connection = DruidUtils.getDruidDataSource().getConnection();
            // 绑定到当前线程
            threadLocal.set(connection);
        }
        System.out.println("ThreadLocal ---"+threadLocal.get());
        return connection;
    }
}
