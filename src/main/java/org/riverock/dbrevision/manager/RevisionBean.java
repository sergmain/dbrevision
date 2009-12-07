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

package org.riverock.dbrevision.manager;

/**
 * User: SergeMaslyukov
 * Date: 29.07.2007
 * Time: 13:11:08
 */
public class RevisionBean {
    private String moduleName;

    private String currentVerson;

    private String lastPatch;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getCurrentVerson() {
        return currentVerson;
    }

    public void setCurrentVerson(String currentVerson) {
        this.currentVerson = currentVerson;
    }

    public String getLastPatch() {
        return lastPatch;
    }

    public void setLastPatch(String lastPatch) {
        this.lastPatch = lastPatch;
    }
}
