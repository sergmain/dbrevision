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

import org.riverock.dbrevision.db.Database;
import org.riverock.dbrevision.db.DatabaseFactory;
import org.riverock.dbrevision.db.DatabaseStructureManager;
import org.riverock.dbrevision.schema.db.v3.DbTable;
import org.riverock.dbrevision.Constants;

import java.sql.*;
import java.util.List;

/**
 * User: SergeMaslyukov
 * Date: 29.08.2007
 * Time: 23:55:28
 */
public class TestConnectionProvider {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        String url = "jdbc:hsqldb:hsql://localhost:9001";

        Class.forName("org.hsqldb.jdbcDriver");

        Connection conn = DriverManager.getConnection(url, "sa", null);

        Database database = DatabaseFactory.getInstance(conn, Database.Family.HYPERSONIC);

        DatabaseMetaData metaData = database.getConnection().getMetaData();
//        String dbSchema = metaData.getUserName();
        String dbSchema = "";
        List<DbTable> list = DatabaseStructureManager.getTableList(database, dbSchema, Constants.DB_REVISION_TABLE_NAME);

        System.out.println("list = " + list);
    }
}
