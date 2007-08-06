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
package org.riverock.dbrevision.db.factory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import org.riverock.dbrevision.annotation.schema.db.DbDataFieldData;
import org.riverock.dbrevision.annotation.schema.db.DbField;
import org.riverock.dbrevision.annotation.schema.db.DbForeignKey;
import org.riverock.dbrevision.annotation.schema.db.DbPrimaryKey;
import org.riverock.dbrevision.annotation.schema.db.DbSequence;
import org.riverock.dbrevision.annotation.schema.db.DbTable;
import org.riverock.dbrevision.annotation.schema.db.DbView;
import org.riverock.dbrevision.annotation.schema.db.DbPrimaryKeyColumn;
import org.riverock.dbrevision.db.Database;
import org.riverock.dbrevision.db.DatabaseManager;
import org.riverock.dbrevision.db.DbPkComparator;
import org.riverock.dbrevision.exception.DbRevisionException;
import org.riverock.dbrevision.utils.DbUtils;

/**
 * InterBase database connect 
 * $Author: serg_main $
 *
 * $Id: InterbaseDatabase.java 1141 2006-12-14 14:43:29Z serg_main $
 *
 */
@SuppressWarnings({"UnusedAssignment"})
public class InterbaseDatabase extends Database {
    private static Logger log = Logger.getLogger( InterbaseDatabase.class );

    /**
     * get family for this adapter
     * @return family
     */
    public Family getFamily() {
        return Family.INTERBASE;
    }

    public InterbaseDatabase(Connection conn) {
        super(conn);
    }

    public int getMaxLengthStringField() {
        return 4000;
    }

    public boolean isBatchUpdate() {
        return false;
    }

    public boolean isNeedUpdateBracket() {
        return false;
    }

    public boolean isByteArrayInUtf8() {
        return false;
    }

    public String getClobField(ResultSet rs, String nameField) {
        return getClobField(rs, nameField, 20000);
    }

    public byte[] getBlobField(ResultSet rs, String nameField, int maxLength) {
        return null;
    }

    public void createTable(DbTable table) {
        if (table == null || table.getFields().isEmpty() ) {
            return;
        }

        String sql = "create table " + table.getName() + "\n" +
            "(";

        boolean isFirst = true;

        for (DbField field : table.getFields()) {
            if (!isFirst)
                sql += ",";
            else
                isFirst = !isFirst;

            sql += "\n" + field.getName();
            switch (field.getJavaType()) {

                case Types.NUMERIC:
                case Types.DECIMAL:
                    if (field.getDecimalDigit()!=0) {
                        sql += " DECIMAL(" + (field.getSize()==null || field.getSize() > 38 ? 38 : field.getSize()) + ',' + field.getDecimalDigit() + ")";
                    }
                    else {
                        if (field.getSize() == 1)
                            sql += " SMALLINT";
                        else
                            sql += " DOUBLE PRECISION";
                    }
                    break;

                case Types.INTEGER:
                    sql += " INTEGER";
                    break;

                case Types.DOUBLE:
                    sql += " DOUBLE";
                    break;

                case Types.CHAR:
                    sql += " VARCHAR(1)";
                    break;

                case Types.VARCHAR:
                    sql += " VARCHAR(" + field.getSize() + ")";
                    break;

                case Types.TIMESTAMP:
                case Types.DATE:
                    sql += " DATETIME";
                    break;

                case Types.LONGVARCHAR:
                    // Oracle 'long' fields type
                    sql += " VARCHAR(10)";
                    break;

                case Types.LONGVARBINARY:
                    // Oracle 'long raw' fields type
                    sql += " LONGVARBINARY";
                    break;

                default:
                    field.setJavaStringType("unknown field type field - " + field.getName() + " javaType - " + field.getJavaType());
                    System.out.println("unknown field type field - " + field.getName() + " javaType - " + field.getJavaType());
            }

            if (field.getDefaultValue() != null) {
                String val = field.getDefaultValue().trim();

                // TODO rewrite. check only if type is 'date' 
                if (DatabaseManager.checkDefaultTimestamp(val)) {
                    val = "current_timestamp";
                }

                sql += (" DEFAULT " + val);
            }

            if (field.getNullable()==DatabaseMetaData.columnNoNulls) {
                sql += " NOT NULL ";
            }
        }
        if (table.getPrimaryKey() != null && table.getPrimaryKey().getColumns().size() != 0) {
            DbPrimaryKey pk = table.getPrimaryKey();

            //            constraintDefinition:
//            [ CONSTRAINT name ]
//            UNIQUE ( column [,column...] ) |
//            PRIMARY KEY ( column [,column...] ) |

            sql += ",\nCONSTRAINT " + pk.getPkName() + " PRIMARY KEY (\n";

            Collections.sort(pk.getColumns(), DbPkComparator.getInstance());
            isFirst = true;
            for (DbPrimaryKeyColumn column : pk.getColumns()) {
                if (!isFirst) {
                    sql += ",";
                }
                else {
                    isFirst = !isFirst;
                }

                sql += column.getColumnName();
            }
            sql += "\n)";
        }
        sql += "\n)";

        Statement st = null;
        try {
            st = this.getConnection().createStatement();
            st.execute(sql);
            int count = st.getUpdateCount();
            if (log.isDebugEnabled()) {
                log.debug("count of processed records " + count);
            }
        }
        catch (SQLException e) {
            log.error("SQL:\n"+sql);
            throw new DbRevisionException(e);
        }
        finally {
            DbUtils.close(st);
            st = null;
        }

    }

