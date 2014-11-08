package utils;

import java.nio.ByteBuffer;

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
}
