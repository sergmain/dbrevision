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

import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.sql.Statement;
import java.sql.SQLException;



import org.riverock.dbrevision.schema.db.DbSchema;
import org.riverock.dbrevision.schema.db.DbSequence;
import org.riverock.dbrevision.schema.db.DbTable;
import org.riverock.dbrevision.schema.db.DbView;
import org.riverock.dbrevision.schema.db.DbViewReplacement;
import org.riverock.dbrevision.schema.db.DbSequenceReplacement;
import org.riverock.dbrevision.db.Database;
import org.riverock.dbrevision.db.DatabaseManager;
import org.riverock.dbrevision.db.DatabaseStructureManager;
import org.riverock.dbrevision.utils.Utils;
import org.riverock.dbrevision.utils.DbUtils;
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

    public static void importStructure(Database database, InputStream stream, boolean isData ) {
        importStructure(database, stream, null, isData);
    }

    public static void importStructure(Database database, InputStream stream, InputStream replacementSchemaStream, boolean isData ) {
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
            throw new DbRevisionException(es, e);
        }
        importStructure(database, schema, replacementSchema, isData);
    }

    public static void importStructure(Database database, DbSchema millSchema, boolean isData) {
        importStructure(database, millSchema, null, isData);
    }

    public static void importStructure(Database database, DbSchema dbSchema, DbSchema replacementSchema, boolean isData) {
        for (DbTable table : dbSchema.getTables()) {

            if (!DatabaseManager.isSkipTable(table.getT())) {
                try {
                    database.createTable(table);
                }
                catch (Exception e) {
                    String es = "Error create table";
                    throw new DbRevisionException(es, e);
                }
                if (isData) {
                    DatabaseStructureManager.setDataTable(database, table);
                    try {
                        database.getConnection().commit();
                    }
                    catch (SQLException e) {
                        String es = "Error store date";
                        throw new DbRevisionException(es, e);
                    }
                }
            }
        }

        List<DbViewReplacement> dbViewReplacements = new ArrayList<DbViewReplacement>();
        if (dbSchema.getViewReplace()!=null) {
            dbViewReplacements.addAll(dbSchema.getViewReplace());
        }
        if (replacementSchema!=null && replacementSchema.getViewReplace()!=null) {
            dbViewReplacements.addAll(replacementSchema.getViewReplace());
        }

        fullCreateViews(database, dbSchema.getViews(), dbViewReplacements);

        createSequences(database, dbSchema, replacementSchema);
        processSequencesReplacement(database, dbSchema, replacementSchema);
    }

    private static void processSequencesReplacement(Database database, DbSchema dbSchema, DbSchema replacementSchema) {
        if (replacementSchema==null || replacementSchema.getSequenceReplacement()==null ) {
            return;
        }
        DbSequenceReplacement r = replacementSchema.getSequenceReplacement();
        DbTable table = r.getTableWithIds();
        database.createTable(table);
        for (DbSequence seq : dbSchema.getSequences()) {
            String sql_ =
                "insert into " + table.getT() +
                    "(" + r.getSequenceColumnName() + ','+r.getValueColumnName()  + ")" +
                    "value " +
                    "('"+seq.getName()+"', "+seq.getLastNumber()+")";

            Statement ps = null;
            try {
                ps = database.getConnection().createStatement();
                ps.execute(sql_);
                database.getConnection().commit();
            }
            catch (SQLException e) {
                throw new DbRevisionException("sql:\n"+sql_, e);
            }
            finally {
                DbUtils.close(ps);
                ps = null;
            }
        }
    }

    private static void createSequences(Database database, DbSchema millSchema, DbSchema replacementSchema) {
        if (replacementSchema!=null &&
            replacementSchema.getSequenceReplacement()!=null &&
            Boolean.TRUE.equals(replacementSchema.getSequenceReplacement().isIsSkipAll())
            ) {

            return;
        }
        for (DbSequence seq : millSchema.getSequences()) {
            database.createSequence(seq);
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
                    throw new DbRevisionException(es, e1);
                }
            }
            catch (Exception e) {
                String es = "Error create view";
                throw new DbRevisionException(es, e);
            }
        }
    }

}
