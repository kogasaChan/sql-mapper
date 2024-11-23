package org.hbin.binding;

import org.hbin.session.SqlSession;

import java.lang.reflect.Proxy;

public class MapperProxyFactory<T> {

    private final Class<T> mapperInterface;

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    @SuppressWarnings("unchecked")
    public T newInstance(SqlSession sqlSession) {
        MapperProxy<T> proxy = new MapperProxy<>(sqlSession, mapperInterface);
        try {
            return (T) Proxy.newProxyInstance(
                    mapperInterface.getClassLoader(),
                    new Class[]{mapperInterface},
                    proxy);
        } catch (IllegalArgumentException | ClassCastException e) {
            throw new RuntimeException("Failed to create proxy instance for " + mapperInterface.getName(), e);
        }
    }

}
