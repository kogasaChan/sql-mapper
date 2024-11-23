package org.hbin.executor.parameter;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ParameterHandler {

    private final PreparedStatement preparedStatement;
    private final Object parameter;

    public ParameterHandler(PreparedStatement preparedStatement, Object parameter) {
        this.preparedStatement = preparedStatement;
        this.parameter = parameter;
    }

    public void setParameters() throws SQLException {
        if (parameter == null) {
            return;
        }

        if (parameter instanceof Map) {
            setMapParameters((Map<String, Object>) parameter);
        } else if (parameter instanceof List) {
            setListParameters((List<Object>) parameter);
        } else if (parameter.getClass().isArray()) {
            setArrayParameters((Object[]) parameter);
        } else {
            setObjectParameters(parameter);
        }
    }

    private void setMapParameters(Map<String, Object> paramMap) throws SQLException {
        int index = 1;
        for (Object value : paramMap.values()) {
            preparedStatement.setObject(index++, value);
        }
    }

    private void setListParameters(List<Object> paramList) throws SQLException {
        for (int i = 0; i < paramList.size(); i++) {
            preparedStatement.setObject(i + 1, paramList.get(i));
        }
    }

    private void setArrayParameters(Object[] paramArray) throws SQLException {
        for (int i = 0; i < paramArray.length; i++) {
            preparedStatement.setObject(i + 1, paramArray[i]);
        }
    }

    private void setObjectParameters(Object obj) throws SQLException {
        Field[] fields = obj.getClass().getDeclaredFields();
        int index = 1;
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(obj);
                preparedStatement.setObject(index++, value);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to set bean parameters: " + obj.getClass().getName(), e);
        }
    }

}
