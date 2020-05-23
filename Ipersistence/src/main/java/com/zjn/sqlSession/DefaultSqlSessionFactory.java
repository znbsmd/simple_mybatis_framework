package com.zjn.sqlSession;

import com.zjn.pojo.Configuration;

/**
 * @author zjn
 * @create 2020-05-23 11:51
 * @description
 */
public class DefaultSqlSessionFactory implements  SqlSessionFactory{

    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration);
    }
}
