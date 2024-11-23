package org.hbin.executor;

import org.hbin.executor.parameter.ParameterHandler;
import org.hbin.executor.resultset.ResultHandler;
import org.hbin.mapping.MappedStatement;
import org.hbin.session.Configuration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SimpleExecutor implements Executor {

    private final Configuration configuration;

    public SimpleExecutor(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> doQuery(MappedStatement ms, Object parameter) {
        String sql = ms.getSql();

        if (sql == null || sql.isEmpty()) {
            throw new IllegalArgumentException("SQL statement cannot be null or empty.");
        }

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            setParameters(preparedStatement, parameter);

            ResultSet resultSet = preparedStatement.executeQuery();
            ResultHandler<E> resultHandler = new ResultHandler(ms.getResultType());

            return resultHandler.handleResult(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException("Error executing query: " + sql, e);
        }
    }

    @Override
    public int doUpdate(MappedStatement ms, Object parameter) {
        String sql = ms.getSql();

        if (sql == null || sql.isEmpty()) {
            throw new IllegalArgumentException("SQL statement cannot be null or empty.");
        }

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            setParameters(preparedStatement, parameter);

            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error executing update: " + sql, e);
        }
    }


    private Connection getConnection() throws SQLException {
        return configuration.getDataSource().getConnection();
    }

    private void setParameters(PreparedStatement preparedStatement, Object parameter) throws SQLException {
        ParameterHandler parameterHandler = new ParameterHandler(preparedStatement, parameter);
        parameterHandler.setParameters();
    }

}
