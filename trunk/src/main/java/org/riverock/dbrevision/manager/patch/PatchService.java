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
package org.riverock.dbrevision.manager.patch;

import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.commons.lang.StringUtils;

import org.riverock.dbrevision.annotation.schema.db.*;
import org.riverock.dbrevision.db.DatabaseAdapter;
import org.riverock.dbrevision.db.DatabaseManager;
import org.riverock.dbrevision.db.DatabaseStructureManager;
import org.riverock.dbrevision.exception.DbRevisionException;
import org.riverock.dbrevision.utils.DbUtils;
import org.riverock.dbrevision.utils.Utils;

/**
 * User: Admin
 * Date: May 15, 2003
 * Time: 11:15:35 PM
 * <p/>
 * $Id: PatchService.java 1141 2006-12-14 14:43:29Z serg_main $
 */
public final class PatchService {
    private static Logger log = Logger.getLogger(PatchService.class);

    public static final String CUSTOM_SQL_TYPE="CUSTOM_SQL";
    public static final String CUSTOM_CLASS_ACTION_TYPE="CUSTOM_CLASS_ACTION";
    public static final String CREATE_SEQUENCE_TYPE="CREATE_SEQUENCE";
    public static final String CREATE_TABLE_TYPE="CREATE_TABLE";
    public static final String ADD_TABLE_COLUMN_TYPE="ADD_TABLE_COLUMN";
    public static final String DROP_TABLE_COLUMN_TYPE="DROP_TABLE_COLUMN";
    public static final String ADD_PRIMARY_KEY_TYPE="ADD_PRIMARY_KEY";
    public static final String ADD_FOREIGN_KEY_TYPE="ADD_FOREIGN_KEY";
    public static final String DROP_PRIMARY_KEY_TYPE="DROP_PRIMARY_KEY";
    public static final String DROP_FOREIGN_KEY_TYPE="DROP_FOREIGN_KEY";
    public static final String DROP_TABLE_TYPE="DROP_TABLE";
    public static final String DROP_SEQUENCE_TYPE="DROP_SEQUENCE";
    public static final String DELETE_BEFORE_FK_TYPE="DELETE_BEFORE_FK";
    public static final String COPY_COLUMN_TYPE="COPY_COLUMN";
    public static final String CLONE_COLUMN_TYPE="CLONE_COLUMN";
    public static final String COPY_TABLE_TYPE="COPY_TABLE";

    private static enum ActionTypes {
        UNKNOWN_TYPE_VALUE,
        CUSTOM_SQL_TYPE_VALUE,
        CUSTOM_CLASS_ACTION_TYPE_VALUE,
        CREATE_SEQUENCE_TYPE_VALUE,
        CREATE_TABLE_TYPE_VALUE,
        ADD_TABLE_COLUMN_TYPE_VALUE,
        DROP_TABLE_COLUMN_TYPE_VALUE,
        ADD_PRIMARY_KEY_TYPE_VALUE,
        ADD_FOREIGN_KEY_TYPE_VALUE,
        DROP_PRIMARY_KEY_TYPE_VALUE,
        DROP_FOREIGN_KEY_TYPE_VALUE,
        DROP_TABLE_TYPE_VALUE,
        DROP_SEQUENCE_TYPE_VALUE,
        DELETE_BEFORE_FK_TYPE_VALUE,
        COPY_COLUMN_TYPE_VALUE,
        CLONE_COLUMN_TYPE_VALUE,
        COPY_TABLE_TYPE_VALUE
    }

    private static Map<String, ActionTypes> actionTypes = new HashMap<String, ActionTypes>();

