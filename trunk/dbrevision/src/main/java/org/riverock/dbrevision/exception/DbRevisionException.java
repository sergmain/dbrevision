package org.riverock.dbrevision.exception;

/**
 * @author Sergei Maslyukov
 *         Date: 21.12.2006
 *         Time: 20:43:06
 *         <p/>
 *         $Id$
 */
public class DbRevisionException extends RuntimeException {

    public DbRevisionException(){
        super();
    }

    public DbRevisionException(String s){
        super(s);
    }

    public DbRevisionException(Throwable th){
        super(th);
    }

    public DbRevisionException(String s, Throwable cause){
        super(s, cause);
    }
}


