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

import org.riverock.dbrevision.annotation.schema.db.DbSchema;
import org.riverock.dbrevision.annotation.schema.db.DbTable;
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

    public static void export(Database adapter, OutputStream outputStream, boolean isData) {
        export(adapter, outputStream, isData, true);
    }

    public static void export(Database adapter, OutputStream outputStream, boolean isData, boolean isOnlyCurrent) {
        export(adapter, outputStream, isData, isOnlyCurrent, "SchemaElement", "utf-8");
    }

    /**
     *
     * @param adapter
     * @param outputStream
     * @param isData
     * @param isOnlyCurrent if true - only objects in current db schema 
     * @param xmlRootElement
     * @param encoding
     */
    public static void export(
        Database adapter,
        OutputStream outputStream,
        boolean isData,
        boolean isOnlyCurrent,
        String xmlRootElement,
        String encoding) {

        try {
            DbSchema schema = DatabaseManager.getDbStructure(adapter, isOnlyCurrent);
            if (isData) {
                for (DbTable table : schema.getTables()) {
                    table.setData(DatabaseStructureManager.getDataTable(adapter, table));
                }
            }
            Utils.writeObjectAsXml(schema, outputStream, xmlRootElement, encoding);
        } catch (Exception e) {
            throw new DbRevisionException(e);
        }
    }
}
