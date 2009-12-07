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
