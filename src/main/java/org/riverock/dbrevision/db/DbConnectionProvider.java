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

import org.apache.log4j.Logger;

import org.riverock.dbrevision.db.factory.HyperSonicAdapter;
import org.riverock.dbrevision.db.factory.DB2Adapter;
import org.riverock.dbrevision.db.factory.SqlServerAdapter;
import org.riverock.dbrevision.db.factory.MySqlAdapter;
import org.riverock.dbrevision.db.factory.OracleAdapter;
import org.riverock.dbrevision.db.factory.PostgreeSqlAdapter;
import org.riverock.dbrevision.db.factory.MaxDBAdapter;

/**
 * @author Sergei Maslyukov
 *         Date: 04.07.2006
 *         Time: 12:03:41
 */
public class DbConnectionProvider {
    private final static Logger log = Logger.getLogger(DbConnectionProvider.class);

    private static Map<String, Class> familyClassMap = new HashMap<String, Class>();
    public static final String ORACLE_FAMILY = "oracle";
    public static final String MYSQL_FAMILY = "mysql";
    public static final String HSQLDB_FAMILY = "hsqldb";
    public static final String MSSQL_FAMILY = "sqlserver";
    public static final String POSTGREES_FAMILY = "postgrees";
    public static final String DB2_FAMILY = "db2";
    public static final String MAXDB_FAMILY = "maxdb";

    static {
        familyClassMap.put(ORACLE_FAMILY, OracleAdapter.class);
        familyClassMap.put(MYSQL_FAMILY, MySqlAdapter.class);
        familyClassMap.put(HSQLDB_FAMILY, HyperSonicAdapter.class);
        familyClassMap.put(MSSQL_FAMILY, SqlServerAdapter.class);
        familyClassMap.put(POSTGREES_FAMILY, PostgreeSqlAdapter.class);
        familyClassMap.put(DB2_FAMILY, DB2Adapter.class);
        familyClassMap.put(MAXDB_FAMILY, MaxDBAdapter.class);
    }

    public static DatabaseAdapter openConnect(final Connection connection, String family)
        throws DatabaseException {
        if (connection == null) {
            String es = "Connection is null.";
            log.fatal(es);
            throw new DatabaseException(es);
        }
        if (family == null) {
            String es = "dbFamily not defined.";
            log.fatal(es);
            throw new DatabaseException(es);
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

            final String es = "Error create DatabaseAdapter instance. See log for details";
            System.out.println(es);
            throw new DatabaseException(es, e);
        }
    }
}
