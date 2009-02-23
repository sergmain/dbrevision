package org.riverock.dbrevision.utils;

import org.riverock.dbrevision.db.Database;
import org.riverock.dbrevision.db.DatabaseFactory;
import org.riverock.dbrevision.db.DatabaseStructureManager;
import org.riverock.dbrevision.annotation.schema.db.DbTable;
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
