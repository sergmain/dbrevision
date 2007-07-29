package org.riverock.dbrevision.exception;

/**
 * User: SergeMaslyukov
 * Date: 28.07.2007
 * Time: 20:46:50
 */
public class ModulePathNotFoundException extends DbRevisionException {

    /**
     * Empty constructor
     */
    public ModulePathNotFoundException(){
        super();
    }

    /**
     * Constructor
     * @param s describing exception
     */
    public ModulePathNotFoundException(String s){
        super(s);
    }

    /**
     * Constructor
     *
     * @param cause cause exception
     */
    public ModulePathNotFoundException(Throwable cause){
        super(cause);
    }

    /**
     * Constructor
     *
     * @param s describing exception
     * @param cause cause exception
     */
    public ModulePathNotFoundException(String s, Throwable cause){
        super(s, cause);
    }
}
