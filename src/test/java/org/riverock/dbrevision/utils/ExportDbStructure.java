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
import org.riverock.dbrevision.system.DbStructureExport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.io.FileOutputStream;

/**
 * User: SergeMaslyukov
 * Date: 12.12.2009
 * Time: 22:29:00
 */
public class ExportDbStructure {

    public static void main(String[] args) throws Exception {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection conn = DriverManager.getConnection(
            "jdbc:oracle:thin:@dbserver:1521:mill",
            "qqq", "www"
        );
        conn.setAutoCommit(false);

        Database database = DatabaseFactory.getInstance(conn, Database.Family.ORACLE);
        FileOutputStream fos = new FileOutputStream("budget1.xml");
        DbStructureExport.export(database, "QQQ", fos, true);
        fos.flush();
        fos.close();
    }
}