    static {
        actionTypes.put(CUSTOM_SQL_TYPE, ActionTypes.CUSTOM_SQL_TYPE_VALUE);
        actionTypes.put(CUSTOM_CLASS_ACTION_TYPE, ActionTypes.CUSTOM_CLASS_ACTION_TYPE_VALUE);
        actionTypes.put(CREATE_SEQUENCE_TYPE, ActionTypes.CREATE_SEQUENCE_TYPE_VALUE);
        actionTypes.put(CREATE_TABLE_TYPE, ActionTypes.CREATE_TABLE_TYPE_VALUE);
        actionTypes.put(ADD_TABLE_COLUMN_TYPE, ActionTypes.ADD_TABLE_COLUMN_TYPE_VALUE);
        actionTypes.put(DROP_TABLE_COLUMN_TYPE, ActionTypes.DROP_TABLE_COLUMN_TYPE_VALUE);
        actionTypes.put(ADD_PRIMARY_KEY_TYPE, ActionTypes.ADD_PRIMARY_KEY_TYPE_VALUE);
        actionTypes.put(ADD_FOREIGN_KEY_TYPE, ActionTypes.ADD_FOREIGN_KEY_TYPE_VALUE);
        actionTypes.put(DROP_PRIMARY_KEY_TYPE, ActionTypes.DROP_PRIMARY_KEY_TYPE_VALUE);
        actionTypes.put(DROP_FOREIGN_KEY_TYPE, ActionTypes.DROP_FOREIGN_KEY_TYPE_VALUE);
        actionTypes.put(DROP_TABLE_TYPE, ActionTypes.DROP_TABLE_TYPE_VALUE);
        actionTypes.put(DROP_SEQUENCE_TYPE, ActionTypes.DROP_SEQUENCE_TYPE_VALUE);
        actionTypes.put(DELETE_BEFORE_FK_TYPE, ActionTypes.DELETE_BEFORE_FK_TYPE_VALUE);
        actionTypes.put(COPY_COLUMN_TYPE, ActionTypes.COPY_COLUMN_TYPE_VALUE);
        actionTypes.put(CLONE_COLUMN_TYPE, ActionTypes.CLONE_COLUMN_TYPE_VALUE);
        actionTypes.put(COPY_TABLE_TYPE, ActionTypes.COPY_TABLE_TYPE_VALUE);
    }

    public static void processPatch(DatabaseAdapter db_, Patch patch) {
        if (patch==null) {
            throw new NullPointerException("patch is null");
        }
        if (log.isInfoEnabled()) {
            log.info("process definition " + patch.getName());
        }

        processTable(db_, patch);
        processPrimaryKey(db_, patch);
        processForeignKeys(db_, patch);
        processSequences(db_, patch);
        processAction(db_, patch);
    }

    public static String getString(Action action, String nameParam, String defValue) {
        String value = getString(action, nameParam);
        if (value == null) {
            return defValue;
        }

        return value;
    }

    public static String getString(Action action, String nameParam) {
        if (action == null || nameParam == null) {
            return null;
        }

        for (ActionParameter actionParameter : action.getActionParameters()) {
            if (actionParameter.getName().equals(nameParam)) {
                return actionParameter.getData();
            }
        }
        return null;
    }

    public static Double getDouble(List<ActionParameter> actionList, String nameParam, double defValue) {
        Double value = getDouble(actionList, nameParam);
        if (value == null)
            return defValue;

        return value;
    }

    public synchronized static Double getDouble(List<ActionParameter> actionList, String nameParam) {
        if (actionList == null || nameParam == null || nameParam.length() == 0) {
            return null;
        }

        for (ActionParameter action : actionList) {
            if (action.getName().equals(nameParam)) {
                String value = action.getData();
                Double doubleValue;
                try {
                    doubleValue = new Double(value);
                }
                catch (Exception e) {
                    String errorString = "Error convert String to Double from data - " + action.getData();
                    log.error(errorString, e);
                    throw new IllegalArgumentException(errorString, e);
                }
                return doubleValue;
            }
        }
        return null;
    }

    public static Long getLong(Action action, String nameParam, long defValue) {
        Long value = getLong(action, nameParam);
        if (value == null) {
            return defValue;
        }

        return value;
    }

    public synchronized static Long getLong(Action action, String nameParam) {
        if (action == null || nameParam == null) {
            return null;
        }

        for (ActionParameter actionParameter : action.getActionParameters()) {
            if (actionParameter.getName().equals(nameParam)) {
                String value = actionParameter.getData();
                Long longValue;
                try {
                    longValue = new Long(value);
                }
                catch (Exception e) {
                    String errorString = "Error convert String to Long from data - " + actionParameter.getData();
                    log.error(errorString, e);
                    throw new IllegalArgumentException(errorString, e);
                }
                return longValue;
            }
        }
        return null;
    }

    public static Integer getInteger(Action action, String nameParam, int defValue) {
        Integer value = getInteger(action, nameParam);
        if (value == null) {
            return defValue;
        }

        return value;
    }

