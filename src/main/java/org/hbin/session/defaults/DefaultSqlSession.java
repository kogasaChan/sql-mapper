package org.hbin.session.defaults;

import org.hbin.executor.Executor;
import org.hbin.executor.SimpleExecutor;
import org.hbin.mapping.MappedStatement;
import org.hbin.session.Configuration;
import org.hbin.session.SqlSession;

import java.util.List;

public class DefaultSqlSession implements SqlSession {

    private final Configuration configuration;
    private final Executor executor;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
        this.executor = new SimpleExecutor(configuration);
    }

    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }

    @Override
    public <T> T select(String statementId, Object parameter) {
        MappedStatement ms = getMappedStatement(statementId);
        List<T> results = executor.doQuery(ms, parameter);

        if (results == null || results.isEmpty()) {
            throw new RuntimeException("No results found for statementId: " + statementId);
        }
        return results.getFirst();
    }

    @Override
    public int insert(String statementId, Object parameter) {
        MappedStatement ms = getMappedStatement(statementId);
        return executor.doUpdate(ms, parameter);
    }

    @Override
    public int update(String statementId, Object parameter) {
        MappedStatement ms = getMappedStatement(statementId);
        return executor.doUpdate(ms, parameter);
    }

    @Override
    public int delete(String statementId, Object parameter) {
        MappedStatement ms = getMappedStatement(statementId);
        return executor.doUpdate(ms, parameter);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    private MappedStatement getMappedStatement(String statementId) {
        MappedStatement ms = configuration.getMappedStatementMap(statementId);
        if (ms == null) {
            throw new RuntimeException("MappedStatement not found for statementId: " + statementId);
        }
        return ms;
    }
}
