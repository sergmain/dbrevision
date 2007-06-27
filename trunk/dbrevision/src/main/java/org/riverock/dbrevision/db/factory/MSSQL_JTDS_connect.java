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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hsqldb.Trace;

import org.riverock.dbrevision.annotation.schema.db.*;
import org.riverock.dbrevision.db.DatabaseAdapter;
import org.riverock.dbrevision.db.DatabaseManager;
import org.riverock.dbrevision.exception.DbRevisionException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Microsoft database connect from sourceforge.net
 * $Author: serg_main $
 * <p/>
 * $Id: MSSQL_JTDS_connect.java 1141 2006-12-14 14:43:29Z serg_main $
 */
@SuppressWarnings({"UnusedAssignment"})
public class MSSQL_JTDS_connect extends DatabaseAdapter {
    private static Logger log = Logger.getLogger(MSSQL_JTDS_connect.class);

    public int getFamily() {
        return DatabaseManager.MSSQL_FAMALY;
    }

    public int getVersion() {
        return 7;
    }

    public int getSubVersion() {
        return 0;
    }

    public MSSQL_JTDS_connect(Connection conn) {
        super(conn);
    }

    public int getMaxLengthStringField() {
        return 4000;
    }

    public boolean getIsBatchUpdate() {
        return false;
    }

    public boolean getIsNeedUpdateBracket() {
        return false;
    }

    public boolean getIsByteArrayInUtf8() {
        return false;
    }

    public String getClobField(ResultSet rs, String nameField)
        throws SQLException {
        return getClobField(rs, nameField, 20000);
    }

    public byte[] getBlobField(ResultSet rs, String nameField, int maxLength) throws Exception {
        Blob blob = rs.getBlob(nameField);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int count;
        byte buffer[] = new byte[1024];

        InputStream inputStream = blob.getBinaryStream();
        while ((count = inputStream.read(buffer)) >= 0) {
            outputStream.write(buffer, 0, count);
            outputStream.flush();
        }
        outputStream.close();
        return outputStream.toByteArray();
    }

    public void createTable(DbTable table) {
        if (table == null || table.getFields().isEmpty() ) {
            return;
        }

        String sql = "create table \"" + table.getName() + "\"\n" +
            "(";

        boolean isFirst = true;

        for (DbField field : table.getFields()) {
            if (!isFirst)
                sql += ",";
            else
                isFirst = !isFirst;

            sql += "\n\"" + field.getName() + "\"";
            int javaType = field.getJavaType();
            switch (javaType) {

                case Types.BIT:
                    sql += " DECIMAL(1,0)";
                    break;

                case Types.TINYINT:
                    sql += " DECIMAL(4,0)";
                    break;

                case Types.BIGINT:
                    sql += " DECIMAL(38,0)";
                    break;

                case Types.NUMERIC:
                case Types.DECIMAL:
                    Integer digit = field.getDecimalDigit();
                    if (digit==null) digit=0;
                    sql += " DECIMAL(" + (field.getSize()==null || field.getSize() > 38 ? 38 : field.getSize()) + ',' + digit + ")";
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

                case Types.BLOB:
                    sql += " IMAGE"; // Image type not compatible with hibernated blob

                        break;
//                case Types.LONGVARCHAR:
//                    sql += " VARCHAR(10)";
//                    break;

//                case Types.LONGVARBINARY:
//                    sql += " LONGVARBINARY";
//                    break;

                default:
                    field.setJavaStringType("unknown field type field - " + field.getName() + " javaType - " + javaType);
                    System.out.println("unknown field type field - " + field.getName() + " javaType - " + javaType);
            }

            if (field.getDefaultValue() != null) {
                String val = field.getDefaultValue().trim();

                if (StringUtils.isNotBlank(val)) {
                    switch (javaType) {
                        case Types.CHAR:
                        case Types.VARCHAR:
                            val = "'" + val + "'";
                            break;
                        case Types.TIMESTAMP:
                        case Types.DATE:
                            if (DatabaseManager.checkDefaultTimestamp(val))
                                val = "CURRENT_TIMESTAMP";

                            break;
                        default:
                    }
                    sql += (" DEFAULT " + val);
                }
            }

            if (field.getNullable() == DatabaseMetaData.columnNoNulls) {
                sql += " NOT NULL ";
            }
        }
        if (table.getPrimaryKey() != null && table.getPrimaryKey().getColumns().size() != 0) {
            DbPrimaryKey pk = table.getPrimaryKey();

            String namePk = pk.getColumns().get(0).getPkName();

//            constraintDefinition:
//            [ CONSTRAINT name ]
//            UNIQUE ( column [,column...] ) |
//            PRIMARY KEY ( column [,column...] ) |

            sql += ",\nCONSTRAINT " + namePk + " PRIMARY KEY (\n";

            List<DbPrimaryKeyColumn> list = pk.getColumns();
            Collections.sort(list, DatabaseManager.pkComparator);

            isFirst = true;
            for (DbPrimaryKeyColumn column : list) {
                if (!isFirst)
                    sql += ',';
                else
                    isFirst = !isFirst;

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
            throw new DbRevisionException(e);
        }
        finally {
            DatabaseManager.close(st);
            st = null;
        }

    }

    public void createForeignKey(DbTable view) {
    }

    public void dropTable(DbTable table) {
        dropTable(table.getName());
    }

    public void dropTable(String nameTable) {
        if (nameTable == null)
            return;

        String sql = "drop table " + nameTable;

        Statement st = null;
        try {
            st = this.getConnection().createStatement();
            st.execute(sql);
            int count = st.getUpdateCount();
            if (log.isDebugEnabled()) {
                log.debug("count of deleted object " + count);
            }
        }
        catch (SQLException e) {
            log.error("Error drop table " + nameTable, e);
            throw new DbRevisionException(e);
        }
        finally {
            DatabaseManager.close(st);
            st = null;
        }
    }

    public void dropSequence(String nameSequence) {
    }

    public void dropConstraint(DbImportedPKColumn impPk) {
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
            DatabaseManager.close(ps);
            ps = null;
        }
    }

