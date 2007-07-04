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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TreeSet;
import java.util.Map;
import java.util.List;
import java.util.GregorianCalendar;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.datatype.DatatypeFactory;

import org.apache.log4j.Logger;
import org.apache.commons.codec.binary.Base64;

import org.riverock.dbrevision.annotation.schema.db.*;
import org.riverock.dbrevision.utils.Utils;
import org.riverock.dbrevision.utils.DbUtils;
import org.riverock.dbrevision.exception.DbRevisionException;

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

    /**
     * create foreign key
     *
     * @param adapter db adapter
     * @param fkList list of foreign keys
     */
    public static void createForeignKey(DatabaseAdapter adapter, DbImportedKeyList fkList) {
        if (fkList == null || fkList.getKeys().isEmpty()) {
            return;
        }

        Map<String, DbImportedPKColumn> hash = DatabaseManager.getFkNames(fkList.getKeys());

        int p = 0;

        for (Map.Entry<String, DbImportedPKColumn> entry : hash.entrySet()) {

            DbImportedPKColumn fkColumn = entry.getValue();
            String searchCurrent = DatabaseManager.getRelateString(fkColumn);
            String sql =
                "ALTER TABLE " + fkList.getKeys().get(0).getFkTableName() + " " +
                    "ADD CONSTRAINT " +
                    (
                        fkColumn.getFkName() == null || fkColumn.getFkName().length() == 0
                            ? fkList.getKeys().get(0).getFkTableName() + p + "_fk"
                            : fkColumn.getFkName()
                    ) +
                    " FOREIGN KEY (";

            int seq = Integer.MIN_VALUE;
            boolean isFirst = true;
            for (DbImportedPKColumn currFkCol : fkList.getKeys()) {
                String search = DatabaseManager.getRelateString(currFkCol);
                if (!searchCurrent.equals(search))
                    continue;

                DbImportedPKColumn column = null;
                int seqTemp = Integer.MAX_VALUE;
                for (DbImportedPKColumn columnTemp : fkList.getKeys()) {
                    String searchTemp = DatabaseManager.getRelateString(columnTemp);
                    if (!searchCurrent.equals(searchTemp))
                        continue;

                    if (seq < columnTemp.getKeySeq() && columnTemp.getKeySeq() < seqTemp) {
                        seqTemp = columnTemp.getKeySeq();
                        column = columnTemp;
                    }
                }
                seq = column.getKeySeq();

                if (!isFirst)
                    sql += ",";
                else
                    isFirst = !isFirst;

                sql += column.getFkColumnName();
            }
            sql += ")\nREFERENCES " + fkColumn.getPkTableName() + " (";

            seq = Integer.MIN_VALUE;
            isFirst = true;
            for (DbImportedPKColumn currFkCol : fkList.getKeys()) {
                String search = DatabaseManager.getRelateString(currFkCol);
                if (!searchCurrent.equals(search))
                    continue;

                DbImportedPKColumn column = null;
                int seqTemp = Integer.MAX_VALUE;
                for (DbImportedPKColumn columnTemp : fkList.getKeys()) {
                    String searchTemp = DatabaseManager.getRelateString(columnTemp);
                    if (!searchCurrent.equals(searchTemp))
                        continue;

                    if (seq < columnTemp.getKeySeq() && columnTemp.getKeySeq() < seqTemp) {
                        seqTemp = columnTemp.getKeySeq();
                        column = columnTemp;
                    }
                }

                seq = column.getKeySeq();

                if (!isFirst)
                    sql += ",";
                else
                    isFirst = !isFirst;

                sql += column.getPkColumnName();
            }
            sql += ") ";
            switch (fkColumn.getDeleteRule().getRuleType()) {
                case DatabaseMetaData.importedKeyRestrict:
                    sql += adapter.getOnDeleteSetNull();
                    break;
                case DatabaseMetaData.importedKeyCascade:
                    sql += "ON DELETE CASCADE ";
                    break;

                default:
                    throw new IllegalArgumentException(" imported keys delete rule '" +
                        fkColumn.getDeleteRule().getRuleName() + "' not supported");
            }
            switch (fkColumn.getDeferrability().getRuleType()) {
                case DatabaseMetaData.importedKeyNotDeferrable:
                    break;
                case DatabaseMetaData.importedKeyInitiallyDeferred:
                    sql += " DEFERRABLE INITIALLY DEFERRED";
                    break;

                default:
                    throw new IllegalArgumentException(" imported keys deferred rule '" +
                        fkColumn.getDeferrability().getRuleName() + "' not supported");
            }

            PreparedStatement ps = null;
            try {
                ps = adapter.getConnection().prepareStatement(sql);
                ps.executeUpdate();
            }
            catch (SQLException exc) {
                if (!adapter.testExceptionTableExists(exc)) {
                    System.out.println("sql " + sql);
                    System.out.println("code " + exc.getErrorCode());
                    System.out.println("state " + exc.getSQLState());
                    System.out.println("message " + exc.getMessage());
                    System.out.println("string " + exc.toString());
                }
                throw new DbRevisionException(exc);
            }
            finally {
                DatabaseManager.close(ps);
                ps = null;
            }

        }
    }

    /**
     * add column to table
     *
     * @param adapter db adapter
     * @param tableName table name
     * @param field column descriptor
     */
    public static void addColumn(DatabaseAdapter adapter, String tableName, DbField field) {
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
    public static void dropColumn(DatabaseAdapter adapter, DbTable table, DbField field) {
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
            DatabaseManager.close(ps);
            ps = null;
        }
    }

    public static void dropView(DatabaseAdapter adapter, DbView view) {
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
            DatabaseManager.close(ps);
            ps = null;
        }
    }

    public static void setDataTable(DatabaseAdapter adapter, DbTable table) {
        setDataTable(adapter, table, null);
    }

    public static void setDataTable(DatabaseAdapter adapter, DbTable table, List<DbBigTextTable> bigTables) {
        if (table == null || table.getData() == null || table.getData().getRecords().size() == 0) {
            log.debug("Table is empty");
            return;
        }

        DbBigTextTable big = DatabaseManager.getBigTextTableDesc(table, bigTables);

        if (table.getFields().isEmpty()) {
            throw new DbRevisionException("Table has zero count of fields");
        }


        boolean isDebug = false;

        String sql_ =
            "insert into " + table.getName() +
                "(";

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

        if (big == null) {

            for (DbDataRecord record : tableData.getRecords()) {
                PreparedStatement ps = null;
                ResultSet rs = null;
                DbField field=null;
                try {
                    ps = adapter.getConnection().prepareStatement(sql_);

                    int fieldPtr = 0;
                    int k=0;
                    for (DbDataFieldData fieldData : record.getFieldsData()) {
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
                                        ps.setLong(k + 1, fieldData.getNumberData().longValueExact());
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
                                    ps.setLong(k + 1, fieldData.getNumberData().longValueExact());
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
                                    if (adapter.getFamily()== DatabaseAdapter.Family.MYSQL) {
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
                    for (DbDataFieldData data : record.getFieldsData()) {
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
                    DatabaseManager.close(rs, ps);
                    rs = null;
                    ps = null;
                }
            }
        }
        else { // process big text table

            int idx = 0;
            int idxFk = 0;
            int idxPk = 0;
            boolean isNotFound = true;
            // search indices of fields in list
            int i=0;
            for (DbField field : table.getFields()) {
                if (field.getName().equals(big.getStorageField())) {
                    idx = i;
                    isNotFound = false;
                }
                if (field.getName().equals(big.getSlaveFkField())) {
                    idxFk = i;
                }
                if (field.getName().equals(big.getSlavePkField())) {
                    idxPk = i;
                }
                i++;
            }
            if (isNotFound) {
                throw new DbRevisionException("Storage field '" + big.getStorageField() + "' not found in table " + table.getName());
            }

            if (isDebug) {
                System.out.println("pk idx " + idxPk);
                System.out.println("fk idx " + idxFk);
                System.out.println("storage idx " + idx);
            }
            Hashtable<Long, Object> hashFk = new Hashtable<Long, Object>(tableData.getRecords().size());
            for (DbDataRecord record : tableData.getRecords()) {
                DbDataFieldData fieldFk = record.getFieldsData().get(idxFk);
                Long idRec = fieldFk.getNumberData().longValue();

                hashFk.put(idRec, new Object());
            }

            // Insert records while we moved over list of foreign keys
            for (Enumeration e = hashFk.keys(); e.hasMoreElements();) {
                Long idFk = (Long) e.nextElement();

                if (isDebug) {
                    System.out.println("ID of fk " + idFk);
                }

                TreeSet<Long> setPk = new TreeSet<Long>();

                // ������� ������ ������������� ��������� ������
                // ��� ������� ���������� �����
                for (DbDataRecord record : tableData.getRecords()) {
                    // get value for foreign key
                    DbDataFieldData fieldFk = record.getFieldsData().get(idxFk);
                    long idRec = fieldFk.getNumberData().longValue();

                    // get value for primary key
                    DbDataFieldData fieldPk = record.getFieldsData().get(idxPk);
                    long idPkRec = fieldPk.getNumberData().longValue();

                    if (idFk == idRec)
                        setPk.add(idPkRec);
                }

                String tempData = "";
                // �������� �� ������ ��������� ������ ������� �������������� ��������� ������
                for (Long aSetPk : setPk) {

                    for (DbDataRecord record : tableData.getRecords()) {

                        DbDataFieldData fieldPk = record.getFieldsData().get(idxPk);
                        long pkTemp = fieldPk.getNumberData().longValue();

                        if (pkTemp == aSetPk) {
                            DbDataFieldData fieldData = record.getFieldsData().get(idx);
                            if (fieldData.getStringData() != null)
                                tempData += fieldData.getStringData();
                        }
                    }
                }


                if (isDebug) {
                    System.out.println("Big text " + tempData);
                }

                PreparedStatement ps1 = null;
                try {

                    if (log.isDebugEnabled()) {
                        log.debug("Start insert data in bigtext field ");
                    }

                    int pos = 0;
                    int prevPos = 0;
                    int maxByte = adapter.getMaxLengthStringField();

                    sql_ =
                        "insert into " + big.getSlaveTable() +
                            '(' + big.getSlavePkField() + ',' +
                            big.getSlaveFkField() + ',' +
                            big.getStorageField() + ')' +
                            "values" +
                            "(?,?,?)";

                    if (log.isDebugEnabled()) {
                        log.debug("insert bigtext. sql 2 - " + sql_);
                    }

                    byte b[] = Utils.getBytesUTF(tempData);

                    ps1 = adapter.getConnection().prepareStatement(sql_);
                    while ((pos = Utils.getStartUTF(b, maxByte, pos)) != -1) {
                        if (log.isDebugEnabled()) {
                            log.debug("Name sequence - " + big.getSequenceName());
                        }

                        CustomSequence seq = new CustomSequence();
                        seq.setSequenceName(big.getSequenceName());
                        seq.setTableName(big.getSlaveTable());
                        seq.setColumnName(big.getSlavePkField());
                        long idSeq = adapter.getSequenceNextValue(seq);

                        if (log.isDebugEnabled()) {
                            log.debug("Bind param #1" + idSeq);
                        }

                        ps1.setLong(1, idSeq);

                        if (log.isDebugEnabled())
                            log.debug("Bind param #2 " + idFk);

                        ps1.setLong(2, idFk);


                        String s = new String(b, prevPos, pos - prevPos, "utf-8");

                        if (log.isDebugEnabled())
                            log.debug("Bind param #3 " + s + (s != null ? ", len " + s.length() : ""));

                        ps1.setString(3, s);

                        if (log.isDebugEnabled())
                            log.debug("Bind param #3 " + s + (s != null ? ", len " + s.length() : ""));

                        if (isDebug && s != null && s.length() > 2000) {
                            System.out.println("Do executeUpdate");
                        }

                        int count = ps1.executeUpdate();

                        if (log.isDebugEnabled()) {
                            log.debug("number of updated records - " + count);
                        }

                        prevPos = pos;

                    } // while ( (pos=StringTools.getStartUTF ...
                }
                catch (SQLException e1) {
                    throw new DbRevisionException(e1);
                }
                catch (UnsupportedEncodingException e1) {
                    throw new DbRevisionException(e1);
                }
                finally {
                    DatabaseManager.close(ps1);
                    ps1 = null;
                }
            }
        }
    }

    /**
     * @param adapter db adapter
     * @param table table for get data
     * @return DbDataTable
     */
    public static DbDataTable getDataTable(DatabaseAdapter adapter, DbTable table) {
        DbDataTable tableData = new DbDataTable();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql_ = "select * from " + table.getName();

            ps = adapter.getConnection().prepareStatement(sql_);

            rs = ps.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();

            if (table.getFields().size() != meta.getColumnCount()) {
                System.out.println("table " + table.getName());
                System.out.println("count field " + table.getFields().size());
                System.out.println("meta count field " + meta.getColumnCount());
                for (DbField field : table.getFields()) {
                    System.out.println("\tfield " + field.getName());
                }

                throw new DbRevisionException("Count for field in ResultSet not equals in DbTable");
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
                            System.out.println("Unknown field type. Field '" + field.getName() + "' type '" + field.getJavaStringType() + "'");
                    }
                    record.getFieldsData().add(fieldData);
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
            DatabaseManager.close(rs, ps);
            rs = null;
            ps = null;
        }
    }

    /**
     * Return filtered list of tables
     * usually schemaPattern is a db username
     * if tablePattern equals "%", this mean what selected all tables
     *
     * @param conn1 db connection
     * @param schemaPattern schema name filter 
     * @param tablePattern table name filter
     * @return List of DbTable
     */
    public static List<DbTable> getTableList(Connection conn1, String schemaPattern, String tablePattern) {
        String[] types = {"TABLE"};

        ResultSet meta = null;
        List<DbTable> v = new ArrayList<DbTable>();
        try {
            DatabaseMetaData db = conn1.getMetaData();

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
    public static List<DbField> getFieldsList(DatabaseAdapter adapter, String schemaPattern, String tablePattern) {
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
                    // fix issue with null value for concrete of BD
                    switch (adapter.getFamily()) {
                        case MYSQL:
                            if (field.getJavaType()==Types.TIMESTAMP && field.getDefaultValue().equals("0000-00-00 00:00:00")) {
                                field.setDefaultValue(null);
                            }
                            break;
                        case DB2:
                            break;
                        case HYPERSONIC:
                            break;
                        case INTERBASE:
                            break;
                        case MAXDB:
                            break;
                        case ORACLE:
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
                            if (adapter.getFamily()== DatabaseAdapter.Family.MYSQL) {
                                field.setDataType("tinyint");
                                field.setJavaType(Types.TINYINT);
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
                            System.out.println("unknown. schema: " + schemaPattern + ", table: " + tablePattern + ", field " + field.getName() + ", javaType: " + field.getJavaType());
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
     * Return info about all PK for tables, which referenced from current table(tableName)
     *
     * @param adapter db adapter
     * @param tableName  name of table
     * @param schemaName name of schema
     * @return List<DbImportedPKColumn>
     */
    public static List<DbImportedPKColumn> getImportedKeys(DatabaseAdapter adapter, String schemaName, String tableName) {
        List<DbImportedPKColumn> v = new ArrayList<DbImportedPKColumn>();
        try {
            DatabaseMetaData db = adapter.getConnection().getMetaData();
            ResultSet columnNames = null;

            if (log.isDebugEnabled())
                log.debug("Get data from getImportedKeys");

            try {
                columnNames = db.getImportedKeys(null, schemaName, tableName);

                while (columnNames.next()) {
                    DbImportedPKColumn impPk = new DbImportedPKColumn();

                    impPk.setPkSchemaName(columnNames.getString("PKTABLE_SCHEM"));
                    impPk.setPkTableName(columnNames.getString("PKTABLE_NAME"));
                    impPk.setPkColumnName(columnNames.getString("PKCOLUMN_NAME"));

                    impPk.setFkSchemaName(columnNames.getString("FKTABLE_SCHEM"));
                    impPk.setFkTableName(columnNames.getString("FKTABLE_NAME"));
                    impPk.setFkColumnName(columnNames.getString("FKCOLUMN_NAME"));

                    impPk.setKeySeq(DbUtils.getInteger(columnNames, "KEY_SEQ"));

                    impPk.setPkName(columnNames.getString("PK_NAME"));
                    impPk.setFkName(columnNames.getString("FK_NAME"));

                    impPk.setUpdateRule(DatabaseManager.decodeUpdateRule(columnNames));
                    impPk.setDeleteRule(DatabaseManager.decodeDeleteRule(columnNames));
                    impPk.setDeferrability(DatabaseManager.decodeDeferrabilityRule(columnNames));

                    if (log.isDebugEnabled()) {
                        log.debug(
                            columnNames.getString("PKTABLE_CAT") + " - " +
                                columnNames.getString("PKTABLE_SCHEM") + "." +
                                columnNames.getString("PKTABLE_NAME") +
                                " - " +
                                columnNames.getString("PKCOLUMN_NAME") +
                                " >> " +
                                columnNames.getString("FKTABLE_CAT") + "." +
                                columnNames.getString("FKTABLE_SCHEM") + "." +
                                columnNames.getString("FKTABLE_NAME") +
                                "; " +
                                columnNames.getShort("KEY_SEQ") + " " +
                                columnNames.getString("UPDATE_RULE") + " " +
                                columnNames.getShort("DELETE_RULE") + " ");
                        Object obj = null;
                        int deferr;
                        obj = columnNames.getObject("DELETE_RULE");

                        if (obj == null)
                            deferr = Integer.MIN_VALUE;
                        else
                            deferr = (int) columnNames.getShort("DELETE_RULE");

                        switch (deferr) {
                            case DatabaseMetaData.importedKeyNoAction:
                                log.debug("DELETE_RULE.importedKeyNoAction");
                                break;
                            case DatabaseMetaData.importedKeyCascade:
                                log.debug("DELETE_RULE.importedKeyCascade");
                                break;
                            case DatabaseMetaData.importedKeySetNull:
                                log.debug("DELETE_RULE.importedKeySetNull");
                                break;
                            case DatabaseMetaData.importedKeyRestrict:
                                log.debug("DELETE_RULE.importedKeyRestrict");
                                break;
                            case DatabaseMetaData.importedKeySetDefault:
                                log.debug("DELETE_RULE.importedKeySetDefault");
                                break;
                            default:
                                log.debug("unknown DELETE_RULE(" + deferr + ")");
                                break;
                        }
                        log.debug("obj: " + obj.getClass().getName() + " ");

                        log.debug("Foreign key name: " + columnNames.getString("FK_NAME") + " ");
                        log.debug("Primary key name: " + columnNames.getString("PK_NAME") + " ");

                        obj = columnNames.getObject("DEFERRABILITY");
                        if (obj == null)
                            deferr = -1;
                        else
                            deferr = (int) columnNames.getShort("DEFERRABILITY");

                        switch (deferr) {
                            case DatabaseMetaData.importedKeyInitiallyDeferred:
                                log.debug("importedKeyInitiallyDeferred");
                                break;
                            case DatabaseMetaData.importedKeyInitiallyImmediate:
                                log.debug("importedKeyInitiallyImmediate");
                                break;
                            case DatabaseMetaData.importedKeyNotDeferrable:
                                log.debug("importedKeyNotDeferrable");
                                break;
                            default:
                                log.debug("unknown DEFERRABILITY(" + deferr + ")");
                                break;
                        }

                    }
                    v.add(impPk);
                }
                columnNames.close();
                columnNames = null;

            }
            catch (Exception e1) {
                log.debug("Method getImportedKeys(null, null, tableName) not supported", e1);
            }
            log.debug("Done  data from getImportedKeys");

        }
        catch (Exception e) {
            throw new DbRevisionException(e);
        }
        return v;
    }

    public static DbPrimaryKey getPrimaryKey(DatabaseAdapter adapter, String schemaPattern, String tablePattern) {

        DbPrimaryKey pk = new DbPrimaryKey();
        ArrayList<DbPrimaryKeyColumn> v = new ArrayList<DbPrimaryKeyColumn>();

        if (log.isDebugEnabled()) {
            log.debug("Get data from getPrimaryKeys");
        }

        try {
            DatabaseMetaData db = adapter.getConnection().getMetaData();
            ResultSet metaData = null;
            metaData = db.getPrimaryKeys(null, schemaPattern, tablePattern);
            while (metaData.next()) {
                DbPrimaryKeyColumn pkColumn = new DbPrimaryKeyColumn();

                pkColumn.setCatalogName(metaData.getString("TABLE_CAT"));
                pkColumn.setSchemaName(metaData.getString("TABLE_SCHEM"));
                pkColumn.setTableName(metaData.getString("TABLE_NAME"));
                pkColumn.setColumnName(metaData.getString("COLUMN_NAME"));
                pkColumn.setKeySeq(DbUtils.getInteger(metaData, "KEY_SEQ"));
                pkColumn.setPkName(metaData.getString("PK_NAME"));

                if (log.isDebugEnabled()) {
                    log.debug(
                        pkColumn.getCatalogName() + "." +
                            pkColumn.getSchemaName() + "." +
                            pkColumn.getTableName() +
                            " - " +
                            pkColumn.getColumnName() +
                            " " +
                            pkColumn.getKeySeq() + " " +
                            pkColumn.getPkName() + " " +
                            ""
                    );
                }
                v.add(pkColumn);
            }
            metaData.close();
            metaData = null;
        }
        catch (Exception e1) {
            log.warn("Method db.getPrimaryKeys(null, null, tableName) not supported", e1);
        }

        if (log.isDebugEnabled()) {
            log.debug("Done data from getPrimaryKeys");
        }

        if (log.isDebugEnabled()) {
            if (v.size() > 1) {
                log.debug("Table with multicolumn PK.");

                for (DbPrimaryKeyColumn pkColumn : v) {
                    log.debug(
                            pkColumn.getCatalogName() + "." +
                                    pkColumn.getSchemaName() + "." +
                                    pkColumn.getTableName() +
                                    " - " +
                                    pkColumn.getColumnName() +
                                    " " +
                                    pkColumn.getKeySeq() + " " +
                                    pkColumn.getPkName() + " " +
                                    ""
                    );
                }
            }
        }
        pk.getColumns().addAll(v);

        return pk;
    }

    public static void setDefaultValueTimestamp(DatabaseAdapter adapter, DbTable originTable, DbField originField)
        throws Exception {
        DbField tempField = DatabaseManager.cloneDescriptionField(originField);
        tempField.setName(tempField.getName() + '1');
        adapter.addColumn(originTable, tempField);
        DatabaseManager.copyFieldData(adapter, originTable, originField, tempField);
        dropColumn(adapter, originTable, originField);
        adapter.addColumn(originTable, originField);
        DatabaseManager.copyFieldData(adapter, originTable, tempField, originField);
        dropColumn(adapter, originTable, tempField);
    }
}