package org.riverock.dbrevision.exception;

/**
 * User: SergeMaslyukov
 * Date: 28.07.2007
 * Time: 20:46:50
 */
public class CurrentVersionCodeWrongException extends DbRevisionException {

    /**
     * Empty constructor
     */
    public CurrentVersionCodeWrongException(){
        super();
    }

    /**
     * Constructor
     * @param s describing exception
     */
    public CurrentVersionCodeWrongException(String s){
        super(s);
    }

    /**
     * Constructor
     *
     * @param cause cause exception
     */
    public CurrentVersionCodeWrongException(Throwable cause){
        super(cause);
    }

    /**
     * Constructor
     *
     * @param s describing exception
     * @param cause cause exception
     */
    public CurrentVersionCodeWrongException(String s, Throwable cause){
        super(s, cause);
    }
}
