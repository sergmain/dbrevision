package org.riverock.dbrevision.exception;

/**
 * User: SergeMaslyukov
 * Date: 28.07.2007
 * Time: 20:46:50
 */
public class PatchParseException extends DbRevisionException {

    /**
     * Empty constructor
     */
    public PatchParseException(){
        super();
    }

    /**
     * Constructor
     * @param s describing exception
     */
    public PatchParseException(String s){
        super(s);
    }

    /**
     * Constructor
     *
     * @param cause cause exception
     */
    public PatchParseException(Throwable cause){
        super(cause);
    }

    /**
     * Constructor
     *
     * @param s describing exception
     * @param cause cause exception
     */
    public PatchParseException(String s, Throwable cause){
        super(s, cause);
    }
}
