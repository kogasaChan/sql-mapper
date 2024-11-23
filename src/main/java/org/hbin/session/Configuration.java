package org.hbin.session;

import org.hbin.binding.MapperRegistry;
import org.hbin.mapping.MappedStatement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class Configuration {

    private DataSource dataSource;
    private final MapperRegistry mapperRegistry = new MapperRegistry();
    private final Map<String, MappedStatement> mappedStatementMap = new HashMap<>();

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    public <T> void addMapper(Class<T> type) {
        mapperRegistry.addMapper(type);
    }

    public boolean hasMapper(Class<?> type) {
        return mapperRegistry.hasMapper(type);
    }

    public MappedStatement getMappedStatementMap(String statementId) {
        MappedStatement ms = mappedStatementMap.get(statementId.trim());
        if (ms == null) {
            throw new RuntimeException("MappedStatement not found: " + statementId);
        }
        return ms;
    }

    public void addMappedStatement(String statementId, MappedStatement ms) {
        mappedStatementMap.put(statementId.trim(), ms);
    }

}
