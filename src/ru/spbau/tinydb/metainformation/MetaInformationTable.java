package ru.spbau.tinydb.metainformation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.tinydb.bufferManager.BufferManager;
import ru.spbau.tinydb.common.DBException;
import ru.spbau.tinydb.queries.Attribute;
import ru.spbau.tinydb.queries.Attribute.DoubleType;
import ru.spbau.tinydb.queries.Attribute.IntegerType;
import ru.spbau.tinydb.queries.Attribute.VarcharType;
import ru.spbau.tinydb.queries.SecondLevelId;
import ru.spbau.tinydb.table.Table;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * tiny_database
 * Created by Sergey on 08.11.2014.
 */
public class MetaInformationTable {

    private final static Collection<Attribute> META_TABLE_SCHEME =
            Arrays.asList(new Attribute("name", new Attribute.VarcharType(255)),
                    new Attribute("val1", Attribute.IntegerType.getInstance()),
                    new Attribute("val2", Attribute.IntegerType.getInstance()));
    private final static SecondLevelId NAME_ID = new SecondLevelId("metainf", "name");
    private final static SecondLevelId VAL1_ID = new SecondLevelId("metainf", "val1");
    private final static SecondLevelId VAL2_ID = new SecondLevelId("metainf", "val2");

    @NotNull
    private final BufferManager bufferManager;
    @NotNull
    private final Table table;

    public MetaInformationTable(@NotNull BufferManager bufferManager) {
        this.bufferManager = bufferManager;
        table = new Table(bufferManager, BufferManager.METAINF_FIRST_PAGE, META_TABLE_SCHEME, "metainf");
    }

    @Nullable
    public Table loadTable(@NotNull String name) {
        int ignore = 0;
        int attributesCount = 0;
        int firstPage = 0;
        Collection<Attribute> attributes = null;
        for(Map<SecondLevelId, Object> row: table) {
            if(ignore != 0) {
                ignore -= 1;
                continue;
            }
            String currName = (String) row.get(NAME_ID);
            if(attributes != null) {
                attributesCount -= 1;
                Attribute.DataType type = intsToType(row);
                attributes.add(new Attribute(currName, type));
                if(attributesCount == 0) {
                    return new Table(bufferManager, firstPage, attributes, name);
                }
                continue;
            }
            if(!name .equals(currName)) {
                ignore = (Integer)row.get(VAL2_ID);
                continue;
            }
            attributes = new ArrayList<>();
            attributesCount = (Integer)row.get(VAL2_ID);
            firstPage = (Integer)row.get(VAL1_ID);
        }

        return null;
    }

    @NotNull
    private Attribute.DataType intsToType(Map<SecondLevelId, Object> row) {
        switch ((Integer)row.get(VAL1_ID)){
            case 1:
                return Attribute.IntegerType.getInstance();
            case 2:
                return Attribute.DoubleType.getInstance();
            case 3:
                return new Attribute.VarcharType((Integer) row.get(VAL2_ID));
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
        return new Table(bufferManager, firstPage, schema, name);
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