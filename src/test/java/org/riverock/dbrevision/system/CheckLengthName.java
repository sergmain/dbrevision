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
package org.riverock.dbrevision.system;

import java.io.FileInputStream;

import org.riverock.dbrevision.schema.db.v3.DbSchema;
import org.riverock.dbrevision.schema.db.v3.DbTable;
import org.riverock.dbrevision.schema.db.v3.DbView;
import org.riverock.dbrevision.utils.Utils;

/**
 * Author: mill
 * Date: Nov 28, 2002
 * Time: 3:10:19 PM
 *
 * $Id: CheckLengthName.java 1141 2006-12-14 14:43:29Z serg_main $
 */
public class CheckLengthName {

    public CheckLengthName() {
    }

    public static void main(String args[]) throws Exception {

        System.out.println("Unmarshal data from file");
        FileInputStream stream = new FileInputStream("webmill-schema.xml");
        DbSchema millSchema = Utils.getObjectFromXml(DbSchema.class, stream);

        for (DbTable table : millSchema.getTables()) {
            if (table.getT().length() > 18)
                System.out.println("Name of table '" + table.getT() +
                    "' is wrong. Exceed " + (table.getT().length() - 18) + " of chars");
        }

        for (DbView view : millSchema.getViews()) {
            if (view.getT().length() > 18)
                System.out.println("Name of view '" + view.getT() +
                    "' is wrong. Exceed " + (view.getT().length() - 18) + " of chars");
        }
    }
}
