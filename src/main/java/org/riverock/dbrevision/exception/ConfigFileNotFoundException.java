package org.riverock.dbrevision.exception;

/**
 * User: SergeMaslyukov
 * Date: 29.07.2007
 * Time: 0:41:59
 */
public class ConfigFileNotFoundException extends DbRevisionException {

    /**
     * Empty constructor
     */
    public ConfigFileNotFoundException(){
        super();
    }

    /**
     * Constructor
     * @param s describing exception
     */
    public ConfigFileNotFoundException(String s){
        super(s);
    }

    /**
     * Constructor
     *
     * @param cause cause exception
     */
    public ConfigFileNotFoundException(Throwable cause){
        super(cause);
    }

    /**
     * Constructor
     *
     * @param s describing exception
     * @param cause cause exception
     */
    public ConfigFileNotFoundException(String s, Throwable cause){
        super(s, cause);
    }
}
