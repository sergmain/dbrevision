package org.riverock.dbrevision.test.db;

import org.riverock.dbrevision.offline.StartupApplication;
import org.riverock.dbrevision.offline.DbRevisionConfig;
import org.riverock.dbrevision.db.DatabaseAdapter;
import org.riverock.dbrevision.db.DatabaseManager;
import org.riverock.dbrevision.db.factory.MYSQLconnect;
import org.apache.commons.lang.CharEncoding;

import java.sql.*;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

/**
 * User: SMaslyukov
 * Date: 22.02.2007
 * Time: 17:17:22
 */
public class FixUTF8Issue {

    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        System.out.println("FixUTF8Issue <[<driver> <url> <login> <pass>] | [none of parameters]>");

        String url = "jdbc:mysql://localhost/test?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull";
        String username = "root";
        String password = "qqq";

        Connection conn;
        if (args!=null && args.length==4) {
            System.out.println("User's invoke\ndriver: "+args[0]+"\nurl: "+args[1]+"\nlogin: " +args[2]+"\npass: "+args[3]);
            Class.forName(args[0]);
            conn = DriverManager.getConnection(args[1], args[2], args[3]);
        }
        else {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, username, password);
        }
        conn.setAutoCommit(false);


        StartupApplication.init();
        System.out.println("DebugDir: " + DbRevisionConfig.getGenericDebugDir());

        DatabaseAdapter db = new MYSQLconnect(conn);

//        processCSS(db);
        processArticle(db);
        processNews(db);

        db.getConnection().commit();
    }

    private static void processCSS(DatabaseAdapter db) throws SQLException {
        ResultSet rs = null;
        PreparedStatement ps = null;

        String cssSql =
                "select * from wm_portal_css a " +
                "where exists " +
                "(select null from wm_portal_css_data b where a.ID_SITE_CONTENT_CSS = b.ID_SITE_CONTENT_CSS)";

        rs = db.getConnection().createStatement().executeQuery(cssSql);
        while (rs.next()) {
            Long cssId = rs.getLong("ID_SITE_CONTENT_CSS");
//            DatabaseManager.getBigTextField(db, cssId, )
        }
    }


    private static void processArticle(DatabaseAdapter db) throws SQLException, UnsupportedEncodingException {
        ResultSet rs = null;

        String cssSql =
                "select * from wm_portlet_article a " +
                "where exists " +
                "(select null from wm_portlet_article_data b where a.ID_SITE_CTX_ARTICLE = b.ID_SITE_CTX_ARTICLE)";

        Statement st = db.getConnection().createStatement();
        rs = st.executeQuery(cssSql);
        while (rs.next()) {
            Long articleId = rs.getLong("ID_SITE_CTX_ARTICLE");
            String text = initArticleText(db, articleId);
            updateArticleBlob(db, articleId, text);
        }
        rs.close();
        st.close();
    }

    private static void processNews(DatabaseAdapter db) throws SQLException, UnsupportedEncodingException {
        ResultSet rs = null;

        String cssSql =
                "select * from wm_news_item a " +
                "where exists " +
                "(select null from wm_news_item_text b where a.ID = b.ID)";

        Statement st = db.getConnection().createStatement();
        rs = st.executeQuery(cssSql);
        while (rs.next()) {
            Long newsId = rs.getLong("ID");
            String text = initNewsText(db, newsId);
            updateNewsBlob(db, newsId, text);
        }
        rs.close();
        st.close();
    }

    private static void updateArticleBlob(DatabaseAdapter db, Long articleId, String articleText) throws SQLException, UnsupportedEncodingException {
        PreparedStatement ps = null;

        String sql =
                "update wm_portlet_article " +
                "set ARTICLE_BLOB=? " +
                "where ID_SITE_CTX_ARTICLE=? ";

        ps = db.getConnection().prepareStatement(sql);
        bindBlob(db, articleText, ps, 1);
        ps.setLong(2, articleId);
        ps.executeUpdate();
        ps.close();
    }

    private static void updateNewsBlob(DatabaseAdapter db, Long articleId, String newsText) throws SQLException, UnsupportedEncodingException {
        PreparedStatement ps = null;

        String sql =
                "update wm_news_item " +
                "set NEWS_BLOB=? " +
                "where ID=? ";

        ps = db.getConnection().prepareStatement(sql);
        bindBlob(db, newsText, ps, 1);
        ps.setLong(2, articleId);
        ps.executeUpdate();
        ps.close();
    }

    private static void bindBlob(DatabaseAdapter db, String articleText, PreparedStatement ps, int idx) throws UnsupportedEncodingException, SQLException {
        if (db.getFamily()== DatabaseManager.MYSQL_FAMALY) {
            byte[] bytes = articleText.getBytes(CharEncoding.UTF_8);

            byte[] fileBytes = new byte[]{};
            if (bytes!=null) {
                fileBytes = bytes;
            }
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileBytes);
            ps.setBinaryStream(idx, byteArrayInputStream, fileBytes.length);

            bytes = null;
            byteArrayInputStream = null;
            fileBytes = null;
        }
        else {
            throw new RuntimeException("Not implemented");
        }
    }

    private static String initNewsText(DatabaseAdapter db, Long newsId) throws SQLException {
        if (newsId==null) {
            return "";
        }

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            ps = db.getConnection().prepareStatement(
                    "select  TEXT " +
                            "from    WM_NEWS_ITEM_TEXT " +
                            "where   ID=? " +
                            "order by ID_MAIN_NEWS_TEXT ASC"
            );
            setLong(ps, 1, newsId );

            rs = ps.executeQuery();

            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append( getString(rs, "TEXT") );
            }
            return sb.toString();
        }
        finally {
            DatabaseManager.close(rs, ps);
            rs = null;
            ps = null;
        }
    }

    private static String initArticleText(DatabaseAdapter db_, Long articleId) throws SQLException {
        if (articleId==null) {
            return "";
        }

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = db_.getConnection().prepareStatement(
                    "select  ARTICLE_DATA " +
                            "from    WM_PORTLET_ARTICLE_DATA " +
                            "where   ID_SITE_CTX_ARTICLE=? " +
                            "order by ID_SITE_CTX_ARTICLE_DATA ASC"
            );
            setLong(ps, 1, articleId );

            rs = ps.executeQuery();

            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append( getString(rs, "ARTICLE_DATA") );
            }
            return sb.toString();
        }
        finally {
            DatabaseManager.close(rs, ps);
            rs=null;
            ps=null;
        }
    }

    public static void setLong(final PreparedStatement ps, final int index, final Long data)
        throws SQLException {
        if (data != null)
            ps.setLong(index, data);
        else
            ps.setNull(index, Types.NUMERIC);
    }

    /**
     * ResultSet rs - ResultSet
     * String f - name of field
     */
    public static String getString(final ResultSet rs, final String f)
        throws SQLException {
        return getString(rs, f, null);
    }

    /**
     * ResultSet rs - ResultSet
     * String f - name of field
     * String def - default value
     */
    public static String getString( final ResultSet rs, final String f, final String def )
        throws SQLException
    {
        if ( rs==null || f==null )
            return def;

        {
            Object obj = rs.getObject(f);
            if (rs.wasNull())
                return def;

            return obj.toString();
        }
    }
}
