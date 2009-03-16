package org.riverock.dbrevision.exception;

/**
 * User: SergeMaslyukov
 * Date: 16.03.2009
 * Time: 17:26:00
 */
public class CreateTableException extends RuntimeException {
    public CreateTableException() {
    }

    public CreateTableException(String message) {
        super(message);
    }

    public CreateTableException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreateTableException(Throwable cause) {
        super(cause);
    }
}
