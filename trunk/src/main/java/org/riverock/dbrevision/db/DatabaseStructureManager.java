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
package org.riverock.dbrevision.db;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.codec.binary.Base64;


import org.riverock.dbrevision.exception.DbRevisionException;
import org.riverock.dbrevision.utils.DbUtils;

/**
 * @author SergeMaslyukov
 *         Date: 20.12.2005
 *         Time: 1:07:54
 *         $Id: DatabaseStructureManager.java 1141 2006-12-14 14:43:29Z serg_main $
 */
@SuppressWarnings({"UnusedAssignment"})
public class DatabaseStructureManager {
    private static final int MAX_LENGTH_BLOB = 1000000;
    private static final String CURRENT = "CURRENT";
    private static final String TIMESTAMP = "TIMESTAMP";


    /**
     * @deprecated use ConstraintManager.createForeignKey(adapter,  fk)
     * create foreign key
     *
     * @param adapter db adapter
     * @param fk list of foreign keys
     */
    public static void createForeignKey(Database adapter, DbForeignKey fk) {
        ConstraintManager.createFk(adapter,  fk);
    }

    /**
     * add column to table
     *
     * @param adapter db adapter
     * @param tableName table name
     * @param field column descriptor
     */
    public static void addColumn(Database adapter, String tableName, DbField field) {
        DbTable table = new DbTable();
        table.setT(tableName);
        adapter.addColumn(table, field);
    }

