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

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import org.riverock.dbrevision.db.factory.HyperSonicAdapter;
import org.riverock.dbrevision.db.factory.DB2Adapter;
import org.riverock.dbrevision.db.factory.SqlServerAdapter;
import org.riverock.dbrevision.db.factory.MySqlAdapter;
import org.riverock.dbrevision.db.factory.OracleAdapter;
import org.riverock.dbrevision.db.factory.PostgreeSqlAdapter;
import org.riverock.dbrevision.db.factory.MaxDBAdapter;
import org.riverock.dbrevision.exception.DbRevisionException;

/**
 * @author Sergei Maslyukov
 *         Date: 04.07.2006
 *         Time: 12:03:41
 */
public class DatabaseAdapterProvider {
    private final static Logger log = Logger.getLogger(DatabaseAdapterProvider.class);

    private static Map<DatabaseAdapter.Family, Class> familyClassMap = new HashMap<DatabaseAdapter.Family, Class>();
    private static Map<String, DatabaseAdapter.Family> familyCodeMap = new HashMap<String, DatabaseAdapter.Family>();

    public static final String ORACLE_FAMILY = "oracle";
    public static final String MYSQL_FAMILY = "mysql";
    public static final String HYPERSONIC_FAMILY = "hypersonic";
    public static final String SQLSERVER_FAMILY = "sqlserver";
    public static final String POSTGREES_FAMILY = "postgrees";
    public static final String DB2_FAMILY = "db2";
    public static final String MAXDB_FAMILY = "maxdb";

    static {
        familyClassMap.put(DatabaseAdapter.Family.ORACLE, OracleAdapter.class);
        familyClassMap.put(DatabaseAdapter.Family.MYSQL, MySqlAdapter.class);
        familyClassMap.put(DatabaseAdapter.Family.HYPERSONIC, HyperSonicAdapter.class);
        familyClassMap.put(DatabaseAdapter.Family.SQLSERVER, SqlServerAdapter.class);
        familyClassMap.put(DatabaseAdapter.Family.POSTGREES, PostgreeSqlAdapter.class);
        familyClassMap.put(DatabaseAdapter.Family.DB2, DB2Adapter.class);
        familyClassMap.put(DatabaseAdapter.Family.MAXDB, MaxDBAdapter.class);
    }

    static {
        familyCodeMap.put(ORACLE_FAMILY, DatabaseAdapter.Family.ORACLE);
        familyCodeMap.put(MYSQL_FAMILY, DatabaseAdapter.Family.MYSQL);
        familyCodeMap.put(HYPERSONIC_FAMILY, DatabaseAdapter.Family.HYPERSONIC);
        familyCodeMap.put(SQLSERVER_FAMILY, DatabaseAdapter.Family.SQLSERVER);
        familyCodeMap.put(POSTGREES_FAMILY, DatabaseAdapter.Family.POSTGREES);
        familyCodeMap.put(DB2_FAMILY, DatabaseAdapter.Family.DB2);
        familyCodeMap.put(MAXDB_FAMILY, DatabaseAdapter.Family.MAXDB);
    }

    public static List<String> getSupportedFamilyCode() {
        return new ArrayList<String>(familyCodeMap.keySet());
    }

    public static DatabaseAdapter.Family decodeFamily(String familyCode) {
        return familyCodeMap.get(familyCode);
    }

    public static DatabaseAdapter getInstance(final Connection connection, String familyCode) {
        return getInstance(connection, decodeFamily(familyCode));
    }

    public static DatabaseAdapter getInstance(final Connection connection, DatabaseAdapter.Family family) {
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
            throw new IllegalStateException("DatabaseAdapter for family '"+family+"' not found");
        }

        try {
            Constructor constructor = clazz.getConstructor(Connection.class);
            DatabaseAdapter db = (DatabaseAdapter)constructor.newInstance(connection);

            if (log.isDebugEnabled()) {
                log.debug("Success create dynamic object: " + db);
            }
            return db;
        }
        catch (Exception e) {
            log.fatal("Error create instance for family " + family);
            log.fatal("Error:", e);

            final String es = "Error create DatabaseAdapter instance.";
            System.out.println(es);
            throw new DbRevisionException(es, e);
        }
    }
}
