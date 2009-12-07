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

package org.riverock.dbrevision.trash;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * User: SMaslyukov
 * Date: 30.08.2007
 * Time: 12:59:28
 */
public class HypersonicSchemaTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        Class.forName("org.hsqldb.jdbcDriver");
        String url = "jdbc:hsqldb:hsql://localhost";

        Connection conn = DriverManager.getConnection(url, "sa", null);


        DatabaseMetaData metaData = conn.getMetaData();
        String dbSchema = metaData.getUserName();

        System.out.println("dbSchema = " + dbSchema);
        
        ResultSet rs = metaData.getSchemas();
        while(rs.next()) {
            String schema = rs.getString("TABLE_SCHEM");
            String catalog = rs.getString("TABLE_CATALOG");
            System.out.println("schema = " + schema+", catalog = " + catalog);
/*
            ResultSetMetaData meta = rs.getMetaData();
            int count = meta.getColumnCount();
            for (int i=0; i<count; i++) {
                Object obj = rs.getObject(i);
                System.out.println("obj = " + obj);
            }
*/
        }
        String term = metaData.getSchemaTerm();
        System.out.println("term = " + term);

    }
}
