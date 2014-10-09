package bufferManager;

import java.nio.ByteBuffer;

/**
 * Created by Sergey on 09.10.2014.
 */
public interface Page {
    /**
     * Returns ByteBuffer associated with this page
     * @param readOnly If true returns read only ByteBuffer
     *                 Else page is guaranteed to be written to disk if returned back to manager
     * @return ByteBuffer associated with this page
     */
    public ByteBuffer getBuffer(boolean readOnly);
}
