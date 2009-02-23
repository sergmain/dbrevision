package org.riverock.dbrevision.exception;

/**
 * User: SergeMaslyukov
 * Date: 23.02.2009
 * Time: 19:45:38
 * $Id$
 */
public class ViewAlreadyExistException extends DbRevisionException {
    public ViewAlreadyExistException() {
    }

    public ViewAlreadyExistException(String s) {
        super(s);
    }

    public ViewAlreadyExistException(Throwable cause) {
        super(cause);
    }

    public ViewAlreadyExistException(String s, Throwable cause) {
        super(s, cause);
    }
}