    public void addColumn(DbTable table, DbField field) {
        String sql = "alter table \"" + table.getName() + "\" add " + field.getName() + " ";

        int fieldType = field.getJavaType();
        switch (fieldType) {

            case Types.BIT:
                sql += " BIT";
                break;

            case Types.TINYINT:
                sql += " TINYINT";
                break;

            case Types.BIGINT:
                sql += " BIGINT";
                break;

                // Todo if number before point ==1 and number after point ==0
                // set type to bit
            case Types.NUMERIC:
            case Types.DECIMAL:
                sql += " DECIMAL";
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
                sql += " VARCHAR(10)";
                break;

            case Types.LONGVARBINARY:
                sql += " LONGVARBINARY";
                break;

            default:
                field.setJavaStringType("unknown field type field - " + field.getName() + " javaType - " + field.getJavaType());
                System.out.println("unknown field type field - " + field.getName() + " javaType - " + field.getJavaType());
        }

        if (field.getDefaultValue() != null) {
            String val = field.getDefaultValue().trim();

            switch (fieldType) {
                case Types.CHAR:
                case Types.VARCHAR:
                    if (!val.equalsIgnoreCase("null")) {
                        val = "'" + val + "'";
                    }
                    break;
                case Types.DATE:
                case Types.TIMESTAMP:
                    if (DatabaseManager.checkDefaultTimestamp(val)) {
                        val = getDefaultTimestampValue();
                    }
                    break;
            }
            sql += (" DEFAULT " + val);
        }

        if (field.getNullable() == DatabaseMetaData.columnNoNulls) {
            sql += " NOT NULL ";
        }

        if (log.isDebugEnabled()) {
            log.debug("MSSQL addColumn sql - \n" + sql);
        }

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
            DatabaseManager.close(ps);
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
            DatabaseManager.close(ps);
            ps = null;
        }
    }

    public void createSequence(DbSequence seq) {
    }

    public void setLongVarbinary(PreparedStatement ps, int index, DbDataFieldData fieldData)
        throws SQLException {
        ps.setNull(index, Types.VARCHAR);
    }

    public void setLongVarchar(PreparedStatement ps, int index, DbDataFieldData fieldData)
        throws SQLException {
        ps.setString(index, "");
    }

    public String getClobField(ResultSet rs, String nameField, int maxLength)
        throws SQLException {
        return null;
    }
/*
            CLOB clob = ((OracleResultSet) rs).getCLOB(nameField);

            if (clob == null)
                return null;

            return clob.getSubString(1, maxLength);
        }
*/

    /**
     * Возвращает значение сиквенса(последовательности) для данного имени последовательности.
     * Для разных коннектов к разным базам данных может быть решена по разному.
     *
     * @param sequence - String. Имя последовательноти для получения следующего значения.
     * @return long - следующее значение для ключа из последовательности
     * @throws SQLException
     */
    public long getSequenceNextValue(CustomSequence sequence)
        throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement("select max(" + sequence.getColumnName() + ") max_id from " + sequence.getTableName());

            rs = ps.executeQuery();

            if (rs.next())
                return rs.getLong(1) + 1;
        }
        finally {
            DatabaseManager.close(rs, ps);
            rs = null;
            ps = null;
        }

        return 1;
    }

    public boolean testExceptionTableNotFound(Exception e) {
        if (e instanceof SQLException) {
//        return ((SQLException) e).getErrorCode() == 208;
            return ((SQLException) e).getErrorCode() == -(Trace.TABLE_NOT_FOUND);
        }
        return false;
    }

    public boolean testExceptionIndexUniqueKey(Exception e, String index) {
        if (e instanceof SQLException) {
            if (((SQLException) e).getErrorCode() == -(Trace.VIOLATION_OF_UNIQUE_INDEX))
                return true;
        }
        return false;
    }

    public boolean testExceptionIndexUniqueKey(Exception e) {
        return false;
    }

    public boolean testExceptionTableExists(Exception e) {
        if (e instanceof SQLException) {
            if (((SQLException) e).getErrorCode() == 2714)
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