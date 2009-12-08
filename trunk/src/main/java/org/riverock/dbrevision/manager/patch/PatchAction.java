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

import org.riverock.dbrevision.db.Database;

/**
 * User: Admin
 * Date: May 20, 2003
 * Time: 10:02:30 PM
 * <p/>
 * $Id: PatchAction.java 1075 2006-11-24 18:08:42Z serg_main $
 */
public interface PatchAction {
    public PatchStatus process(Database adapter) throws Exception;
}
