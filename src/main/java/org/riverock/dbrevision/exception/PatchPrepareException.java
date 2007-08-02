package org.riverock.dbrevision.exception;

/**
 * User: SergeMaslyukov
 * Date: 28.07.2007
 * Time: 20:46:50
 */
public class PatchPrepareException extends DbRevisionException {

    /**
     * Empty constructor
     */
    public PatchPrepareException(){
        super();
    }

    /**
     * Constructor
     * @param s describing exception
     */
    public PatchPrepareException(String s){
        super(s);
    }

    /**
     * Constructor
     *
     * @param cause cause exception
     */
    public PatchPrepareException(Throwable cause){
        super(cause);
    }

    /**
     * Constructor
     *
     * @param s describing exception
     * @param cause cause exception
     */
    public PatchPrepareException(String s, Throwable cause){
        super(s, cause);
    }
}
