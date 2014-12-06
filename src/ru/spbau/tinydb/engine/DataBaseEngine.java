package ru.spbau.tinydb.engine;

import org.jetbrains.annotations.NotNull;
import ru.spbau.tinydb.bufferManager.BufferManager;
import ru.spbau.tinydb.common.DBException;
import ru.spbau.tinydb.metainformation.MetaInformationTable;
import ru.spbau.tinydb.queries.Attribute;
import ru.spbau.tinydb.queries.SecondLevelId;
import ru.spbau.tinydb.table.Table;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author adkozlov
 */
public class DataBaseEngine implements AutoCloseable {

    @NotNull
    private static final DataBaseEngine INSTANCE = new DataBaseEngine();
    @NotNull
    private static final Map<String, IDataBase> DB_INSTANCES = new HashMap<>();

    private DataBaseEngine() {
    }

    public static DataBaseEngine getInstance() {
        return INSTANCE;
    }

    @NotNull
    public static IDataBase getDBInstance(@NotNull String dbFileName) throws DBException {
        IDataBase result = DB_INSTANCES.get(dbFileName);

        if (result == null) {
            result = new DataBase(dbFileName);
            DB_INSTANCES.put(dbFileName, result);
        }

        return result;
    }

    @Override
    public void close() throws Exception {
        for (IDataBase instance : DB_INSTANCES.values()) {
            instance.close();
        }
    }

    private static class DataBase implements IDataBase {

        @NotNull
        private final MetaInformationTable metaInf;
        @NotNull
        private final BufferManager bufferManager;

        private DataBase(@NotNull String dbFileName) throws DBException {
            bufferManager = new BufferManager(dbFileName);
            metaInf = new MetaInformationTable(bufferManager);
        }

        @NotNull
        public Iterator<Map<SecondLevelId, Object>> selectAll(@NotNull String tableName) throws DBException {
            return findTable(tableName).iterator();
        }

        public void insertRecord(@NotNull String tableName,
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

            try {
                table.insertRecord(row);
            } catch (UnsupportedEncodingException | ExecutionException e) {
                throw new DBException(e);
            }
        }

        private Table findTable(String tableName) {
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
        public void close() throws IOException {
            flush();
        }

        public void flush() {
            bufferManager.flushBuffer();
        }
    }
}