    public static Integer getInteger(Action action, String nameParam) {
        if (action == null || nameParam == null) {
            return null;
        }

        for (ActionParameter actionParameter : action.getActionParameters()) {
            if (actionParameter.getName().equals(nameParam)) {
                String value = actionParameter.getData();
                Integer intValue;
                try {
                    intValue = new Integer(value);
                }
                catch (Exception e) {
                    String errorString = "Error convert String to Integer from data - " + actionParameter.getData();
                    log.error(errorString, e);
                    throw new IllegalArgumentException(errorString, e);
                }
                return intValue;
            }
        }
        return null;
    }

    public static Boolean getBoolean(Action action, String nameParam, boolean defValue) {
        Boolean value = getBoolean(action, nameParam);
        if (value == null) {
            return defValue;
        }

        return value;
    }

    public synchronized static Boolean getBoolean(Action action, String nameParam) {
        if (action == null || nameParam == null) {
            return null;
        }

        for (ActionParameter actionParameter : action.getActionParameters()) {
            if (actionParameter.getName().equals(nameParam)) {
                String value = actionParameter.getData();
                if (value == null) {
                    value = "false";
                }

                if (value.equals("1")) {
                    value = "true";
                }

                Boolean booleanValue;
                try {
                    booleanValue = Boolean.valueOf(value);
                }
                catch (Exception e) {
                    String errorString = "Error convert String to Boolean from data - " + actionParameter.getData();
                    log.error(errorString, e);
                    throw new IllegalArgumentException(errorString);
                }
                return booleanValue;
            }
        }
        return null;
    }

    ////////////////////////

    private static void processTable(DatabaseAdapter db_, Patch patch) {
        log.debug("processTable ");
        if (patch == null) {
            return;
        }

        for (Object o : patch.getActionOrTableDataOrTable()) {
             if (o instanceof DbTable) {
                 db_.createTable((DbTable)o);
             }
        }
    }

    private static void processPrimaryKey(DatabaseAdapter db_, Patch patch) {
        log.debug("processPrimaryKey ");
        if (patch == null) {
            return;
        }

        for (Object o : patch.getActionOrTableDataOrTable()) {
             if (o instanceof DbPrimaryKey) {
                DbPrimaryKey pk = (DbPrimaryKey)o;

                 if (!pk.getColumns().isEmpty()) {
                     DbSchema schema = DatabaseManager.getDbStructure(db_);
                     DbTable table = DatabaseManager.getTableFromStructure(schema, pk.getTableName());
                     DatabaseManager.addPrimaryKey(db_, table, pk);
                 }
             }
        }
    }

    private static void processForeignKeys(DatabaseAdapter adapter, Patch patch) {
        log.debug("processForeignKeys ");
        if (patch == null) {
            return;
        }

        List<DbForeignKey> keys = new ArrayList<DbForeignKey>();
        for (Object o : patch.getActionOrTableDataOrTable()) {
             if (o instanceof DbForeignKey) {
                 keys.add((DbForeignKey)o);
            }
        }

        int p=0;
        for (DbForeignKey key : keys) {
            if (StringUtils.isBlank(key.getFkName())) {
                key.setFkName(key.getFkTableName() + p + "_fk");
            }
            DatabaseStructureManager.createForeignKey(adapter, key);
        }
    }

    private static void processSequences(DatabaseAdapter db_, Patch patch) {
        log.debug("processSequences ");
        if (patch == null) {
            return;
        }

        for (Object o : patch.getActionOrTableDataOrTable()) {
             if (o instanceof DbSequence) {
                 db_.createSequence((DbSequence)o);
             }
        }
    }

