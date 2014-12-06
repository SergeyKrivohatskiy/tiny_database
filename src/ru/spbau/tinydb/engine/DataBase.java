package ru.spbau.tinydb.engine;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.jetbrains.annotations.NotNull;

import ru.spbau.tinydb.bufferManager.BufferManager;
import ru.spbau.tinydb.common.DBException;
import ru.spbau.tinydb.cursors.AtributesCursor;
import ru.spbau.tinydb.cursors.NLJoinCursor;
import ru.spbau.tinydb.cursors.WhereCursor;
import ru.spbau.tinydb.expressions.comparison.JoinOnExpression;
import ru.spbau.tinydb.metainformation.MetaInformationTable;
import ru.spbau.tinydb.queries.Attribute;
import ru.spbau.tinydb.queries.SecondLevelId;
import ru.spbau.tinydb.queries.SelectionTable;
import ru.spbau.tinydb.queries.WhereCondition;
import ru.spbau.tinydb.table.Record;
import ru.spbau.tinydb.table.Table;

public class DataBase implements IDataBase {

    @NotNull
    private final MetaInformationTable metaInf;
    @NotNull
    private final BufferManager bufferManager;

    public DataBase(@NotNull String dbFileName) throws DBException {
        bufferManager = new BufferManager(dbFileName);
        metaInf = new MetaInformationTable(bufferManager);
    }

    @Override
    public int delete(@NotNull String tableName, @NotNull WhereCondition filter) throws DBException {
        Table table = findTable(tableName);
        Iterator<Record> recordIterator = new WhereCursor(table.iterator(), filter);
        int removed = 0;

        while (recordIterator.hasNext()) {
            Record record = recordIterator.next();
            if (filter.check(record.getAtributes())) {
                table.remove(record.getRecordId());
                removed += 1;
            }
        }

        return removed;
    }

    public int insert(@NotNull String tableName,
                      @NotNull List<String> attrs, @NotNull List<Object> record)
            throws DBException {
        Table table = findTable(tableName);

        Collection<Attribute> schema = table.getSchema();
        Object[] row = new Object[schema.size()];

        int i = 0;
        for (Attribute attr : schema) {
            int idx = attrs.indexOf(attr.getAttributeName());
            if (idx == -1) {
                row[i] = getDefault(attr.getDataType());
            } else {
                row[i] = record.get(idx);
            }
            i += 1;
        }

        // TODO return correct value of affected rows
        try {
            table.insertRecord(row);
            return 1;
        } catch (UnsupportedEncodingException | ExecutionException | ClassCastException e) {
            throw new DBException(e);
        }
    }

    @NotNull
    private Table findTable(String tableName) throws DBException {
        Table table = metaInf.loadTable(tableName);

        if (table == null) {
            throw new DBException("Table " + tableName + " doesn't exist");
        }
        return table;
    }

    private Object getDefault(Attribute.DataType dataType) {
        if (dataType instanceof Attribute.IntegerType) {
            return 0;
        }
        if (dataType instanceof Attribute.DoubleType) {
            return (double) 0;
        }
        if (dataType instanceof Attribute.VarcharType) {
            return "";
        }

        throw new DBException("Unsupported attribute type");
    }

    public boolean createTable(@NotNull String tableName,
                               @NotNull Collection<Attribute> schema) throws DBException {
        Table table = metaInf.loadTable(tableName);

        if (table == null) {
            try {
                metaInf.createTable(tableName, schema);
            } catch (UnsupportedEncodingException | ExecutionException e) {
                throw new DBException(e);
            }

            return true;
        } else {
            return false;
        }
    }

    @NotNull
    public Collection<Attribute> getTableSchema(String tableName) {
        return metaInf.loadTable(tableName).getSchema();
    }

    @Override
    public void close() throws Exception {
        flush();
    }

    @Override
    public void flush() {
        bufferManager.flushBuffer();
    }

    @Override
    public Iterator<Map<SecondLevelId, Object>> select(
            SelectionTable table, WhereCondition filter) {
        Iterator<Record> recordCursor = new WhereCursor(findTable(table.getTableName()).iterator(), filter);
        Iterator<Map<SecondLevelId, Object>> resultCursor = new AtributesCursor(recordCursor);
        for(JoinOnExpression joinExpresion: table.getExpressions()) {
            Table joinTable = findTable(joinExpresion.getTableName());
            resultCursor = new NLJoinCursor(resultCursor, new Iterable<Map<SecondLevelId,Object>>() {
                @Override
                public Iterator<Map<SecondLevelId, Object>> iterator() {
                    return new AtributesCursor(joinTable.iterator());
                }
            }, joinExpresion);
        }
        return resultCursor;
    }
}