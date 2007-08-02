package org.riverock.dbrevision.exception;

/**
 * User: SergeMaslyukov
 * Date: 28.07.2007
 * Time: 20:46:50
 */
public class FirstPatchNotFoundException extends DbRevisionException {

    /**
     * Empty constructor
     */
    public FirstPatchNotFoundException(){
        super();
    }

    /**
     * Constructor
     * @param s describing exception
     */
    public FirstPatchNotFoundException(String s){
        super(s);
    }

    /**
     * Constructor
     *
     * @param cause cause exception
     */
    public FirstPatchNotFoundException(Throwable cause){
        super(cause);
    }

    /**
     * Constructor
     *
     * @param s describing exception
     * @param cause cause exception
     */
    public FirstPatchNotFoundException(String s, Throwable cause){
        super(s, cause);
    }
}
