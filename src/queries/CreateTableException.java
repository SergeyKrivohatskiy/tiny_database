package queries;

import common.DBException;

/**
 * @author adkozlov
 */
public class CreateTableException extends DBException {

    public CreateTableException(String message) {
        super(message);
    }
}
