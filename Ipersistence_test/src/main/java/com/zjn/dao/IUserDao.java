package com.zjn.dao;

import com.zjn.pojo.User;

import java.util.List;

/**
 * @author zjn
 * @create 2020-05-22 23:07
 * @description
 */
public interface IUserDao {

    //查询所有用户
    public List<User> findAll() throws Exception;


    //根据条件进行用户查询
    public User findByCondition(User user) throws Exception;
}
