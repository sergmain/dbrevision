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

import java.io.OutputStream;

import org.riverock.dbrevision.schema.db.v3.DbSchema;
import org.riverock.dbrevision.schema.db.v3.DbTable;
import org.riverock.dbrevision.db.Database;
import org.riverock.dbrevision.db.DatabaseManager;
import org.riverock.dbrevision.db.DatabaseStructureManager;
import org.riverock.dbrevision.exception.DbRevisionException;
import org.riverock.dbrevision.utils.Utils;

/**
 * Export data from DB to XML file
 * <p/>
 * Author: mill
 * Date: Nov 28, 2002
 * Time: 3:10:19 PM
 * <p/>
 * $Id: DbStructureExport.java 1141 2006-12-14 14:43:29Z serg_main $
 */
public class DbStructureExport {

    public static void export(Database database, final String schema, OutputStream outputStream, boolean isData) {
        export(database, schema, outputStream, isData, "Schema", "utf-8");
    }

    /**
     *
     * @param database
     * @param outputStream
     * @param isData
     * @param xmlRootElement
     * @param encoding
     */
    public static void export(
        final Database database,
        final String schema,
        final OutputStream outputStream,
        boolean isData,
        String xmlRootElement,
        String encoding) {

        try {
            DbSchema dbSchema = DatabaseManager.getDbStructure(database, schema);
            if (isData) {
                for (DbTable table : dbSchema.getTables()) {
                    table.setD(DatabaseStructureManager.getDataTable(database, table));
                }
            }

            Utils.writeMarshalToOutputStream(dbSchema, encoding, xmlRootElement, outputStream, true, null);
        } catch (Exception e) {
            throw new DbRevisionException(e);
        }
    }
}
