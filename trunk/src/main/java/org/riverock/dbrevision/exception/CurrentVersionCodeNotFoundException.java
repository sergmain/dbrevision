package org.riverock.dbrevision.exception;

/**
 * User: SergeMaslyukov
 * Date: 28.07.2007
 * Time: 20:46:50
 */
public class CurrentVersionCodeNotFoundException extends DbRevisionException {

    /**
     * Empty constructor
     */
    public CurrentVersionCodeNotFoundException(){
        super();
    }

    /**
     * Constructor
     * @param s describing exception
     */
    public CurrentVersionCodeNotFoundException(String s){
        super(s);
    }

    /**
     * Constructor
     *
     * @param cause cause exception
     */
    public CurrentVersionCodeNotFoundException(Throwable cause){
        super(cause);
    }

    /**
     * Constructor
     *
     * @param s describing exception
     * @param cause cause exception
     */
    public CurrentVersionCodeNotFoundException(String s, Throwable cause){
        super(s, cause);
    }
}
