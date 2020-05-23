package com.zjn.sqlSession;

import com.zjn.pojo.Configuration;
import com.zjn.pojo.MappedStatement;

import java.util.List;

/**
 * @author zjn
 * @create 2020-05-23 10:34
 * @description 查询执行器接口
 */
public interface Executor {

    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception;
}
