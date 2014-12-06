package ru.spbau.tinydb.engine;

import org.jetbrains.annotations.NotNull;

import ru.spbau.tinydb.common.DBException;
import java.util.*;

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

}
