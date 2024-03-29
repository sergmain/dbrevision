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
package org.riverock.dbrevision.manager.patch;

import java.sql.Statement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.riverock.dbrevision.db.Database;
import org.riverock.dbrevision.db.DatabaseManager;
import org.riverock.dbrevision.db.DatabaseStructureManager;
import org.riverock.dbrevision.db.ConstraintManager;
import org.riverock.dbrevision.exception.DbRevisionException;
import org.riverock.dbrevision.utils.DbUtils;
import org.riverock.dbrevision.utils.Utils;
import org.riverock.dbrevision.schema.db.v3.*;

/**
 * User: Admin
 * Date: May 15, 2003
 * Time: 11:15:35 PM
 * <p/>
 * $Id: PatchService.java 1141 2006-12-14 14:43:29Z serg_main $
 */
public final class PatchService {

    static final String CUSTOM_SQL_TYPE = "CUSTOM_SQL";
    static final String CUSTOM_CLASS_ACTION_TYPE = "CUSTOM_CLASS_ACTION";
    static final String CREATE_SEQUENCE_TYPE = "CREATE_SEQUENCE";
    static final String CREATE_TABLE_TYPE = "CREATE_TABLE";
    static final String ADD_TABLE_COLUMN_TYPE = "ADD_TABLE_COLUMN";
    static final String DROP_TABLE_COLUMN_TYPE = "DROP_TABLE_COLUMN";
    static final String ADD_PRIMARY_KEY_TYPE = "ADD_PRIMARY_KEY";
    static final String ADD_FOREIGN_KEY_TYPE = "ADD_FOREIGN_KEY";
    static final String DROP_PRIMARY_KEY_TYPE = "DROP_PRIMARY_KEY";
    static final String DROP_FOREIGN_KEY_TYPE = "DROP_FOREIGN_KEY";
    static final String DROP_TABLE_TYPE = "DROP_TABLE";
    static final String DROP_SEQUENCE_TYPE = "DROP_SEQUENCE";
    static final String DELETE_BEFORE_FK_TYPE = "DELETE_BEFORE_FK";
    static final String COPY_COLUMN_TYPE = "COPY_COLUMN";
    static final String CLONE_COLUMN_TYPE = "CLONE_COLUMN";
    static final String COPY_TABLE_TYPE = "COPY_TABLE";

    private static enum ActionTypes {
        SQL,
        CUSTOM_CLASS,
        ADD_TABLE_FIELD,

        CREATE_SEQUENCE,
        CREATE_TABLE,
        DROP_TABLE_COLUMN,
        ADD_PRIMARY_KEY,
        ADD_FOREIGN_KEY,
        DROP_PRIMARY_KEY,
        DROP_FOREIGN_KEY,
        DROP_TABLE,
        DROP_SEQUENCE,
        DELETE_BEFORE_FK,
        COPY_COLUMN,
        CLONE_COLUMN,
        COPY_TABLE
    }

    private static Map<String, ActionTypes> actionTypes = new HashMap<String, ActionTypes>();

    static {
        actionTypes.put(CustomClassAction.class.getName(), ActionTypes.SQL);
        actionTypes.put(AddTableFieldAction.class.getName(), ActionTypes.ADD_TABLE_FIELD);
        actionTypes.put(CustomClassAction.class.getName(), ActionTypes.CUSTOM_CLASS);

        actionTypes.put(CREATE_SEQUENCE_TYPE, ActionTypes.CREATE_SEQUENCE);
        actionTypes.put(CREATE_TABLE_TYPE, ActionTypes.CREATE_TABLE);
        actionTypes.put(DROP_TABLE_COLUMN_TYPE, ActionTypes.DROP_TABLE_COLUMN);
        actionTypes.put(ADD_PRIMARY_KEY_TYPE, ActionTypes.ADD_PRIMARY_KEY);
        actionTypes.put(ADD_FOREIGN_KEY_TYPE, ActionTypes.ADD_FOREIGN_KEY);
        actionTypes.put(DROP_PRIMARY_KEY_TYPE, ActionTypes.DROP_PRIMARY_KEY);
        actionTypes.put(DROP_FOREIGN_KEY_TYPE, ActionTypes.DROP_FOREIGN_KEY);
        actionTypes.put(DROP_TABLE_TYPE, ActionTypes.DROP_TABLE);
        actionTypes.put(DROP_SEQUENCE_TYPE, ActionTypes.DROP_SEQUENCE);
        actionTypes.put(DELETE_BEFORE_FK_TYPE, ActionTypes.DELETE_BEFORE_FK);
        actionTypes.put(COPY_COLUMN_TYPE, ActionTypes.COPY_COLUMN);
        actionTypes.put(CLONE_COLUMN_TYPE, ActionTypes.CLONE_COLUMN);
        actionTypes.put(COPY_TABLE_TYPE, ActionTypes.COPY_TABLE);
    }

