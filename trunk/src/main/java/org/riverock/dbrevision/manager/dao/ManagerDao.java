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

package org.riverock.dbrevision.manager.dao;

import org.riverock.dbrevision.manager.RevisionBean;
import org.riverock.dbrevision.db.Database;

import java.util.List;

/**
 * User: SergeMaslyukov
 * Date: 29.07.2007
 * Time: 13:12:11
 */
public interface ManagerDao {
    List<RevisionBean> getRevisions(Database adapter);

    RevisionBean getRevision(Database database, String moduleName, String versionName);

    void checkDbRevisionTableExist(Database database);

    void markCurrentVersion(Database database, String moduleName, String versionName, String patchName);

    RevisionBean getRevision(Database database, String moduleName);
}
