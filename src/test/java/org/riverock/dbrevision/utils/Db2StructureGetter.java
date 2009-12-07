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

package org.riverock.dbrevision.utils;

import org.riverock.dbrevision.annotation.schema.db.DbSchema;
import org.riverock.dbrevision.db.Database;
import org.riverock.dbrevision.db.DatabaseFactory;
import org.riverock.dbrevision.db.DatabaseManager;

import java.sql.*;
import java.util.Properties;

/**
 * User: SergeMaslyukov
 * Date: 29.08.2007
 * Time: 23:55:28
 */
public class Db2StructureGetter {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        if (args.length <2) {
            throw new IllegalArgumentException("Need args");
        }

        Class.forName("com.ibm.db2.jcc.DB2Driver");
        String url = "jdbc:db2://10.0.0.7:50000/LSAI";

        Properties p = new Properties();
        p.setProperty("user", args[0]);
        p.setProperty("password", args[1]);

        Connection conn = DriverManager.getConnection(url, p);
        conn.setAutoCommit(false);

        Database database = DatabaseFactory.getInstance(conn, Database.Family.DB2);

        DatabaseMetaData metaData = database.getConnection().getMetaData();
        ResultSet rs = metaData.getSchemas();
        while(rs.next()) {
            String schema = rs.getString("TABLE_SCHEM");
            String cat = rs.getString("TABLE_CATALOG");
            System.out.println("schema = " + schema);
            System.out.println("cat = " + cat);
        }

        DbSchema schema = DatabaseManager.getDbStructure(database, "SAI");

        System.out.println("list = " + schema.getTables().size());
    }
}