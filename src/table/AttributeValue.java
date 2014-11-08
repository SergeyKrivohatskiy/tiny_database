package table;

import queries.Attribute;
import utils.Utils;

import java.io.UnsupportedEncodingException;

/**
 * tiny_database
 * Created by Sergey on 08.11.2014.
 */
public class AttributeValue {
    public final Attribute.DataType type;
    public final byte[] value;

    public AttributeValue(Attribute.DataType type, byte[] value) {
        this.type = type;
        this.value = value;
    }

    public AttributeValue(String value) throws UnsupportedEncodingException {
        this.type = Attribute.DataType.VARCHAR;
        this.value = Utils.stringToBytes(value, 255);
    }

    public AttributeValue(int intValue) {
        this.type = Attribute.DataType.INTEGER;
        this.value = Utils.intToBytes(intValue);
    }
}
