package org.hbin.mapping;

import org.hbin.constants.SqlCommandType;

public class MappedStatement {

    private String namespace;
    private String id;
    private String statementId;
    private String sql;
    private SqlCommandType sqlCommandType;
    private Class<?> resultType;

    public MappedStatement(String namespace, String id, String sql, SqlCommandType sqlCommandType, Class<?> resultType) {
        this.namespace = namespace;
        this.id = id;
        this.sql = sql;
        this.sqlCommandType = sqlCommandType;
        this.resultType = resultType;
        this.statementId = namespace + "." + id;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatementId() {
        return statementId;
    }

    public void setStatementId(String statementId) {
        this.statementId = statementId;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public void setSqlCommandType(SqlCommandType sqlCommandType) {
        this.sqlCommandType = sqlCommandType;
    }

    public Class<?> getResultType() {
        return resultType;
    }

    public void setResultType(Class<?> resultType) {
        this.resultType = resultType;
    }

}
