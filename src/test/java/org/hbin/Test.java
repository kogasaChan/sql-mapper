package org.hbin;

import org.hbin.mappers.UserMapper;
import org.hbin.models.User;
import org.hbin.session.SqlSession;
import org.hbin.session.SqlSessionFactory;
import org.hbin.session.SqlSessionFactoryBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

public class Test {
    @org.junit.Test
    public void test() {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build("/mybatis-config.xml");

        try {
            SqlSession sqlSession = sqlSessionFactory.openSession();
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

            Map<String, Object> params = new LinkedHashMap<>();
            params.put("id", 1);
            params.put("name", "John");
            userMapper.insertUser(params);

            User user = userMapper.getUser(1, "John");
            System.out.println("uid=" + user.getId() + " " + "username=" + user.getName());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
