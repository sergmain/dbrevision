package org.riverock.dbrevision.exception;

/**
 * User: SergeMaslyukov
 * Date: 28.07.2007
 * Time: 20:46:50
 */
public class FirstVersionWithPatchdException extends DbRevisionException {

    /**
     * Empty constructor
     */
    public FirstVersionWithPatchdException(){
        super();
    }

    /**
     * Constructor
     * @param s describing exception
     */
    public FirstVersionWithPatchdException(String s){
        super(s);
    }

    /**
     * Constructor
     *
     * @param cause cause exception
     */
    public FirstVersionWithPatchdException(Throwable cause){
        super(cause);
    }

    /**
     * Constructor
     *
     * @param s describing exception
     * @param cause cause exception
     */
    public FirstVersionWithPatchdException(String s, Throwable cause){
        super(s, cause);
    }
}
