package org.hbin.binding;

import org.hbin.constants.SqlCommandType;
import org.hbin.mapping.MappedStatement;
import org.hbin.session.Configuration;
import org.hbin.session.SqlSession;
import org.hbin.utils.CommonUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

public class MapperProxy<T> implements InvocationHandler, Serializable {

    private final SqlSession sqlSession;
    private final Class<T> mapperInterface;

    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }

        Configuration configuration = sqlSession.getConfiguration();
        String statementId = mapperInterface.getName() + "." + method.getName();
        MappedStatement mappedStatementMap = configuration.getMappedStatementMap(statementId);

        if (mappedStatementMap == null) {
            throw new IllegalArgumentException("MappedStatement not found for: " + statementId);
        }

        SqlCommandType commandType = mappedStatementMap.getSqlCommandType();

        if (CommonUtils.isPrimitiveOrWrapper(args[0].getClass())) {
            args[0] = Arrays.stream(args).toArray();
        }

        return switch (commandType) {
            case SELECT -> sqlSession.select(statementId, args[0]);
            case INSERT -> sqlSession.insert(statementId, args[0]);
            case UPDATE -> sqlSession.update(statementId, args[0]);
            case DELETE -> sqlSession.delete(statementId, args[0]);
        };
    }

}
