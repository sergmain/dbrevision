package org.riverock.dbrevision.exception;

/**
 * User: SergeMaslyukov
 * Date: 28.07.2007
 * Time: 20:46:50
 */
public class CurrentVersionNotDefinedException extends DbRevisionException {

    /**
     * Empty constructor
     */
    public CurrentVersionNotDefinedException(){
        super();
    }

    /**
     * Constructor
     * @param s describing exception
     */
    public CurrentVersionNotDefinedException(String s){
        super(s);
    }

    /**
     * Constructor
     *
     * @param cause cause exception
     */
    public CurrentVersionNotDefinedException(Throwable cause){
        super(cause);
    }

    /**
     * Constructor
     *
     * @param s describing exception
     * @param cause cause exception
     */
    public CurrentVersionNotDefinedException(String s, Throwable cause){
        super(s, cause);
    }
}
