package com.zjn.test;

import com.zjn.dao.IUserDao;
import com.zjn.pojo.User;
import com.zjn.io.Resources;
import com.zjn.sqlSession.SqlSession;
import com.zjn.sqlSession.SqlSessionFactory;
import com.zjn.sqlSession.SqlSessionFactoryBuilder;
import org.junit.Test;
import java.io.InputStream;
import java.util.List;

/**
 * @author zjn
 * @create 2020-05-23 12:06
 * @description
 */
public class TestIPersistence {


    @Test
    public void test() throws Exception {

        // 加载配置文件到内存
        InputStream resourceAsSteam = Resources.getResourceAsSteam("sqlMapConfig.xml");
        // 解析配置文件&创建会话
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsSteam);

        SqlSession sqlSession = sqlSessionFactory.openSession();

        User user = new User();
        user.setId(1);
        user.setUsername("张三");
        User user2 = sqlSession.selectOne("com.zjn.dao.IUserDao.findAll", user);

        System.out.println(user2.getUsername());
//        User user2 = sqlSession.selectOne("user.selectOne", user);
//
//        List<User> users = sqlSession.selectList("user.selectList");
//        for (User u: users ) {
//            System.out.println(u);
//        }

        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        List<User> all = userDao.findAll();
        for (User user1 : all) {
            System.out.println(user1.getUsername());
        }
    }
}
