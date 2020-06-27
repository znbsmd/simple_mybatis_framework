package com.zjn.utils;

import java.sql.SQLException;

/**
 * @author zjn
 * @create 2020-05-28 16:03
 * @description
 */
public class TransactionManager {

    private ConnectionUtils connectionUtils;

    public void setConnectionUtils(ConnectionUtils connectionUtils) {
        this.connectionUtils = connectionUtils;
    }

    public void beginTransaction() throws SQLException {
        System.out.println("trans begin");
        connectionUtils.getCurrentThreadConn().setAutoCommit(false);
    }

    public void commit() throws SQLException {
        System.out.println("trans commit");
        connectionUtils.getCurrentThreadConn().commit();
    }

    public void rollback() throws SQLException {
        System.out.println("trans commit");
        connectionUtils.getCurrentThreadConn().rollback();
    }
}
