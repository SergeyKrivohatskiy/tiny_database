package ru.spbau.tinydb.metainformation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.tinydb.bufferManager.BufferManager;
import ru.spbau.tinydb.common.DBException;
import ru.spbau.tinydb.queries.Attribute;
import ru.spbau.tinydb.queries.Attribute.DoubleType;
import ru.spbau.tinydb.queries.Attribute.IntegerType;
import ru.spbau.tinydb.queries.Attribute.VarcharType;
import ru.spbau.tinydb.table.Table;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

/**
 * tiny_database
 * Created by Sergey on 08.11.2014.
 */
public class MetaInformationTable {

    private static final Collection<Attribute> META_TABLE_SCHEME =
            Arrays.asList(new Attribute("", new Attribute.VarcharType()),
                    new Attribute("", Attribute.IntegerType.getInstance()),
                    new Attribute("", Attribute.IntegerType.getInstance()));

    @NotNull
    private final BufferManager bufferManager;
    @NotNull
    private final Table table;

    public MetaInformationTable(@NotNull BufferManager bufferManager) {
        this.bufferManager = bufferManager;
        table = new Table(bufferManager, BufferManager.METAINF_FIRST_PAGE, META_TABLE_SCHEME);
    }

    @Nullable
    public Table loadTable(@NotNull String name) {
        int ignore = 0;
        int attributesCount = 0;
        int firstPage = 0;
        Collection<Attribute> attributes = null;
        for (Object[] row : table) {
            if (ignore != 0) {
                ignore -= 1;
                continue;
            }
            String currName = (String) row[0];
            if (attributes != null) {
                attributesCount -= 1;
                Attribute.DataType type = recordToType(row);
                attributes.add(new Attribute(currName, type));
                if (attributesCount == 0) {
                    return new Table(bufferManager, firstPage, attributes);
                }
                continue;
            }
            if (!name.equals(currName)) {
                ignore = (Integer) row[2];
                continue;
            }
            attributes = new ArrayList<>();
            attributesCount = (Integer) row[2];
            firstPage = (Integer) row[1];
        }

        return null;
    }

    @NotNull
    private Attribute.DataType recordToType(@NotNull Object[] row) {
        switch ((Integer) row[1]) {
            case 1:
                return Attribute.IntegerType.getInstance();
            case 2:
                return Attribute.DoubleType.getInstance();
            case 3:
                return new Attribute.VarcharType((Integer) row[2]);
        }

        throw new DBException("Unknown type");
    }

    @NotNull
    public Table createTable(@NotNull String name, @NotNull Collection<Attribute> schema) throws UnsupportedEncodingException, ExecutionException {
        int firstPage = bufferManager.getFreePage();
        table.insertRecord(createRecord(name, firstPage, schema.size()));

        for (Attribute attribute : schema) {
            table.insertRecord(createRecord(attribute));
        }

        return new Table(bufferManager, firstPage, schema);
    }

    @NotNull
    private static Object[] createRecord(@NotNull String name) {
        Object[] result = new Object[3];
        result[0] = name;

        return result;
    }

    @NotNull
    private static Object[] createRecord(@NotNull String name, int firstPage, int size) {
        Object[] result = createRecord(name);

        result[1] = firstPage;
        result[2] = size;

        return result;
    }

    @NotNull
    private static Object[] createRecord(@NotNull Attribute attribute) {
        Object[] result = createRecord(attribute.getAttributeName());

        Attribute.DataType dataType = attribute.getDataType();
        if (dataType instanceof IntegerType) {
            result[1] = 1;
        } else if (dataType instanceof DoubleType) {
            result[1] = 2;
        } else if (dataType instanceof VarcharType) {
            result[1] = 3;
            result[2] = ((VarcharType) dataType).getLength();
        } else {
            throw new DBException("Unknown type");
        }

        return result;
    }
}
