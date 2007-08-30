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
