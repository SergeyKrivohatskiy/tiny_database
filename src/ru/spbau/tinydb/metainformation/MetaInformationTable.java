package ru.spbau.tinydb.metainformation;

import ru.spbau.tinydb.bufferManager.BufferManager;
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

    private final static Collection<Attribute> META_TABLE_SCHEME =
            Arrays.asList(new Attribute("", new Attribute.VarcharType(255)),
                    new Attribute("", Attribute.IntegerType.getInstance()),
                    new Attribute("", Attribute.IntegerType.getInstance()));
    private final BufferManager bufferManager;
    private final Table table;


    public MetaInformationTable(BufferManager bufferManager) {
        this.bufferManager = bufferManager;
        table = new Table(bufferManager, BufferManager.METAINF_FIRST_PAGE, META_TABLE_SCHEME);
    }

    public Table loadTable(String name) {
        int ignore = 0;
        int attributesCount = 0;
        int firstPage = 0;
        Collection<Attribute> attributes = null;
        for(Object[] row: table) {
            if(ignore != 0) {
                ignore -= 1;
                continue;
            }
            String currName = (String) row[0];
            if(attributes != null) {
                attributesCount -= 1;
                Attribute.DataType type = intsToType(row);
                attributes.add(new Attribute(currName, type));
                if(attributesCount == 0) {
                    return new Table(bufferManager, firstPage, attributes);
                }
                continue;
            }
            if(!name .equals(currName)) {
                ignore = (Integer)row[2];
                continue;
            }
            attributes = new ArrayList<>();
            attributesCount = (Integer)row[2];
            firstPage = (Integer)row[1];
        }
        return null;
    }

    private Attribute.DataType intsToType(Object[] row) {
        switch ((Integer)row[1]){
            case 1:
                return Attribute.IntegerType.getInstance();
            case 2:
                return Attribute.DoubleType.getInstance();
            case 3:
                return new Attribute.VarcharType((Integer) row[2]);
        }
        throw new RuntimeException();
    }

    private void typeToInt(Attribute.DataType dataType, Object[] record) {
        if(dataType instanceof IntegerType) {
        	record[1] = 1;
        	return;
        }
        if(dataType instanceof DoubleType) {
        	record[1] = 2;
        	return;
        }
        if(dataType instanceof VarcharType) {
        	record[1] = 3;
        	record[2] = ((VarcharType) dataType).getLength();
        	return;
        }
        throw new RuntimeException();
    }

    public Table createTable(String name, Collection<Attribute> schema) throws UnsupportedEncodingException, ExecutionException {
        int firstPage = bufferManager.getFreePage();
        Object[] record = new Object[3];
        record[0] = name;
        record[1] = new Integer(firstPage);
        record[2] = new Integer(schema.size());
        table.insertRecord(record);
        for(Attribute attr: schema) {
        	record[0] = attr.getAttributeName();
            typeToInt(attr.getDataType(), record);
            table.insertRecord(record);
        }
        return new Table(bufferManager, firstPage, schema);
    }
}