    public void createForeignKey(DbTable view) {
    }

    public void dropTable(DbTable table) {
        dropTable(table.getName());
    }

    public void dropTable(String nameTable) {
        if (nameTable == null) {
            return;
        }

        String sql = "drop table " + nameTable;

        Statement st = null;
        try {
            st = this.getConnection().createStatement();
            st.execute(sql);
            int count = st.getUpdateCount();
            if (log.isDebugEnabled())
                log.debug("count of deleted object " + count);
        }
        catch (SQLException e) {
            log.error("Error drop table " + nameTable, e);
            throw new DbRevisionException(e);
        }
        finally {
            DbUtils.close(st);
            st = null;
        }
    }

    public void dropSequence(String nameSequence) {
    }

    public void dropConstraint(DbForeignKey impPk) {
        if (impPk == null) {
            return;
        }

        String sql = "ALTER TABLE " + impPk.getPkTableName() + " DROP CONSTRAINT " + impPk.getPkName();

        PreparedStatement ps = null;
        try {
            ps = this.getConnection().prepareStatement(sql);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }
        finally {
            DbUtils.close(ps);
            ps = null;
        }
    }

    public void addColumn(DbTable table, DbField field) {
        String sql = "alter table " + table.getName() + " add " + field.getName() + " ";

        switch (field.getJavaType()) {

            case Types.NUMERIC:
            case Types.DECIMAL:
                sql += " DOUBLE PRECISION";
                break;

            case Types.INTEGER:
                sql += " INTEGER";
                break;

            case Types.DOUBLE:
                sql += " DOUBLE";
                break;

            case Types.CHAR:
                sql += " VARCHAR(1)";
                break;

            case Types.VARCHAR:
                sql += (" VARCHAR(" + field.getSize() + ") ");
                break;

            case Types.TIMESTAMP:
            case Types.DATE:
                sql += " DATETIME";
                break;

            case Types.LONGVARCHAR:
                // Oracle 'long' fields type
                sql += " VARCHAR(10)";
                break;

            case Types.LONGVARBINARY:
                // Oracle 'long raw' fields type
                sql += " LONGVARBINARY";
                break;

            default:
                field.setJavaStringType("unknown field type field - " + field.getName() + " javaType - " + field.getJavaType());
                System.out.println("unknown field type field - " + field.getName() + " javaType - " + field.getJavaType());
        }

        if (field.getDefaultValue() != null) {
            String val = field.getDefaultValue().trim();

            //TODO rewrite init of def as in createTable
//                if (!val.equalsIgnoreCase("null"))
//                    val = "'"+val+"'";
            if (DatabaseManager.checkDefaultTimestamp(val))
                val = "current_timestamp";

            sql += (" DEFAULT " + val);
        }

        if (field.getNullable() == DatabaseMetaData.columnNoNulls) {
            sql += " NOT NULL ";
        }

        if (log.isDebugEnabled())
            log.debug("Interbase addColumn sql - \n" + sql);

        Statement ps = null;
        try {
            ps = this.getConnection().createStatement();
            ps.executeUpdate(sql);
            this.getConnection().commit();
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }
        finally {
            DbUtils.close(ps);
            ps = null;
        }
    }

    public String getOnDeleteSetNull() {
        return "ON DELETE NO ACTION";
    }

