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
package org.riverock.dbrevision.db;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import org.riverock.dbrevision.annotation.schema.db.*;
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
    private final static Logger log = Logger.getLogger(DatabaseStructureManager.class);
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
        table.setName(tableName);
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
            table.getName() == null || table.getName().length() == 0
            )
            return;

        if (field == null ||
            field.getName() == null || field.getName().length() == 0
            )
            return;

        String sql_ = "ALTER TABLE " + table.getName() + " DROP COLUMN " + field.getName();
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
            view.getName() == null || view.getName().length() == 0
            )
            return;

        String sql_ = "drop VIEW " + view.getName();
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
        if (table == null || table.getData() == null || table.getData().getRecords().isEmpty()) {
            log.debug("Table is empty");
            return;
        }

        if (table.getFields().isEmpty()) {
            throw new DbRevisionException("Table has zero count of fields");
        }

        boolean isDebug = false;
        String sql_ = "insert into " + table.getName() + "(";

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

        DbDataTable tableData = table.getData();

        for (DbDataRecord record : tableData.getRecords()) {
            PreparedStatement ps = null;
            ResultSet rs = null;
            DbField field=null;
            try {
                ps = adapter.getConnection().prepareStatement(sql_);

                int fieldPtr = 0;
                int k=0;
                for (DbDataFieldData fieldData : record.getFieldData()) {
                    field = table.getFields().get(fieldPtr++);

                    if (fieldData.isIsNull()) {
                        int type = table.getFields().get(k).getJavaType();
                        if (type == Types.TIMESTAMP) {
                            type = Types.DATE;
                        }

                        ps.setNull(k + 1, type);
                    }
                    else {
                        if (isDebug) {
                            System.out.println("param #" + (k + 1) + ", type " + table.getFields().get(k).getJavaType());
                        }

                        switch (table.getFields().get(k).getJavaType()) {
                            case Types.BIT:
                            case Types.TINYINT:
                            case Types.BIGINT:

                            case Types.DECIMAL:
                            case Types.DOUBLE:
                            case Types.NUMERIC:
                                if (field.getDecimalDigit() == null || field.getDecimalDigit() == 0) {
                                    if (isDebug) {
                                        System.out.println("Types.NUMERIC as Types.INTEGER param #" + (k + 1) + ", " +
                                            "value " + fieldData.getNumberData().doubleValue() + ", long value " + ((long) fieldData.getNumberData().doubleValue() +
                                            ", extracted value: " + fieldData.getNumberData().longValueExact())
                                        );
                                    }
                                    ps.setBigDecimal(k + 1, fieldData.getNumberData());
                                }
                                else {
                                    if (isDebug) {
                                        System.out.println("Types.NUMERIC param #" + (k + 1) + ", value " + fieldData.getNumberData().doubleValue());
                                    }
                                    ps.setBigDecimal(k + 1, fieldData.getNumberData());
                                }
                                break;

                            case Types.INTEGER:
                                if (isDebug) {
                                    System.out.println("Types.INTEGER param #" + (k + 1) + ", value " + fieldData.getNumberData().doubleValue());
                                }
                                ps.setBigDecimal(k + 1, fieldData.getNumberData());
                                break;

                            case Types.CHAR:
                                if (isDebug) {
                                    System.out.println("param #" + (k + 1) + ", value " + fieldData.getStringData().substring(0, 1));
                                }
                                ps.setString(k + 1, fieldData.getStringData().substring(0, 1));
                                break;

                            case Types.VARCHAR:
                                if (isDebug) {
                                    System.out.println("param #" + (k + 1) + ", value " + fieldData.getStringData());
                                }
                                ps.setString(k + 1, fieldData.getStringData());
                                break;

                            case Types.DATE:
                            case Types.TIMESTAMP:
                                long timeMillis = fieldData.getDateData().toGregorianCalendar().getTimeInMillis();
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
                                    byte[] bytes = Base64.decodeBase64(fieldData.getStringData().getBytes());

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
                                    System.out.println("param #" + (k + 1) + ", value " + fieldData.getStringData());
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
                String es = "Error get data for table " + table.getName();
                log.error(es, e);
                int k=0;
                for (DbDataFieldData data : record.getFieldData()) {
                    log.error("date: " + data.getDateData());
                    log.error("decimal digit: " + data.getDecimalDigit());
                    log.error("is null: " + data.isIsNull());
                    log.error("java type: " + table.getFields().get(k).getJavaType());
                    log.error("number: " + data.getNumberData());
                    log.error("size: " + data.getSize());
                    log.error("string: " + data.getStringData());
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
        DbDataTable tableData = new DbDataTable();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql_ = "select * from " + table.getName();

            ps = adapter.getConnection().prepareStatement(sql_);

            rs = ps.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();

            if (table.getFields().size() != meta.getColumnCount()) {
                log.fatal("table " + table.getName());
                log.fatal("count field " + table.getFields().size());
                log.fatal("meta count field " + meta.getColumnCount());
                for (DbField field : table.getFields()) {
                    log.fatal("\tfield " + field.getName());
                }

                throw new DbRevisionException("Count for field in ResultSet not equals in DbTable. May be you forgot initialize DbFields.");
            }

            byte[] bytes=null;

            while (rs.next()) {
                DbDataRecord record = new DbDataRecord();
                for (DbField field : table.getFields()) {
                    DbDataFieldData fieldData = new DbDataFieldData();

                    switch (field.getJavaType()) {

                        case Types.BIT:
                        case Types.TINYINT:
                        case Types.BIGINT:
                        case Types.SMALLINT:

                        case Types.DECIMAL:
                        case Types.INTEGER:
                        case Types.DOUBLE:
                        case Types.FLOAT:
                        case Types.NUMERIC:
                            fieldData.setNumberData(rs.getBigDecimal(field.getName()));
                            fieldData.setIsNull(rs.wasNull());
                            break;

                        case Types.CHAR:
                        case Types.VARCHAR:
                            fieldData.setStringData(rs.getString(field.getName()));
                            fieldData.setIsNull(rs.wasNull());
                            break;

                        case Types.DATE:
                        case Types.TIMESTAMP:
                            Timestamp timestamp = rs.getTimestamp(field.getName());
                            if (rs.wasNull()) {
                                fieldData.setIsNull(true);
                            }
                            else {
                                GregorianCalendar calendar = new GregorianCalendar();
                                calendar.setTimeInMillis(timestamp.getTime());
                                fieldData.setDateData(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
                                fieldData.setIsNull(false);
                            }
                            break;

                        case Types.LONGVARCHAR:
                            fieldData.setStringData(rs.getString(field.getName()));
                            fieldData.setIsNull(rs.wasNull());
                            break;
                            
                        case Types.LONGVARBINARY:
                            switch(adapter.getFamily()) {
                                case MYSQL:
                                    bytes = adapter.getBlobField(rs, field.getName(), MAX_LENGTH_BLOB);
                                    if (bytes!=null) {
                                        byte[] encodedBytes = Base64.encodeBase64(bytes);
                                        fieldData.setStringData( new String(encodedBytes) );
                                    }
                                    fieldData.setIsNull(rs.wasNull());
                                    bytes = null;
                                    break;
                                default:
                                    fieldData.setStringData(rs.getString(field.getName()));
                                    fieldData.setIsNull(rs.wasNull());
                            }
                            break;
                        case Types.BLOB:
                            switch (adapter.getFamily()) {
                                case ORACLE:
                                    bytes = adapter.getBlobField(rs, field.getName(), MAX_LENGTH_BLOB);
                                    fieldData.setIsNull(rs.wasNull());
                                    break;
                                case MYSQL:
                                    bytes = adapter.getBlobField(rs, field.getName(), MAX_LENGTH_BLOB);
                                    fieldData.setIsNull(rs.wasNull());
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
                                fieldData.setStringData( new String(encodedBytes) );
                            }
                            bytes = null;
                            break;
                        default:
                            String es = "Unknown field type. Field '" + field.getName() + "' type '" + field.getJavaStringType() + "'";
                            log.error(es);
                            System.out.println(es);
                    }
                    record.getFieldData().add(fieldData);
                }
                tableData.getRecords().add(record);
            }
            return tableData;
        }
        catch (Exception e) {
            String es = "Error get data for table " + table.getName();
            log.error(es, e);
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

                table.setSchema(meta.getString("TABLE_SCHEM"));
                table.setName(meta.getString("TABLE_NAME"));
                table.setType(meta.getString("TABLE_TYPE"));
                table.setRemark(meta.getString("REMARKS"));

                if (log.isDebugEnabled()) {
                    log.debug("Table - " + table.getName() + "  remak - " + table.getRemark());
                }

                v.add(table);
            }
        }
        catch (Exception e) {
            log.error("Error get list of view", e);
            throw new DbRevisionException(e);
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
                field.setDataType(metaField.getString("TYPE_NAME"));
                field.setJavaType(DbUtils.getInteger(metaField, "DATA_TYPE", Integer.MIN_VALUE));
                field.setSize(DbUtils.getInteger(metaField, "COLUMN_SIZE"));
                field.setDecimalDigit(DbUtils.getInteger(metaField, "DECIMAL_DIGITS"));
                field.setNullable(DbUtils.getInteger(metaField, "NULLABLE"));
                String defValue = metaField.getString("COLUMN_DEF");

                field.setDefaultValue(defValue == null ? null : defValue.trim());

                if (field.getDefaultValue()!=null) {
                    // fix issue with null and other default values for concrete of DB
                    switch (adapter.getFamily()) {
                        case MYSQL:
                            if (field.getJavaType()==Types.TIMESTAMP && field.getDefaultValue().equals("0000-00-00 00:00:00")) {
                                field.setDefaultValue(null);
                            }
                            break;
                        case DB2:
                            // check for IBM DB2 CURRENT TIMESTAMP
                            if (field.getDefaultValue().toUpperCase().startsWith(CURRENT)) {
                                String s1 = field.getDefaultValue().substring(CURRENT.length()).trim();
                                if (s1.equalsIgnoreCase(TIMESTAMP)) {
                                    field.setDefaultValue("current_timestamp");
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
                            if (field.getJavaType()==Types.VARCHAR && field.getDefaultValue().length()>0) {
                                if (field.getDefaultValue().charAt(0)!='\'' || field.getDefaultValue().charAt(field.getDefaultValue().length()-1)!='\'') {
                                    throw new DbRevisionException(
                                        "Found wrong oracle default varchar value: " + field.getDefaultValue()+". " +
                                            "Value must start and end with \' char.");
                                }
                                field.setDefaultValue(field.getDefaultValue().substring(1, field.getDefaultValue().length()-1));
                            }
                            break;
                        case POSTGREES:
                            break;
                        case SQLSERVER:
                            if (field.getDefaultValue().startsWith("(") && field.getDefaultValue().endsWith(")")) {
                                field.setDefaultValue( field.getDefaultValue().substring(1, field.getDefaultValue().length()-1));
                            }
                            break;
                    }
                }

                if (field.getDataType().equalsIgnoreCase("BLOB")) {
                    field.setJavaType(Types.BLOB);
                    field.setJavaStringType("java.sql.Types.BLOB");
                }
                else if (field.getDataType().equalsIgnoreCase("CLOB")) {
                    field.setJavaType(Types.CLOB);
                    field.setJavaStringType("java.sql.Types.CLOB");
                }
                else {
                    switch (field.getJavaType()) {

                        case Types.DECIMAL:
                            field.setJavaStringType("java.sql.Types.DECIMAL");
                            break;

                        case Types.NUMERIC:
                            field.setJavaStringType("java.sql.Types.NUMERIC");
                            break;

                        case Types.INTEGER:
                            field.setJavaStringType("java.sql.Types.INTEGER");
                            break;

                        case Types.DOUBLE:
                            field.setJavaStringType("java.sql.Types.DOUBLE");
                            break;

                        case Types.VARCHAR:
                            field.setJavaStringType("java.sql.Types.VARCHAR");
                            break;

                        case Types.CHAR:
                            field.setJavaStringType("java.sql.Types.CHAR");
                            break;

                        case Types.DATE:
                            field.setJavaStringType("java.sql.Types.TIMESTAMP");
                            break;

                        case Types.LONGVARCHAR:
                            field.setJavaStringType("java.sql.Types.LONGVARCHAR");
                            break;

                        case Types.LONGVARBINARY:
                            field.setJavaStringType("java.sql.Types.LONGVARBINARY");
                            break;

                        case Types.TIMESTAMP:
                            field.setJavaStringType("java.sql.Types.TIMESTAMP");
                            break;

                        case Types.BIT:
                            // Work around with MySql JDBC driver bug: TINYINT(1)==BIT
                            // always process as TINYINT
                            if (adapter.getFamily()== Database.Family.MYSQL) {
                                field.setDataType("tinyint");
                                field.setJavaType(Types.TINYINT);
                                field.setSize(1);
                                field.setDecimalDigit(null);
                                field.setJavaStringType("java.sql.Types.TINYINT");
                                break;
                            }
                            field.setJavaStringType("java.sql.Types.BIT");
                            break;

                        case Types.TINYINT:
                            field.setJavaStringType("java.sql.Types.TINYINT");
                            break;

                        case Types.BIGINT:
                            field.setJavaStringType("java.sql.Types.BIGINT");
                            break;

                        case Types.SMALLINT:
                            field.setJavaStringType("java.sql.Types.SMALLINT");
                            break;

                        case Types.FLOAT:
                            field.setJavaStringType("java.sql.Types.FLOAT");
                            break;

                        default:
                            field.setJavaStringType("unknown. schema: " + schemaPattern + ", table: " + tablePattern + ", field: " + field.getName() + ", javaType: " + field.getJavaType());
                            String es = "unknown. schema: " + schemaPattern + ", table: " + tablePattern + ", field " + field.getName() + ", javaType: " + field.getJavaType();
                            log.error(es);
                            System.out.println(es);
                    }
                }

                if (log.isDebugEnabled()) {
                    log.debug("Field name - " + field.getName());
                    log.debug("Field dataType - " + field.getDataType());
                    log.debug("Field type - " + field.getJavaType());
                    log.debug("Field size - " + field.getSize());
                    log.debug("Field decimalDigit - " + field.getDecimalDigit());
                    log.debug("Field nullable - " + field.getNullable());

                    if (field.getNullable() == DatabaseMetaData.columnNullableUnknown) {
                        log.debug("Table " + tablePattern + " field - " + field.getName() + " with unknown nullable status");
                    }

                }
                v.add(field);
            }
        }
        catch (Exception e) {
            log.error("schemaPattern: " + schemaPattern + ", tablePattern: " + tablePattern, e);
            throw new DbRevisionException(e);
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
    public static DbPrimaryKey getPrimaryKey(Database adapter, String schemaPattern, String tablePattern) {
        return ConstraintManager.getPrimaryKey(adapter,  schemaPattern, tablePattern);
    }

    public static void setDefaultValueTimestamp(Database adapter, DbTable originTable, DbField originField) {
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
        c.setColumnName(srcCol.getColumnName());
        c.setKeySeq(srcCol.getKeySeq());

        return c;
    }

    public static DbForeignKey cloneDescriptionFK(final DbForeignKey srcFk) {
        if (srcFk == null) {
            return null;
        }

        DbForeignKey fk = new DbForeignKey();
        fk.setDeferrability(srcFk.getDeferrability());
        fk.setDeleteRule(srcFk.getDeleteRule());
        fk.setFkName(srcFk.getFkName());
        fk.setFkTableName(srcFk.getFkTableName());
        fk.setFkSchemaName(srcFk.getFkSchemaName());
        fk.setPkName(srcFk.getPkName());
        fk.setPkTableName(srcFk.getPkTableName());
        fk.setPkSchemaName(srcFk.getPkSchemaName());
        fk.setUpdateRule(srcFk.getUpdateRule());
        for (DbForeignKeyColumn srcFkColumn : srcFk.getColumns()) {
            fk.getColumns().add(cloneDescriptionForeignKeyColumn(srcFkColumn));
        }

        return fk;
    }

    static DbForeignKeyColumn cloneDescriptionForeignKeyColumn(DbForeignKeyColumn srcFkColumn) {
        DbForeignKeyColumn c = new DbForeignKeyColumn();
        c.setFkColumnName(srcFkColumn.getFkColumnName());
        c.setKeySeq(srcFkColumn.getKeySeq());
        c.setPkColumnName(srcFkColumn.getPkColumnName());

        return c;
    }

    public static DbPrimaryKey cloneDescriptionPK(final DbPrimaryKey srcPk) {
        if (srcPk == null) {
            return null;
        }

        DbPrimaryKey pk = new DbPrimaryKey();
        pk.setCatalogName(srcPk.getCatalogName());
        pk.setPkName(srcPk.getPkName());
        pk.setSchemaName(srcPk.getSchemaName());
        pk.setTableName(srcPk.getTableName());
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
        f.setApplType(srcField.getApplType());
        f.setComment(srcField.getComment());
        f.setDataType(srcField.getDataType());
        f.setDecimalDigit(srcField.getDecimalDigit());
        f.setDefaultValue(srcField.getDefaultValue());
        f.setJavaStringType(srcField.getJavaStringType());
        f.setJavaType(srcField.getJavaType());
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

        r.setSchema(srcTable.getSchema());
        r.setName(srcTable.getName());
        r.setType(srcTable.getType());

        DbPrimaryKey pk = cloneDescriptionPK(srcTable.getPrimaryKey());
        r.setPrimaryKey(pk);

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
            if (log.isInfoEnabled()) {
                log.info("copy field data failed, some objects is null");
            }

            return;
        }

        String sql_ =
            "update " + table.getName() + ' ' +
                "SET " + targetField.getName() + '=' + sourceField.getName();

        Statement ps = null;
        try {
            ps = db_.getConnection().createStatement();
            ps.execute(sql_);
        }
        catch (SQLException e) {
            String errorString = "Error copy data from field '" + table.getName() + '.' + sourceField.getName() +
                "' to '" + table.getName() + '.' + targetField.getName() + "' " + e.getErrorCode() + "\nsql - " + sql_;

            log.error(errorString, e);
            System.out.println(errorString);
            throw new DbRevisionException(e);
        }
        finally {
            DbUtils.close(ps);
            ps = null;
        }
    }
}