    private static void processAction(DatabaseAdapter db_, Patch patch) {
        log.debug("processAction ");
        if (patch == null) {
            return;
        }

        for (Object o : patch.getActionOrTableDataOrTable()) {
             if (o instanceof Action) {
                 Action action = (Action)o;
                        try {
                            ActionTypes type = actionTypes.get(action.getType());
                            if (type==null) type=ActionTypes.UNKNOWN_TYPE_VALUE;
                            switch (type) {
                                case ADD_FOREIGN_KEY_TYPE_VALUE: {

                                }
                                break;

                                case ADD_PRIMARY_KEY_TYPE_VALUE: {

                                }
                                break;

                                case ADD_TABLE_COLUMN_TYPE_VALUE: {
                                    if (log.isDebugEnabled())
                                        log.debug("process action ADD_TABLE_COLUMN_TYPE");

                                    DbField field = new DbField();

                                    field.setName(getString(action, "column_name"));
                                    field.setJavaType(
                                        DatabaseManager.sqlTypesMapping(getString(action, "column_type"))
                                    );
                                    field.setSize(getInteger(action, "column_size", 0));
                                    field.setDecimalDigit(getInteger(action, "column_decimal_digit", 0));
                                    field.setDefaultValue(getString(action, "column_default_value"));
                                    field.setNullable(getInteger(action, "column_nullable", 0));

                                    DatabaseStructureManager.addColumn(db_, getString(action, "table_name"), field);
                                }
                                break;

                                case CLONE_COLUMN_TYPE_VALUE: {

                                }
                                break;

                                case COPY_COLUMN_TYPE_VALUE: {

                                }
                                break;

                                case CREATE_SEQUENCE_TYPE_VALUE: {
                                    DbSequence seq = new DbSequence();
                                    seq.setCacheSize(getInteger(action, "sequence_cache_size", 0));
                                    seq.setIncrementBy(getInteger(action, "sequence_increment", 1));
                                    seq.setIsCycle(getBoolean(action, "sequence_is_cycle", false));
                                    seq.setIsOrder(getBoolean(action, "sequence_is_order", false));
                                    seq.setLastNumber(getLong(action, "sequence_last_number", 0));
                                    seq.setMaxValue(getString(action, "sequence_max_value", "0"));
                                    seq.setMinValue(getInteger(action, "sequence_min_value", 0));
                                    seq.setName(getString(action, "sequence_name"));

                                    db_.createSequence(seq);
                                }
                                break;

                                case CREATE_TABLE_TYPE_VALUE: {

                                }
                                break;

                                case CUSTOM_CLASS_ACTION_TYPE_VALUE: {
                                    String className = getString(action, "class_name");
                                    if (className == null)
                                        throw new Exception("Patch - " + patch.getName() + ", action '" + CUSTOM_CLASS_ACTION_TYPE + "' must have parameter 'class_name'");

                                    Object obj = Utils.createCustomObject(className);
                                    if (obj == null)
                                        throw new Exception("Patch - " + patch.getName() + ", action '" + CUSTOM_CLASS_ACTION_TYPE + "', obj is null");

                                    ((PatchAction) obj).processAction(db_, action);
                                }
                                break;

                                case CUSTOM_SQL_TYPE_VALUE: {
                                    String sql = getString(action, "sql");
                                    if (log.isDebugEnabled()) {
                                        log.debug("Action type " + action.getType());
                                        log.debug("Custom sql " + sql);
                                    }
                                    Statement st = null;
                                    try {
                                        st = db_.getConnection().createStatement();
                                        st.execute(sql);
                                    }
                                    catch (Exception e) {
                                        log.error("Exception exceute statement " + sql, e);
                                        throw e;
                                    }
                                    catch (Error e) {
                                        log.error("Error exceute statement " + sql, e);
                                        throw e;
                                    }
                                    finally {
                                        DbUtils.close(st);
                                        //noinspection UnusedAssignment
                                        st = null;
                                    }
                                }
                                break;

                                case DELETE_BEFORE_FK_TYPE_VALUE: {

                                }
                                break;

                                case DROP_FOREIGN_KEY_TYPE_VALUE: {

                                }
                                break;

                                case DROP_PRIMARY_KEY_TYPE_VALUE: {

                                }
                                break;

                                case DROP_TABLE_TYPE_VALUE: {
                                    String nameTable = getString(action, "name_table");
                                    if (nameTable != null) {
                                        db_.dropTable(nameTable);
                                        db_.getConnection().commit();
                                    } else
                                        log.error("Patch - " + patch.getName() + ", action '" + DROP_TABLE_TYPE + "' must have parameter 'name_table'");

                                }
                                break;

                                case DROP_TABLE_COLUMN_TYPE_VALUE: {

                                }
                                break;

                                case DROP_SEQUENCE_TYPE_VALUE: {
                                    String nameSeq = getString(action, "name_sequence");
                                    if (nameSeq != null) {
                                        db_.dropSequence(nameSeq);
                                    } else
                                        log.error("Patch - " + patch.getName() + ", action '" + DROP_TABLE_TYPE + "' must have parameter 'name_sequence'");
                                }
                                break;
                                default:
                                    String errorString = "Unknown type of action - " + action.getType();
                                    log.error(errorString);
                                    throw new Exception(errorString);

                            }
                        }
                        catch (Exception e) {
                            throw new DbRevisionException(e);
                        }
            }
        }
    }
}