    public static void processPatch(Database database, Patch patch) {
        if (patch == null) {
            throw new NullPointerException("patch is null");
        }

        validate(database, patch);
        processTable(database, patch);
        processPrimaryKey(database, patch);
        processForeignKeys(database, patch);
        processSequences(database, patch);
        processAction(database, patch);
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
        if (value == null) {
            return defValue;
        }

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
                    throw new IllegalArgumentException(errorString);
                }
                return booleanValue;
            }
        }
        return null;
    }

    ////////////////////////

    private static void validate(Database database, Patch patch) {
        if (patch == null) {
            return;
        }

        for (Object o : patch.getActionOrCustomClassActionOrSqlAction()) {
            if (o instanceof Validator) {
                Validator obj = (Validator) o;
                String className = obj.getClazz();
                if (StringUtils.isBlank(className)) {
                    throw new DbRevisionException("Patch: " + patch.getName() + ", class name is blank");
                }

                PatchStatus status;
                try {
                    Object object = Utils.createCustomObject(className);
                    if (object == null) {
                        throw new DbRevisionException("Class '" + className + "', object is null");
                    }

                    status = ((PatchValidator) object).validate(database);
                }
                catch (Exception e) {
                    throw new DbRevisionException("Error process class '" + className + "'");
                }
                if (status!=null && status.getStatus()== PatchStatus.Status.ERROR) {
                    throw new DbRevisionException("Error process class '" + className + "'");
                }
            }
        }
    }

    private static void processTable(Database database, Patch patch) {
        if (patch == null) {
            return;
        }

        for (Object o : patch.getActionOrCustomClassActionOrSqlAction()) {
            if (o instanceof DbTable) {
                database.createTable((DbTable) o);
            }
        }
    }

    private static void processPrimaryKey(Database database, Patch patch) {
        if (patch == null) {
            return;
        }

        for (Object o : patch.getActionOrCustomClassActionOrSqlAction()) {
            if (o instanceof DbPrimaryKey) {
                DbPrimaryKey pk = (DbPrimaryKey) o;

                if (!pk.getColumns().isEmpty()) {
//                    DbSchema schema = DatabaseManager.getDbStructure(database);
//                    DbTable table = DatabaseManager.getTableFromStructure(schema, pk.getT());
                    ConstraintManager.addPk(database, pk);
                }
            }
        }
    }

    private static void processForeignKeys(Database adapter, Patch patch) {
        if (patch == null) {
            return;
        }

        List<DbForeignKey> keys = new ArrayList<DbForeignKey>();
        for (Object o : patch.getActionOrCustomClassActionOrSqlAction()) {
            if (o instanceof DbForeignKey) {
                keys.add((DbForeignKey) o);
            }
        }

        int p = 0;
        for (DbForeignKey key : keys) {
            if (StringUtils.isBlank(key.getFk())) {
                key.setFk(key.getFkTable() + p + "_fk");
            }
            ConstraintManager.createFk(adapter, key);
        }
    }

    private static void processSequences(Database db_, Patch patch) {
        if (patch == null) {
            return;
        }

        for (Object o : patch.getActionOrCustomClassActionOrSqlAction()) {
            if (o instanceof DbSequence) {
                db_.createSequence((DbSequence) o);
            }
        }
    }

    private static void processAction(Database db_, Patch patch) {
        if (patch == null) {
            return;
        }

        for (Object o : patch.getActionOrCustomClassActionOrSqlAction()) {
            ActionTypes type = actionTypes.get(o.getClass().getName());
            if (type != null) {
                switch (type) {
                    case ADD_FOREIGN_KEY: {

                    }
                    break;

                    case ADD_PRIMARY_KEY: {

                    }
                    break;

                    case ADD_TABLE_FIELD: {
                        AddTableFieldAction obj = (AddTableFieldAction) o;
                        DbField field = obj.getField();
                        int fieldType = DatabaseManager.sqlTypesMapping(field.getDbtype());
                        field.setType(fieldType);

                        try {
                            DatabaseStructureManager.addColumn(db_, obj.getTableName(), field);
                        }
                        catch (Throwable e) {
                            String s="";
                            s += "Error add column";
                            s += "  table name; " + obj.getTableName();
                            s += "  comment; "+ obj.getField().getComment();
                            s += "  dataType: "+ obj.getField().getDbtype();
                            s += "  decimalDigit: "+ obj.getField().getDigit();
                            s += "  defaultValue: "+ obj.getField().getDef();
                            s += "  JavaType; "+ obj.getField().getType();
                            s += "  name: "+ obj.getField().getName();
                            s += "  nullable: "+ obj.getField().getNullable();
                            s += "  size: "+ obj.getField().getSize();
                            throw new DbRevisionException(s, e);
                        }
                    }
                    break;

                    case CLONE_COLUMN: {

                    }
                    break;

                    case COPY_COLUMN: {

                    }
                    break;

                    case CREATE_SEQUENCE: {
                        DbSequence seq = new DbSequence();
                        if (true) {
                            throw new RuntimeException("not impleented");
                        }
/*
                                    seq.setCacheSize(getInteger(action, "sequence_cache_size", 0));
                                    seq.setInc(getInteger(action, "sequence_increment", 1));
                                    seq.setIsCycle(getBoolean(action, "sequence_is_cycle", false));
                                    seq.setIsOrder(getBoolean(action, "sequence_is_order", false));
                                    seq.setLastNumber(getLong(action, "sequence_last_number", 0));
                                    seq.setMax(getString(action, "sequence_max_value", "0"));
                                    seq.setMin(getInteger(action, "sequence_min_value", 0));
                                    seq.setT(getString(action, "sequence_name"));

                                    db_.createSequence(seq);
*/
                    }
                    break;

                    case CREATE_TABLE: {

                    }
                    break;

                    case CUSTOM_CLASS: {
                        CustomClassAction obj = (CustomClassAction) o;
                        String className = obj.getClazz();
                        if (className == null) {
                            throw new DbRevisionException("Patch - " + patch.getName() + ", action '" + CUSTOM_CLASS_ACTION_TYPE + "', class name is null");
                        }

                        try {
                            Object object = Utils.createCustomObject(className);
                            if (object == null) {
                                throw new DbRevisionException("Class '" + className + "', object is null");
                            }

                            ((PatchAction) object).process(db_);
                        }
                        catch (Exception e) {
                            throw new DbRevisionException("Error process class '" + className + "'", e);
                        }
                    }
                    break;

                    case SQL: {
                        SqlAction obj = (SqlAction) o;
                        String sql = obj.getSql();
                        Statement st = null;
                        try {
                            st = db_.getConnection().createStatement();
                            st.execute(sql);
                        }
                        catch (SQLException e) {
                            final String es = "SQL:\n" + sql;
                            throw new DbRevisionException(es, e);
                        }
                        finally {
                            DbUtils.close(st);
                            //noinspection UnusedAssignment
                            st = null;
                        }
                    }
                    break;

                    case DELETE_BEFORE_FK: {

                    }
                    break;

                    case DROP_FOREIGN_KEY: {

                    }
                    break;

                    case DROP_PRIMARY_KEY: {

                    }
                    break;

                    case DROP_TABLE: {
                        if (true) {
                            throw new RuntimeException("not impleented");
                        }
/*
                                    String nameTable = getString(action, "name_table");
                                    if (nameTable != null) {
                                        db_.dropTable(nameTable);
                                        db_.getConnection().commit();
                                    }
                                    else {
                                        log.error("Patch - " + patch.getT() + ", action '" + DROP_TABLE_TYPE + "' must have parameter 'name_table'");
                                    }
*/
                    }
                    break;

                    case DROP_TABLE_COLUMN: {

                    }
                    break;

                    case DROP_SEQUENCE: {
                        if (true) {
                            throw new RuntimeException("not impleented");
                        }
/*
                                    String nameSeq = getString(action, "name_sequence");
                                    if (nameSeq != null) {
                                        db_.dropSequence(nameSeq);
                                    }
                                    else {
                                        log.error("Patch - " + patch.getT() + ", action '" + DROP_TABLE_TYPE + "' must have parameter 'name_sequence'");
                                    }
*/
                    }
                    break;
                    default:
                        String errorString = "missed action type: " + type;
                        throw new DbRevisionException(errorString);
                }
            }
        }
    }
}
