package org.hbin.executor;

import org.hbin.mapping.MappedStatement;

import java.util.List;

public interface Executor {

    <E> List<E> doQuery(MappedStatement ms, Object parameter);

    int doUpdate(MappedStatement ms, Object parameter);

}
