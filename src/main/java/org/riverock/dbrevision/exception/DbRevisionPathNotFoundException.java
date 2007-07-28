package org.riverock.dbrevision.exception;

/**
 * User: SergeMaslyukov
 * Date: 28.07.2007
 * Time: 20:46:50
 */
public class DbRevisionPathNotFoundException extends DbRevisionException {

    /**
     * Empty constructor
     */
    public DbRevisionPathNotFoundException(){
        super();
    }

    /**
     * Constructor
     * @param s describing exception
     */
    public DbRevisionPathNotFoundException(String s){
        super(s);
    }

    /**
     * Constructor
     *
     * @param cause cause exception
     */
    public DbRevisionPathNotFoundException(Throwable cause){
        super(cause);
    }

    /**
     * Constructor
     *
     * @param s describing exception
     * @param cause cause exception
     */
    public DbRevisionPathNotFoundException(String s, Throwable cause){
        super(s, cause);
    }
}


