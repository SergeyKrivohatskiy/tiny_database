package metainformation;

import bufferManager.BufferManager;
import queries.Attribute;
import table.AttributeValue;
import table.Table;
import utils.Utils;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * tiny_database
 * Created by Sergey on 08.11.2014.
 */
public class MetaInformationTable {

    private final static Collection<Attribute> META_TABLE_SCHEME =
            Arrays.asList(new Attribute("name", Attribute.DataType.VARCHAR),
                    new Attribute("p1", Attribute.DataType.INTEGER),
                    new Attribute("p2", Attribute.DataType.INTEGER));
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
        for(Map<String, AttributeValue> row: table) {
            if(ignore != 0) {
                ignore -= 1;
                continue;
            }
            String currName = Utils.bytesToString(row.get("name").value);
            if(attributes != null) {
                attributesCount -= 1;
                Attribute.DataType type = intToType(Utils.bytesToInt(row.get("p1").value));
                attributes.add(new Attribute(currName, type));
                if(attributesCount == 0) {
                    return new Table(bufferManager, firstPage, attributes);
                }
            }
            if(!name .equals(currName)) {
                ignore = Utils.bytesToInt(row.get("p2").value);
                continue;
            }
            attributes = new ArrayList<>();
            attributesCount = Utils.bytesToInt(row.get("p2").value);
            firstPage = Utils.bytesToInt(row.get("p1").value);
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
        Map<String, AttributeValue> record = new HashMap<>();
        record.put("name", new AttributeValue(Attribute.DataType.VARCHAR, Utils.stringToBytes(name, 255)));
        record.put("p1", new AttributeValue(Attribute.DataType.INTEGER, Utils.intToBytes(firstPage)));
        record.put("p2", new AttributeValue(Attribute.DataType.INTEGER, Utils.intToBytes(schema.size())));
        table.insertRecord(record);
        for(Attribute attr: schema) {
            record.put("name", new AttributeValue(Attribute.DataType.VARCHAR, Utils.stringToBytes(attr.getAttributeName(), 255)));
            int p1 = typeToInt(attr.getDataType());
            record.put("p1", new AttributeValue(Attribute.DataType.INTEGER, Utils.intToBytes(p1)));
            table.insertRecord(record);
        }
        return new Table(bufferManager, firstPage, schema);
    }
}
