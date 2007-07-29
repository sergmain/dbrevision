package org.riverock.dbrevision.exception;

/**
 * User: SergeMaslyukov
 * Date: 29.07.2007
 * Time: 0:41:59
 */
public class ConfigParseException extends DbRevisionException {

    /**
     * Empty constructor
     */
    public ConfigParseException(){
        super();
    }

    /**
     * Constructor
     * @param s describing exception
     */
    public ConfigParseException(String s){
        super(s);
    }

    /**
     * Constructor
     *
     * @param cause cause exception
     */
    public ConfigParseException(Throwable cause){
        super(cause);
    }

    /**
     * Constructor
     *
     * @param s describing exception
     * @param cause cause exception
     */
    public ConfigParseException(String s, Throwable cause){
        super(s, cause);
    }
}
