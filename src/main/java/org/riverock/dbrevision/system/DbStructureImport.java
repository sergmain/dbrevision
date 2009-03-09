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
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import org.riverock.dbrevision.annotation.schema.db.DbSchema;
import org.riverock.dbrevision.annotation.schema.db.DbSequence;
import org.riverock.dbrevision.annotation.schema.db.DbTable;
import org.riverock.dbrevision.annotation.schema.db.DbView;
import org.riverock.dbrevision.annotation.schema.db.DbViewReplacement;
import org.riverock.dbrevision.db.Database;
import org.riverock.dbrevision.db.DatabaseManager;
import org.riverock.dbrevision.db.DatabaseStructureManager;
import org.riverock.dbrevision.utils.Utils;
import org.riverock.dbrevision.exception.DbRevisionException;
import org.riverock.dbrevision.exception.ViewAlreadyExistException;

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

    public static void importStructure(Database database, InputStream stream, boolean isData ) {
        importStructure(database, stream, null, isData);
    }

    public static void importStructure(Database database, InputStream stream, InputStream replacementSchemaStream, boolean isData ) {
        log.debug("Unmarshal data from inputstream");
        DbSchema schema;
        DbSchema replacementSchema=null;
        try {
            schema = Utils.getObjectFromXml(DbSchema.class, stream);
            if (replacementSchemaStream!=null) {
                replacementSchema = Utils.getObjectFromXml(DbSchema.class, replacementSchemaStream);
            }
        }
        catch (Exception e) {
            String es = "Error unmarshal DB structure from input stream";
            log.error(es, e);
            throw new DbRevisionException(es, e);
        }
        importStructure(database, schema, replacementSchema, isData);
    }

    public static void importStructure(Database database, DbSchema millSchema, boolean isData) {
        importStructure(database, millSchema, null, isData);
    }

    public static void importStructure(Database database, DbSchema millSchema, DbSchema replacementSchema, boolean isData) {
        for (DbTable table : millSchema.getTables()) {

            if (!DatabaseManager.isSkipTable(table.getName())) {
                try {
                    log.debug("create table " + table.getName());
                    database.createTable(table);
                }
                catch (Exception e) {
                    String es = "Error create table ";
                    log.debug(es + table.getName(), e);
                    throw new DbRevisionException(es, e);
                }
                if (isData) {
                    DatabaseStructureManager.setDataTable(database, table);
                }
            }
            else {
                log.debug("skip table " + table.getName());
            }

        }

        List<DbViewReplacement> dbViewReplacements = new ArrayList<DbViewReplacement>();
        if (millSchema.getViewReplacement()!=null) {
            dbViewReplacements.addAll(millSchema.getViewReplacement());
        }
        if (replacementSchema!=null && replacementSchema.getViewReplacement()!=null) {
            dbViewReplacements.addAll(replacementSchema.getViewReplacement());
        }

        fullCreateViews(database, millSchema.getViews(), dbViewReplacements);

        for (DbSequence seq : millSchema.getSequences()) {
            try {
                log.debug("create sequence " + seq.getName());
                database.createSequence(seq);
            }
            catch (Exception e) {
                String es = "Error create sequence ";
                log.debug(es + seq.getName(), e);
                throw new DbRevisionException(es, e);
            }
        }
    }

    public static void fullCreateViews(Database database, List<DbView> views) {
        fullCreateViews(database, views, null);
    }

    public static void fullCreateViews(Database database, List<DbView> views, List<DbViewReplacement> replacementViews) {
        DatabaseManager.createWithReplaceAllView(database, views, replacementViews);

        for (DbView view : views) {
            DbViewReplacement viewReplacement = DatabaseManager.getViewReplcament(database, view, replacementViews);
            try {
                if (viewReplacement!=null) {
                    if (Boolean.TRUE.equals(viewReplacement.isSkip())) {
                        continue;
                    }
                    view = viewReplacement.getView();
                }
                
                database.createView(view);
            }
            catch(ViewAlreadyExistException e) {
                DatabaseStructureManager.dropView(database, view);
                try {
                    database.createView(view);
                }
                catch (Exception e1) {
                    String es = "Error create view - ";
                    log.error(es, e1);
                    throw new DbRevisionException(es, e1);
                }
            }
            catch (Exception e) {
                String es = "Error create view";
                log.debug(es,e);
                throw new DbRevisionException(es, e);
            }
        }
    }

}
