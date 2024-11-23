package org.hbin.session;

public interface SqlSession {

    <T> T getMapper(Class<T> mapperClass);

    <T> T select(String statementId, Object parameter);

    int insert(String statementId, Object parameter);

    int update(String statementId, Object parameter);

    int delete(String statementId, Object parameter);

    Configuration getConfiguration();
}
