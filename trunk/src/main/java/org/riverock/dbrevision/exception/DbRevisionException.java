package org.riverock.dbrevision.exception;

/**
 * Generic db revision exception
 *
 * @author Sergei Maslyukov
 *         Date: 21.12.2006
 *         Time: 20:43:06
 *         <p/>
 *         $Id$
 */
public class DbRevisionException extends RuntimeException {

    /**
     * Empty constructor
     */
    public DbRevisionException(){
        super();
    }

    /**
     * Constructor
     * @param s describing exception
     */
    public DbRevisionException(String s){
        super(s);
    }

    /**
     * Constructor
     *
     * @param cause cause exception
     */
    public DbRevisionException(Throwable cause){
        super(cause);
    }

    /**
     * Constructor
     *
     * @param s describing exception
     * @param cause cause exception
     */
    public DbRevisionException(String s, Throwable cause){
        super(s, cause);
    }
}


