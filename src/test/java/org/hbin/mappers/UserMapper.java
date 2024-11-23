package org.hbin.mappers;

import org.hbin.models.User;

import java.util.Map;

public interface UserMapper {
    User getUserById(int id);

    User getUser(int id, String name);

    int insertUser(Map<String, Object> map);
}
