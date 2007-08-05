package org.riverock.dbrevision.manager.patch;

/**
 * User: SergeMaslyukov
 * Date: 05.08.2007
 * Time: 14:51:52
 */
public class PatchMessage {
    private String message=null;

    public PatchMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
