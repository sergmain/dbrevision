package org.riverock.dbrevision.exception;

/**
 * User: SergeMaslyukov
 * Date: 28.07.2007
 * Time: 20:46:50
 */
public class InitStructureFileNotFoundException extends DbRevisionException {

    /**
     * Empty constructor
     */
    public InitStructureFileNotFoundException(){
        super();
    }

    /**
     * Constructor
     * @param s describing exception
     */
    public InitStructureFileNotFoundException(String s){
        super(s);
    }

    /**
     * Constructor
     *
     * @param cause cause exception
     */
    public InitStructureFileNotFoundException(Throwable cause){
        super(cause);
    }

    /**
     * Constructor
     *
     * @param s describing exception
     * @param cause cause exception
     */
    public InitStructureFileNotFoundException(String s, Throwable cause){
        super(s, cause);
    }
}
