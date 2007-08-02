package org.riverock.dbrevision.exception;

/**
 * User: SergeMaslyukov
 * Date: 28.07.2007
 * Time: 20:46:50
 */
public class TwoPatchesWithEmptyPreviousPatchException extends DbRevisionException {

    /**
     * Empty constructor
     */
    public TwoPatchesWithEmptyPreviousPatchException(){
        super();
    }

    /**
     * Constructor
     * @param s describing exception
     */
    public TwoPatchesWithEmptyPreviousPatchException(String s){
        super(s);
    }

    /**
     * Constructor
     *
     * @param cause cause exception
     */
    public TwoPatchesWithEmptyPreviousPatchException(Throwable cause){
        super(cause);
    }

    /**
     * Constructor
     *
     * @param s describing exception
     * @param cause cause exception
     */
    public TwoPatchesWithEmptyPreviousPatchException(String s, Throwable cause){
        super(s, cause);
    }
}