/*
 * org.riverock.dbrevision - Database revision engine
 * For more information about DbRevision, please visit project site
 * http://www.riverock.org
 *
 * Copyright (C) 2006-2006, Riverock Software, All Rights Reserved.
 *
 * Riverock - The Open-source Java Development Community
 * http://www.riverock.org
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.riverock.dbrevision.system;

import java.io.InputStream;

import org.apache.log4j.Logger;

import org.riverock.dbrevision.annotation.schema.db.DbSchema;
import org.riverock.dbrevision.annotation.schema.db.DbSequence;
import org.riverock.dbrevision.annotation.schema.db.DbTable;
import org.riverock.dbrevision.annotation.schema.db.DbView;
import org.riverock.dbrevision.db.DatabaseAdapter;
import org.riverock.dbrevision.db.DatabaseManager;
import org.riverock.dbrevision.db.DatabaseStructureManager;
import org.riverock.dbrevision.utils.Utils;
import org.riverock.dbrevision.exception.DbRevisionException;

/**
 * import data from input stream to DB
 * 
 * Author: mill
 * Date: Nov 28, 2002
 * Time: 3:10:19 PM
 * <p/>
 * $Id: DbStructureImport.java 1141 2006-12-14 14:43:29Z serg_main $
 * <p/>
 */
@SuppressWarnings({"UnusedAssignment"})
public class DbStructureImport {
    private static Logger log = Logger.getLogger(DbStructureImport.class);

    public static void importStructure(DatabaseAdapter adapter, InputStream stream, boolean isData ) {
        log.debug("Unmarshal data from inputstream");
        DbSchema millSchema;
        try {
            millSchema = Utils.getObjectFromXml(DbSchema.class, stream);
        }
        catch (Exception e) {
            String es = "Error unmarshal DB structure from input stream";
            log.error(es, e);
            throw new DbRevisionException(es, e);
        }
        importStructure(millSchema, adapter, isData);
    }

    public static void importStructure(DbSchema millSchema, DatabaseAdapter db_, boolean isData) {
        for (DbTable table : millSchema.getTables()) {
            if (table.getName().toLowerCase().startsWith("tb_"))
                continue;

            if (!DatabaseManager.isSkipTable(table.getName())) {
                try {
                    log.debug("create table " + table.getName());
                    db_.createTable(table);
                }
                catch (Exception e) {
                    String es = "Error create table ";
                    log.debug(es + table.getName(), e);
                    throw new DbRevisionException(es, e);
                }
                if (isData) {
                    DatabaseStructureManager.setDataTable(db_, table);
                }
            }
            else {
                log.debug("skip table " + table.getName());
            }

        }

        for (DbView view : millSchema.getViews()) {
            DatabaseManager.createWithReplaceAllView(db_, millSchema);
            try {
                log.debug("create view " + view.getName());
                db_.createView(view);
            }
            catch (Exception e) {
                if (db_.testExceptionViewExists(e)) {
                    log.debug("view " + view.getName() + " already exists");
                    log.debug("drop view " + view.getName());
                    DatabaseStructureManager.dropView(db_, view);
                    log.debug("create view " + view.getName());
                    try {
                        db_.createView(view);
                    }
                    catch (Exception e1) {
                        String es = "Error create view - ";
                        log.error(es, e1);
                        throw new DbRevisionException(es, e1);
                    }
                }
                else {
                    String es = "Error create view";
                    log.debug(es,e);
                    throw new DbRevisionException(es, e);
                }
            }
        }
        DatabaseManager.createWithReplaceAllView(db_, millSchema);

        for (DbSequence seq : millSchema.getSequences()) {
            try {
                log.debug("create sequence " + seq.getName());
                db_.createSequence(seq);
            }
            catch (Exception e) {
                String es = "Error create sequence ";
                log.debug(es + seq.getName(), e);
                throw new DbRevisionException(es, e);
            }
        }
    }
}
