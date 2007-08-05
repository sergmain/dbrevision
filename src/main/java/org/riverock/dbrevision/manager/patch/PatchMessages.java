package org.riverock.dbrevision.manager.patch;

import java.util.List;
import java.util.ArrayList;

/**
 * User: SergeMaslyukov
 * Date: 05.08.2007
 * Time: 14:51:32
 */
public class PatchMessages {

    private List<PatchMessage> messages = new ArrayList<PatchMessage>();

    public void addMessage(String message) {
        getMessages().add( new PatchMessage(message) );
    }

    public void addMessage(PatchMessage message) {
        getMessages().add( message );
    }

    public List<PatchMessage> getMessages() {
        if (messages==null) {
            messages = new ArrayList<PatchMessage>();
        }
        return messages;
    }
}
