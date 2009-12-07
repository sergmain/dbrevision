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

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import org.riverock.dbrevision.db.impl.HyperSonicDatabase;
import org.riverock.dbrevision.db.impl.DB2Database;
import org.riverock.dbrevision.db.impl.SqlServerDatabase;
import org.riverock.dbrevision.db.impl.MySqlDatabase;
import org.riverock.dbrevision.db.impl.OracleDatabase;
import org.riverock.dbrevision.db.impl.PostgreeSqlDatabase;
import org.riverock.dbrevision.db.impl.MaxDBDatabase;
import org.riverock.dbrevision.exception.DbRevisionException;

/**
 * @author Sergei Maslyukov
 *         Date: 04.07.2006
 *         Time: 12:03:41
 */
public class DatabaseFactory {
    private final static Logger log = Logger.getLogger(DatabaseFactory.class);

    private static Map<Database.Family, Class> familyClassMap = new HashMap<Database.Family, Class>();
    private static Map<String, Database.Family> familyCodeMap = new HashMap<String, Database.Family>();

    public static final String ORACLE_FAMILY = "oracle";
    public static final String MYSQL_FAMILY = "mysql";
    public static final String HYPERSONIC_FAMILY = "hypersonic";
    public static final String SQLSERVER_FAMILY = "sqlserver";
    public static final String POSTGREES_FAMILY = "postgrees";
    public static final String DB2_FAMILY = "db2";
    public static final String MAXDB_FAMILY = "maxdb";

    static {
        familyClassMap.put(Database.Family.ORACLE, OracleDatabase.class);
        familyClassMap.put(Database.Family.MYSQL, MySqlDatabase.class);
        familyClassMap.put(Database.Family.HYPERSONIC, HyperSonicDatabase.class);
        familyClassMap.put(Database.Family.SQLSERVER, SqlServerDatabase.class);
        familyClassMap.put(Database.Family.POSTGREES, PostgreeSqlDatabase.class);
        familyClassMap.put(Database.Family.DB2, DB2Database.class);
        familyClassMap.put(Database.Family.MAXDB, MaxDBDatabase.class);
    }

    static {
        familyCodeMap.put(ORACLE_FAMILY, Database.Family.ORACLE);
        familyCodeMap.put(MYSQL_FAMILY, Database.Family.MYSQL);
        familyCodeMap.put(HYPERSONIC_FAMILY, Database.Family.HYPERSONIC);
        familyCodeMap.put(SQLSERVER_FAMILY, Database.Family.SQLSERVER);
        familyCodeMap.put(POSTGREES_FAMILY, Database.Family.POSTGREES);
        familyCodeMap.put(DB2_FAMILY, Database.Family.DB2);
        familyCodeMap.put(MAXDB_FAMILY, Database.Family.MAXDB);
    }

    public static List<String> getSupportedFamilyCode() {
        return new ArrayList<String>(familyCodeMap.keySet());
    }

    public static Database.Family decodeFamily(String familyCode) {
        return familyCodeMap.get(familyCode);
    }

    public static Database getInstance(final Connection connection, String familyCode) {
        return getInstance(connection, decodeFamily(familyCode));
    }

    public static Database getInstance(final Connection connection, Database.Family family) {
        if (connection == null) {
            String es = "Connection is null.";
            log.fatal(es);
            throw new DbRevisionException(es);
        }
        if (family == null) {
            String es = "dbFamily not defined.";
            log.fatal(es);
            throw new DbRevisionException(es);
        }

        if (log.isDebugEnabled()) {
            log.debug("dc.getFamily(): " + family);
        }
        Class clazz = familyClassMap.get(family);
        if (log.isDebugEnabled()) {
            log.debug("clazz: " + clazz);
        }

        if (clazz == null) {
            throw new IllegalStateException("Database for family '"+family+"' not found");
        }

        try {
            Constructor constructor = clazz.getConstructor(Connection.class);
            Database db = (Database)constructor.newInstance(connection);

            if (log.isDebugEnabled()) {
                log.debug("Success create dynamic object: " + db);
            }
            return db;
        }
        catch (Exception e) {
            log.fatal("Error create instance for family " + family);
            log.fatal("Error:", e);

            final String es = "Error create Database instance.";
            System.out.println(es);
            throw new DbRevisionException(es, e);
        }
    }
}
