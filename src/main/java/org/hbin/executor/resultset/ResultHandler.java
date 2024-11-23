package org.hbin.executor.resultset;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultHandler<T> {

    private final Class<T> resultType;
    private final Map<String, Field> fieldCache = new HashMap<>();

    public ResultHandler(Class<T> resultType) {
        this.resultType = resultType;
        initializeFieldCache();
    }

    private void initializeFieldCache() {
        Field[] fields = resultType.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            fieldCache.put(field.getName(), field);
        }
    }

    public List<T> handleResult(ResultSet resultSet) throws SQLException {
        ArrayList<T> resultList = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();

        while (resultSet.next()) {
            try {
                T instance = resultType.getDeclaredConstructor().newInstance();
                mapResultSetToInstance(resultSet, metaData, instance);
                resultList.add(instance);
            } catch (Exception e) {
                throw new RuntimeException("Error mapping result set to " + resultType.getName(), e);
            }
        }
        return resultList;
    }

    private void mapResultSetToInstance(ResultSet resultSet, ResultSetMetaData metaData, T instance) throws SQLException, IllegalAccessException {
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String columnName = metaData.getColumnName(i);
            Object value = resultSet.getObject(i);

            Field field = fieldCache.get(columnName);
            if (field != null) {
                field.set(instance, value);
            }
        }
    }

}
