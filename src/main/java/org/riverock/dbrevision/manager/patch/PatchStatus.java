package org.riverock.dbrevision.manager.patch;

/**
 * User: SergeMaslyukov
 * Date: 05.08.2007
 * Time: 15:12:05
 */
public class PatchStatus {
    public enum Status {SUCCESS, ERROR}

    private Status status = Status.SUCCESS;

    private PatchMessages messages = new PatchMessages();

    public PatchMessages getMessages() {
        return messages;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if (this.status==Status.ERROR && status==Status.SUCCESS) {
            throw new IllegalStateException("Init status to SUCCESS after status was inited to ERROR");
        }
        this.status = status;
    }
}
