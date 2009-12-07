/*
 * Copyright 2007 Sergei Maslyukov at riverock.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
