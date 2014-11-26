package metainformation;

import bufferManager.BufferManager;
import queries.Attribute;
import table.Table;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * tiny_database
 * Created by Sergey on 08.11.2014.
 */
public class MetaInformationTable {

    private final static Collection<Attribute> META_TABLE_SCHEME =
            Arrays.asList(new Attribute("", Attribute.DataType.VARCHAR),
                    new Attribute("", Attribute.DataType.INTEGER),
                    new Attribute("", Attribute.DataType.INTEGER));
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
                Attribute.DataType type = intToType((Integer)row[1]);
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

    private Attribute.DataType intToType(int i) {
        switch (i){
            case 1:
                return Attribute.DataType.INTEGER;
            case 2:
                return Attribute.DataType.CHAR;
            case 3:
                return Attribute.DataType.VARCHAR;
        }
        throw new RuntimeException();
    }

    private int typeToInt(Attribute.DataType dataType) {
        switch (dataType){
            case INTEGER:
                return 1;
            case CHAR:
                return 2;
            case VARCHAR:
                return 3;
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
            int p1 = typeToInt(attr.getDataType());
            record[1] = new Integer(p1);
            table.insertRecord(record);
        }
        return new Table(bufferManager, firstPage, schema);
    }
}
