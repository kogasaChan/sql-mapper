package org.hbin.binding;

import org.hbin.session.SqlSession;

import java.util.HashMap;
import java.util.Map;

public class MapperRegistry {

    private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        MapperProxyFactory<T> factory = (MapperProxyFactory<T>) knownMappers.get(type);
        if (factory == null) {
            throw new RuntimeException("Type " + type + " is not known to the MapperRegistry.");
        }
        return factory.newInstance(sqlSession);
    }

    public <T> void addMapper(Class<T> type) {
        if (knownMappers.containsKey(type)) {
            throw new RuntimeException("Mapper " + type + " is already registered.");
        }
        if (!type.isInterface()) {
            throw new RuntimeException("Mapper " + type + " must be an interface.");
        }
        knownMappers.put(type, new MapperProxyFactory<>(type));
    }

    public boolean hasMapper(Class<?> type) {
        return knownMappers.containsKey(type);
    }

}
