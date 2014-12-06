package ru.spbau.tinydb.engine;

import org.jetbrains.annotations.NotNull;
import ru.spbau.tinydb.common.DBException;
import ru.spbau.tinydb.queries.Attribute;
import ru.spbau.tinydb.queries.SecondLevelId;
import ru.spbau.tinydb.queries.WhereCondition;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author adkozlov
 */
public interface IDataBase extends AutoCloseable {

    @NotNull
    public Iterator<Map<SecondLevelId, Object>> selectAll(@NotNull String tableName) throws DBException;

    public int insert(@NotNull String tableName, @NotNull List<String> attributes, @NotNull List<Object> record)
            throws DBException;

    public int delete(@NotNull String tableName, @NotNull WhereCondition filter) throws DBException;

    public boolean createTable(@NotNull String tableName,
                               @NotNull Collection<Attribute> schema) throws DBException;

    public void flush();

    @NotNull
    Iterable<Map<SecondLevelId, Object>> findTable(String tableName) throws DBException;
}
