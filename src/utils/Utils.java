package utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import queries.Attribute;

/**
 * tiny_database
 * Created by Sergey on 08.11.2014.
 */
public class Utils {
    public static byte[] intToBytes(int i) {
        return ByteBuffer.allocate(4).putInt(i).array();
    }

    public static int bytesToInt(byte[] intBytes) {
        return ByteBuffer.wrap(intBytes, 0, 4).getInt();
    }

    public static byte[] stringToBytes(String value, int size) throws UnsupportedEncodingException {
        byte[] res = new byte[size];
        byte[] stringBytes = value.getBytes("US-ASCII");
        if(stringBytes.length > size) {
            throw new IllegalArgumentException("String len is more than size");
        }
        System.arraycopy(stringBytes, 0, res, 0, stringBytes.length);
        Arrays.fill(res, stringBytes.length, res.length, (byte) 0);
        return res;
    }

    public static String bytesToString(byte[] bytes) {
        try {
            int i = 0;
            for(; i < bytes.length; i++) {
                if(bytes[i] == 0) {
                    break;
                }
            }
            return new String(bytes, 0, i, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            // Should not be
            throw new RuntimeException();
        }
    }
    
    public static byte[] doubleToBytes(double d) {
    	byte[] bytes = new byte[8];
    	ByteBuffer.wrap(bytes).putDouble(d);
    	return bytes;
    }
    
    public static Double bytesToDouble(byte[] bytes) {
    	return ByteBuffer.wrap(bytes).getDouble();
    }

    public static void printSchema(Collection<Attribute> schema) {
    	for(Attribute attr: schema) {
    		System.out.print("| " + attr.getAttributeName() + " ");
    	}
		System.out.println("|");
	}

    public static void printAll(Iterator<Object[]> cursor) {
        while (cursor.hasNext()){
            System.out.println(Arrays.toString(cursor.next()));
        }
    }
}