    public String getDefaultTimestampValue() {
        return "current_timestamp";
    }

/*
ALTER TABLE table
{ [ ALTER COLUMN column_name
    { new_data_type [ ( precision [ , scale ] ) ]
        [ COLLATE < collation_name > ]
        [ NULL | NOT NULL ]
        | {ADD | DROP } ROWGUIDCOL }
    ]
    | ADD
        { [ < column_definition > ]
        |  column_name AS computed_column_expression
        } [ ,...n ]
    | [ WITH CHECK | WITH NOCHECK ] ADD
        { < table_constraint > } [ ,...n ]
    | DROP
        { [ CONSTRAINT ] constraint_name
            | COLUMN column } [ ,...n ]
    | { CHECK | NOCHECK } CONSTRAINT
        { ALL | constraint_name [ ,...n ] }
    | { ENABLE | DISABLE } TRIGGER
        { ALL | trigger_name [ ,...n ] }
}

< column_definition > ::=
    { column_name data_type }
    [ [ DEFAULT constant_expression ] [ WITH VALUES ]
    | [ IDENTITY [ (seed , increment ) [ NOT FOR REPLICATION ] ] ]
        ]
    [ ROWGUIDCOL ]
    [ COLLATE < collation_name > ]
    [ < column_constraint > ] [ ...n ]

< column_constraint > ::=
    [ CONSTRAINT constraint_name ]
    { [ NULL | NOT NULL ]
        | [ { PRIMARY KEY | UNIQUE }
            [ CLUSTERED | NONCLUSTERED ]
            [ WITH FILLFACTOR = fillfactor ]
            [ ON { filegroup | DEFAULT } ]
            ]
        | [ [ FOREIGN KEY ]
            REFERENCES ref_table [ ( ref_column ) ]
            [ ON DELETE { CASCADE | NO ACTION } ]
            [ ON UPDATE { CASCADE | NO ACTION } ]
            [ NOT FOR REPLICATION ]
            ]
        | CHECK [ NOT FOR REPLICATION ]
            ( logical_expression )
    }

< table_constraint > ::=
    [ CONSTRAINT constraint_name ]
    { [ { PRIMARY KEY | UNIQUE }
        [ CLUSTERED | NONCLUSTERED ]
        { ( column [ ,...n ] ) }
        [ WITH FILLFACTOR = fillfactor ]
        [ ON {filegroup | DEFAULT } ]
        ]
        |    FOREIGN KEY
            [ ( column [ ,...n ] ) ]
            REFERENCES ref_table [ ( ref_column [ ,...n ] ) ]
            [ ON DELETE { CASCADE | NO ACTION } ]
            [ ON UPDATE { CASCADE | NO ACTION } ]
            [ NOT FOR REPLICATION ]
        | DEFAULT constant_expression
            [ FOR column ] [ WITH VALUES ]
        |    CHECK [ NOT FOR REPLICATION ]
            ( search_conditions )
    }

*/

    public List<DbView> getViewList(String schemaPattern, String tablePattern) {
        return DatabaseManager.getViewList(getConnection(), schemaPattern, tablePattern);
    }

    public List<DbSequence> getSequnceList(String schemaPattern) {
        return new ArrayList<DbSequence>();
    }

    public String getViewText(DbView view) {
        return null;
    }

    public void createView(DbView view) {
        if (view == null ||
            view.getName() == null || view.getName().length() == 0 ||
            view.getText() == null || view.getText().length() == 0
        )
            return;

        String sql_ =
            "CREATE VIEW " + view.getName() +
            " AS " + StringUtils.replace(view.getText(), "||", "+");

        Statement ps = null;
        try {
            ps = this.getConnection().createStatement();
            ps.execute(sql_);
        }
        catch (SQLException e) {
            String errorString = "Error create view. Error code " + e.getErrorCode() + "\n" + sql_;
            log.error(errorString, e);
            throw new DbRevisionException(errorString, e);
        }
        finally {
            DbUtils.close(ps);
            ps = null;
        }
    }

    public void createSequence(DbSequence seq) {
    }

    public void setLongVarbinary(PreparedStatement ps, int index, DbDataFieldData fieldData) {
        try {
            ps.setNull(index, Types.VARCHAR);
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }
    }

    public void setLongVarchar(PreparedStatement ps, int index, DbDataFieldData fieldData) {
        try {
            ps.setString(index, "");
        }
        catch (SQLException e) {
            throw new DbRevisionException(e);
        }
    }

    public String getClobField(ResultSet rs, String nameField, int maxLength) {
        return null;
    }
/*
            CLOB clob = ((OracleResultSet) rs).getCLOB(nameField);

            if (clob == null)
                return null;

            return clob.getSubString(1, maxLength);
        }
*/

    public boolean testExceptionTableNotFound(Exception e) {
        if (((SQLException) e).getErrorCode() == 208)
            return true;
        return false;
    }

    public boolean testExceptionIndexUniqueKey(Exception e, String index) {
        if (e instanceof SQLException) {
            if (((SQLException) e).getErrorCode() == -(org.hsqldb.Trace.VIOLATION_OF_UNIQUE_INDEX))
                return true;
        }
/*
        if ((e instanceof SQLException) &&
                ((e.toString().indexOf("ORA-00001") != -1) &&
                (e.toString().indexOf(index) != -1)))

            return true;
*/
        return false;
    }

    public boolean testExceptionIndexUniqueKey(Exception e) {
        return false;
    }

    public boolean testExceptionTableExists(Exception e) {
        if (e instanceof SQLException) {
            if (((SQLException) e).getErrorCode() == 335544351)
                return true;
        }
        return false;
    }

    public boolean testExceptionViewExists(Exception e) {
        if (e instanceof SQLException) {
            if (((SQLException) e).getErrorCode() == 2714)
                return true;
        }
        return false;
    }

    public boolean testExceptionSequenceExists(Exception e) {
        return false;
    }

    public boolean testExceptionConstraintExists(Exception e) {
        if (e instanceof SQLException) {
            if (((SQLException) e).getErrorCode() == -(org.hsqldb.Trace.CONSTRAINT_ALREADY_EXISTS))
                return true;
        }
        return false;
    }
}