    /**
     * drop field from specified table
     *
     * @param adapter db adapter
     * @param table table definition
     * @param field field to drop
     */
    public static void dropColumn(Database adapter, DbTable table, DbField field) {
        if (table == null ||
            table.getT() == null || table.getT().length() == 0
            )
            return;

        if (field == null ||
            field.getName() == null || field.getName().length() == 0
            )
            return;

        String sql_ = "ALTER TABLE " + table.getT() + " DROP COLUMN " + field.getName();
        PreparedStatement ps = null;
        try {
            ps = adapter.getConnection().prepareStatement(sql_);
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

    public static void dropView(Database adapter, DbView view) {
        if (view == null ||
            view.getT() == null || view.getT().length() == 0
            )
            return;

        String sql_ = "drop VIEW " + view.getT();
        PreparedStatement ps = null;
        try {
            ps = adapter.getConnection().prepareStatement(sql_);
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

    public static void setDataTable(Database adapter, DbTable table) {
        if (table == null || table.getD() == null || table.getD().getRecords().isEmpty()) {
            return;
        }

        if (table.getFields().isEmpty()) {
            throw new DbRevisionException("Table has zero count of fields");
        }

        boolean isDebug = false;
        String sql_ = "insert into " + table.getT() + "(";

        boolean isFirst = true;
        for (DbField field : table.getFields()) {
            if (isFirst) {
                isFirst = false;
            }
            else {
                sql_ += ", ";
            }

            sql_ += field.getName();
        }
        sql_ += ")values(";

        isFirst = true;
        for (int i=0; i<table.getFields().size(); i++) {
            if (isFirst)
                isFirst = false;
            else
                sql_ += ", ";

            sql_ += '?';
        }
        sql_ += ")";

        DbDataTable tableData = table.getD();

        for (DbDataRecord record : tableData.getRecords()) {
            PreparedStatement ps = null;
            ResultSet rs = null;
            DbField field=null;
            try {
                ps = adapter.getConnection().prepareStatement(sql_);

                int fieldPtr = 0;
                int k=0;
                for (DbDataFieldData fieldData : record.getF()) {
                    field = table.getFields().get(fieldPtr++);

                    if (fieldData.isNll()) {
                        int type = table.getFields().get(k).getType();
//                        if (type == Types.TIMESTAMP) {
//                            type = Types.DATE;
//                        }

                        ps.setNull(k + 1, type);
                    }
                    else {
                        if (isDebug) {
                            System.out.println("param #" + (k + 1) + ", type " + table.getFields().get(k).getType());
                        }

                        switch (table.getFields().get(k).getType()) {
                            case Types.BIT:
                            case Types.TINYINT:
                            case Types.BIGINT:

                            case Types.DECIMAL:
                            case Types.DOUBLE:
                            case Types.NUMERIC:
                                if (field.getDigit() == null || field.getDigit() == 0) {
                                    if (isDebug) {
                                        System.out.println("Types.NUMERIC as Types.INTEGER param #" + (k + 1) + ", " +
                                            "value " + fieldData.getN().doubleValue() + ", long value " + ((long) fieldData.getN().doubleValue() +
                                            ", extracted value: " + fieldData.getN().longValueExact())
                                        );
                                    }
                                    ps.setBigDecimal(k + 1, fieldData.getN());
                                }
                                else {
                                    if (isDebug) {
                                        System.out.println("Types.NUMERIC param #" + (k + 1) + ", value " + fieldData.getN().doubleValue());
                                    }
                                    ps.setBigDecimal(k + 1, fieldData.getN());
                                }
                                break;

                            case Types.INTEGER:
                                if (isDebug) {
                                    System.out.println("Types.INTEGER param #" + (k + 1) + ", value " + fieldData.getN().doubleValue());
                                }
                                ps.setBigDecimal(k + 1, fieldData.getN());
                                break;

                            case Types.CHAR:
                                if (isDebug) {
                                    System.out.println("param #" + (k + 1) + ", value " + fieldData.getS().substring(0, 1));
                                }
                                ps.setString(k + 1, fieldData.getS().substring(0, 1));
                                break;

                            case Types.VARCHAR:
                                if (isDebug) {
                                    System.out.println("param #" + (k + 1) + ", value " + fieldData.getS());
                                }
                                ps.setString(k + 1, fieldData.getS());
                                break;

                            case Types.DATE:
                            case Types.TIMESTAMP:
                                long timeMillis = fieldData.getD().toGregorianCalendar().getTimeInMillis();
                                Timestamp stamp = new Timestamp(timeMillis);
                                if (isDebug) {
                                    System.out.println("param #" + (k + 1) + ", value " + stamp);
                                }
                                ps.setTimestamp(k + 1, stamp);
                                break;

                            case Types.LONGVARCHAR:
                                adapter.setLongVarchar(ps, k + 1, fieldData);
                                break;

                            case Types.LONGVARBINARY:
                                adapter.setLongVarbinary(ps, k + 1, fieldData);
                                break;

                            case Types.BLOB:
                                if (adapter.getFamily()== Database.Family.MYSQL) {
                                    byte[] bytes = Base64.decodeBase64(fieldData.getS().getBytes());

                                    byte[] fileBytes = new byte[]{};
                                    if (bytes!=null) {
                                        fileBytes = bytes;
                                    }
                                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileBytes);
                                    ps.setBinaryStream(k + 1, byteArrayInputStream, fileBytes.length);
                                        
                                    bytes = null;
                                    byteArrayInputStream = null;
                                    fileBytes = null;
                                }

                                break;

                            case 1111:
                                if (isDebug) {
                                    System.out.println("param #" + (k + 1) + ", value " + fieldData.getS());
                                }
                                ps.setString(k + 1, "");
                                break;
                            default:
                                System.out.println("Unknown field type.");
                        }
                    }
                    k++;
                }
                ps.executeUpdate();
            }
            catch (Exception e) {
                String es = "Error get data for table " + table.getT();
                int k=0;
                for (DbDataFieldData data : record.getF()) {
                    es += (", date: " + data.getD());
                    es += (", is null: " + data.isNll());
                    es += (", java type: " + table.getFields().get(k).getType());
                    es += (", number: " + data.getN());
                    es += (", string: " + data.getS());
                    k++;
                }
                throw new DbRevisionException(es, e);
            }
            finally {
                DbUtils.close(rs, ps);
                rs = null;
                ps = null;
            }
        }
    }

    /**
     * @param adapter db adapter
     * @param table table for get data
     * @return DbDataTable
     */
    public static DbDataTable getDataTable(Database adapter, DbTable table) {
        if (table.getS()==null && table.getT()==null) {
            throw new IllegalArgumentException("schema: " + table.getS()+", table: " + table.getT());
        }
        DbDataTable tableData = new DbDataTable();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql_ = "select * from " + table.getS()+'.'+table.getT();

            ps = adapter.getConnection().prepareStatement(sql_);

            rs = ps.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();

            if (table.getFields().size() != meta.getColumnCount()) {
                String es = "Count for field in ResultSet not equals in DbTable. May be you forgot initialize DbFields. ";

                es += (", table " + table.getT());
                es += (", count field " + table.getFields().size());
                es += (", meta count field " + meta.getColumnCount());
                for (DbField field : table.getFields()) {
                    es += (", field " + field.getName());
                }

                throw new DbRevisionException(es);
            }

            byte[] bytes=null;

            while (rs.next()) {
                DbDataRecord record = new DbDataRecord();
                for (DbField field : table.getFields()) {
                    DbDataFieldData fieldData = new DbDataFieldData();

                    switch (field.getType()) {

                        case Types.BIT:
                        case Types.TINYINT:
                        case Types.BIGINT:
                        case Types.SMALLINT:

                        case Types.DECIMAL:
                        case Types.INTEGER:
                        case Types.DOUBLE:
                        case Types.FLOAT:
                        case Types.NUMERIC:
                            fieldData.setN(rs.getBigDecimal(field.getName()));
                            fieldData.setNll(rs.wasNull());
                            break;

                        case Types.CHAR:
                        case Types.VARCHAR:
                            fieldData.setS(rs.getString(field.getName()));
                            fieldData.setNll(rs.wasNull());
                            break;

                        case Types.DATE:
                        case Types.TIMESTAMP:
                            Timestamp timestamp = rs.getTimestamp(field.getName());
                            if (rs.wasNull()) {
                                fieldData.setNll(true);
                            }
                            else {
                                GregorianCalendar calendar = new GregorianCalendar();
                                calendar.setTimeInMillis(timestamp.getTime());
                                fieldData.setD(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
                                fieldData.setNll(false);
                            }
                            break;

                        case Types.LONGVARCHAR:
                            fieldData.setS(rs.getString(field.getName()));
                            fieldData.setNll(rs.wasNull());
                            break;
                            
                        case Types.LONGVARBINARY:
                            switch(adapter.getFamily()) {
                                case MYSQL:
                                    bytes = adapter.getBlobField(rs, field.getName(), MAX_LENGTH_BLOB);
                                    if (bytes!=null) {
                                        byte[] encodedBytes = Base64.encodeBase64(bytes);
                                        fieldData.setS( new String(encodedBytes) );
                                    }
                                    fieldData.setNll(rs.wasNull());
                                    bytes = null;
                                    break;
                                default:
                                    fieldData.setS(rs.getString(field.getName()));
                                    fieldData.setNll(rs.wasNull());
                            }
                            break;
                        case Types.BLOB:
                            switch (adapter.getFamily()) {
                                case ORACLE:
                                    bytes = adapter.getBlobField(rs, field.getName(), MAX_LENGTH_BLOB);
                                    fieldData.setNll(rs.wasNull());
                                    break;
                                case MYSQL:
                                    bytes = adapter.getBlobField(rs, field.getName(), MAX_LENGTH_BLOB);
                                    fieldData.setNll(rs.wasNull());
                                    break;

                                case DB2:
                                    break;
                                case HYPERSONIC:
                                    break;
                                case INTERBASE:
                                    break;
                                case SQLSERVER:
                                    break;
                                case MAXDB:
                                    break;
                            }
                            if (bytes!=null) {
                                byte[] encodedBytes = Base64.encodeBase64(bytes);
                                fieldData.setS( new String(encodedBytes) );
                            }
                            bytes = null;
                            break;
                        default:
                            final String es = "unknown field type field - " + field.getName() + " javaType - " + field.getType();
                            throw new IllegalStateException(es);
                    }
                    record.getF().add(fieldData);
                }
                tableData.getRecords().add(record);
            }
            return tableData;
        }
        catch (Exception e) {
            String es = "Error get data for table " + table.getT();
            throw new DbRevisionException(es, e);
        }
        finally {
            DbUtils.close(rs, ps);
            rs = null;
            ps = null;
        }
    }

    /**
     * @deprecated NOT SUPPORTED ANY MORE.
     * Use public static List<DbTable> ViewManager.getTableList(Database database, String schemaPattern, String tablePattern);
     *
     * @param conn1
     * @param schemaPattern
     * @param tablePattern
     * @return
     */
    public static List<DbTable> getTableList(Connection conn1, String schemaPattern, String tablePattern) {
        throw new RuntimeException("NOT SUPPORTED ANY MORE. See JavaDoc");
    }

    /**
     * Return filtered list of tables
     * usually schemaPattern is a db username
     * if tablePattern equals "%", this mean what selected all tables
     *
     * @param database db connection
     * @param schemaPattern schema name filter 
     * @param tablePattern table name filter
     * @return List of DbTable
     */
    public static List<DbTable> getTableList(Database database, String schemaPattern, String tablePattern) {
        String[] types = {"TABLE"};

        ResultSet meta = null;
        List<DbTable> v = new ArrayList<DbTable>();
        try {
            DatabaseMetaData db = database.getConnection().getMetaData();

            meta = db.getTables(null, schemaPattern, tablePattern, types );

            while (meta.next()) {
                DbTable table = new DbTable();

                table.setS(meta.getString("TABLE_SCHEM"));
                table.setT(meta.getString("TABLE_NAME"));
                table.setR(meta.getString("REMARKS"));

                v.add(table);
            }
        }
        catch (Exception e) {
            final String es = "Error get list of view";
            throw new DbRevisionException(es, e);
        }
        return v;
    }

    /**
     * @param adapter db adapter
     * @param schemaPattern String
     * @param tablePattern String
     * @return ArrayList
     */
    public static List<DbField> getFieldsList(Database adapter, String schemaPattern, String tablePattern) {
        List<DbField> v = new ArrayList<DbField>();
        DatabaseMetaData db = null;
        ResultSet metaField = null;
        try {
            db = adapter.getConnection().getMetaData();
            metaField = db.getColumns(null, schemaPattern, tablePattern, null);
            while (metaField.next()) {
                DbField field = new DbField();

                field.setName(metaField.getString("COLUMN_NAME"));
                field.setDbtype(metaField.getString("TYPE_NAME"));
                field.setType(DbUtils.getInteger(metaField, "DATA_TYPE", Integer.MIN_VALUE));
                field.setSize(DbUtils.getInteger(metaField, "COLUMN_SIZE"));
                field.setDigit(DbUtils.getInteger(metaField, "DECIMAL_DIGITS"));
                field.setNullable(DbUtils.getInteger(metaField, "NULLABLE"));
                String defValue = metaField.getString("COLUMN_DEF");

                field.setDef(defValue == null ? null : defValue.trim());

                if (field.getDef()!=null) {
                    // fix issue with null and other default values for concrete of DB
                    switch (adapter.getFamily()) {
                        case MYSQL:
                            if (field.getType()==Types.TIMESTAMP && field.getDef().equals("0000-00-00 00:00:00")) {
                                field.setDef(null);
                            }
                            break;
                        case DB2:
                            // check for IBM DB2 CURRENT TIMESTAMP
                            if (field.getDef().toUpperCase().startsWith(CURRENT)) {
                                String s1 = field.getDef().substring(CURRENT.length()).trim();
                                if (s1.equalsIgnoreCase(TIMESTAMP)) {
                                    field.setDef("current_timestamp");
                                }
                            }

                            break;
                        case HYPERSONIC:
                            break;
                        case INTERBASE:
                            break;
                        case MAXDB:
                            break;
                        case ORACLE:
                            if (field.getType()==Types.VARCHAR && field.getDef().length()>0) {
                                if (field.getDef().charAt(0)!='\'' || field.getDef().charAt(field.getDef().length()-1)!='\'') {
                                    throw new DbRevisionException(
                                        "Found wrong oracle default varchar value: " + field.getDef()+". " +
                                            "Value must start and end with \' char.");
                                }
                                field.setDef(field.getDef().substring(1, field.getDef().length()-1));
                            }
                            break;
                        case POSTGREES:
                            break;
                        case SQLSERVER:
                            if (field.getDef().startsWith("(") && field.getDef().endsWith(")")) {
                                field.setDef( field.getDef().substring(1, field.getDef().length()-1));
                            }
                            break;
                    }
                }

                if (field.getDbtype().equalsIgnoreCase("BLOB")) {
                    field.setType(Types.BLOB);
                }
                else if (field.getDbtype().equalsIgnoreCase("CLOB")) {
                    field.setType(Types.CLOB);
                }
                else {
                    switch (field.getType()) {

                        case Types.DECIMAL:
                            break;

                        case Types.NUMERIC:
                            break;

                        case Types.INTEGER:
                            break;

                        case Types.DOUBLE:
                            break;

                        case Types.VARCHAR:
                            break;

                        case Types.CHAR:
                            break;

                        case Types.DATE:
                            break;

                        case Types.LONGVARCHAR:
                            break;

                        case Types.LONGVARBINARY:
                            break;

                        case Types.TIMESTAMP:
                            break;

                        case Types.BIT:
                            // Work around with MySql JDBC driver bug: TINYINT(1)==BIT
                            // always process as TINYINT
                            if (adapter.getFamily()== Database.Family.MYSQL) {
                                field.setDbtype("TINYINT");
                                field.setType(Types.TINYINT);
                                field.setSize(1);
                                field.setDigit(null);
                                break;
                            }
                            break;

                        case Types.TINYINT:
                            break;

                        case Types.BIGINT:
                            break;

                        case Types.SMALLINT:
                            break;

                        case Types.FLOAT:
                            break;

                        default:
                            final String es = "unknown field type field - " + field.getName() + " javaType - " + field.getType();
                            throw new IllegalStateException(es);
                    }
                }

                v.add(field);
            }
        }
        catch (Exception e) {
            final String es = "schemaPattern: " + schemaPattern + ", tablePattern: " + tablePattern;
            throw new DbRevisionException(es, e);
        }
        finally {
            if (metaField != null) {
                try {
                    metaField.close();
                    metaField = null;
                }
                catch (Exception e01) {
                    // catch close error
                }
            }
        }
        return v;
    }

    /**
     * @deprecated use ConstraintManager.getIndexes(adapter,  schemaName, tableName);
     * @param adapter
     * @param schemaName
     * @param tableName
     * @return
     */
    public static List<DbIndex> getIndexes(Database adapter, String schemaName, String tableName) {
        return ConstraintManager.getIndexes(adapter,  schemaName, tableName);
    }

    /**
     * @deprecated use ConstraintManager.getForeignKeys(adapter,  schemaName, tableName);
     *
     * Return info about all PK for tables, which referenced from current table(tableName)
     *
     * @param adapter db adapter
     * @param tableName  name of table
     * @param schemaName name of schema
     * @return List<DbForeignKey>
     */
    public static List<DbForeignKey> getForeignKeys(Database adapter, String schemaName, String tableName) {
        return ConstraintManager.getForeignKeys(adapter,  schemaName, tableName);
    }

    /**
     * @deprecated use ConstraintManager.getForeignKeys(adapter,  schemaName, tableName);
     * 
     * @param adapter
     * @param schemaPattern
     * @param tablePattern
     * @return
     */
    public static DbPrimaryKey getPk(Database adapter, String schemaPattern, String tablePattern) {
        return ConstraintManager.getPk(adapter,  schemaPattern, tablePattern);
    }

    public static void setDefTimestamp(Database adapter, DbTable originTable, DbField originField) {
        DbField tempField = cloneDescriptionField(originField);
        tempField.setName(tempField.getName() + '1');
        adapter.addColumn(originTable, tempField);
        copyFieldData(adapter, originTable, originField, tempField);
        dropColumn(adapter, originTable, originField);
        adapter.addColumn(originTable, originField);
        copyFieldData(adapter, originTable, tempField, originField);
        dropColumn(adapter, originTable, tempField);
    }

    public static DbPrimaryKeyColumn cloneDescriptionPrimaryKeyColumn(final DbPrimaryKeyColumn srcCol) {
        DbPrimaryKeyColumn c = new DbPrimaryKeyColumn();
        c.setC(srcCol.getC());
        c.setSeq(srcCol.getSeq());

        return c;
    }

    public static DbForeignKey cloneDescriptionFK(final DbForeignKey srcFk) {
        if (srcFk == null) {
            return null;
        }

        DbForeignKey fk = new DbForeignKey();
        fk.setDefer(srcFk.getDefer());
        fk.setDRule(srcFk.getDRule());
        fk.setFk(srcFk.getFk());
        fk.setFkTable(srcFk.getFkTable());
        fk.setFkSchema(srcFk.getFkSchema());
        fk.setPk(srcFk.getPk());
        fk.setPkTable(srcFk.getPkTable());
        fk.setPkSchema(srcFk.getPkSchema());
        fk.setURule(srcFk.getURule());
        for (DbForeignKeyColumn srcFkColumn : srcFk.getColumns()) {
            fk.getColumns().add(cloneDescriptionForeignKeyColumn(srcFkColumn));
        }

        return fk;
    }

    static DbForeignKeyColumn cloneDescriptionForeignKeyColumn(DbForeignKeyColumn srcFkColumn) {
        DbForeignKeyColumn c = new DbForeignKeyColumn();
        c.setFkCol(srcFkColumn.getFkCol());
        c.setSeq(srcFkColumn.getSeq());
        c.setPkCol(srcFkColumn.getPkCol());

        return c;
    }

    public static DbPrimaryKey cloneDescriptionPK(final DbPrimaryKey srcPk) {
        if (srcPk == null) {
            return null;
        }

        DbPrimaryKey pk = new DbPrimaryKey();
        pk.setC(srcPk.getC());
        pk.setPk(srcPk.getPk());
        pk.setS(srcPk.getS());
        pk.setT(srcPk.getT());
        for (DbPrimaryKeyColumn column : srcPk.getColumns()) {
            pk.getColumns().add(cloneDescriptionPrimaryKeyColumn(column));
        }

        return pk;
    }

    public static DbField cloneDescriptionField(final DbField srcField) {
        if (srcField == null) {
            return null;
        }

        DbField f = new DbField();
        f.setComment(srcField.getComment());
        f.setDbtype(srcField.getDbtype());
        f.setDigit(srcField.getDigit());
        f.setDef(srcField.getDef());
        f.setType(srcField.getType());
        f.setName(srcField.getName());
        f.setNullable(srcField.getNullable());
        f.setSize(srcField.getSize());

        return f;
    }

    /**
     * Clone description of table. Data not cloned
     *
     * @param srcTable source table
     * @return DbTable cloned table
     */
    public static DbTable cloneDescriptionTable(final DbTable srcTable) {
        if (srcTable == null) {
            return null;
        }

        DbTable r = new DbTable();

        r.setS(srcTable.getS());
        r.setT(srcTable.getT());

        DbPrimaryKey pk = cloneDescriptionPK(srcTable.getPk());
        r.setPk(pk);

        for (DbField DbField : srcTable.getFields()) {
            DbField f = cloneDescriptionField(DbField);
            r.getFields().add(f);
        }

        for (DbForeignKey DbForeignKey : srcTable.getForeignKeys()) {
            DbForeignKey fk = cloneDescriptionFK(DbForeignKey);
            r.getForeignKeys().add(fk);
        }

        return r;
    }

    public static void copyFieldData(
        final Database db_, final DbTable table, final DbField sourceField, final DbField targetField
    ) {
        if (table == null || sourceField == null || targetField == null) {
            return;
        }

        String sql_ =
            "update " + table.getT() + ' ' +
                "SET " + targetField.getName() + '=' + sourceField.getName();

        Statement ps = null;
        try {
            ps = db_.getConnection().createStatement();
            ps.execute(sql_);
        }
        catch (SQLException e) {
            String errorString = "Error copy data from field '" + table.getT() + '.' + sourceField.getName() +
                "' to '" + table.getT() + '.' + targetField.getName() + "' " + e.getErrorCode() + "\nsql - " + sql_;

            throw new DbRevisionException(errorString, e);
        }
        finally {
            DbUtils.close(ps);
            ps = null;
        }
    }
}
