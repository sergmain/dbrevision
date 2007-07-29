package org.riverock.dbrevision.exception;

/**
 * User: SergeMaslyukov
 * Date: 28.07.2007
 * Time: 20:46:50
 */
public class VersionPathNotFoundException extends DbRevisionException {

    /**
     * Empty constructor
     */
    public VersionPathNotFoundException(){
        super();
    }

    /**
     * Constructor
     * @param s describing exception
     */
    public VersionPathNotFoundException(String s){
        super(s);
    }

    /**
     * Constructor
     *
     * @param cause cause exception
     */
    public VersionPathNotFoundException(Throwable cause){
        super(cause);
    }

    /**
     * Constructor
     *
     * @param s describing exception
     * @param cause cause exception
     */
    public VersionPathNotFoundException(String s, Throwable cause){
        super(s, cause);
    }
}