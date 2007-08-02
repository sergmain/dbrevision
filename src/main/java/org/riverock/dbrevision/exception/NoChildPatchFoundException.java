package org.riverock.dbrevision.exception;

/**
 * User: SergeMaslyukov
 * Date: 28.07.2007
 * Time: 20:46:50
 */
public class NoChildPatchFoundException extends DbRevisionException {

    /**
     * Empty constructor
     */
    public NoChildPatchFoundException(){
        super();
    }

    /**
     * Constructor
     * @param s describing exception
     */
    public NoChildPatchFoundException(String s){
        super(s);
    }

    /**
     * Constructor
     *
     * @param cause cause exception
     */
    public NoChildPatchFoundException(Throwable cause){
        super(cause);
    }

    /**
     * Constructor
     *
     * @param s describing exception
     * @param cause cause exception
     */
    public NoChildPatchFoundException(String s, Throwable cause){
        super(s, cause);
    }
}
