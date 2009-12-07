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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.sql.Connection;

import org.apache.log4j.Logger;

/**
 * @author Sergei Maslyukov
 *         Date: 14.12.2006
 *         Time: 17:10:38
 *         <p/>
 *         $Id$
 */
public class DbUtils {
    private final static Logger log = Logger.getLogger(DbUtils.class);

    /**
     * Get result of query as Long
     *
     * @param conn   db connection
     * @param sql    query string
     * @param params query parameters
     * @param types  parameters types
     * @return Long result
     * @throws SQLException on error
     */
    @SuppressWarnings({"UnusedAssignment"})
    public static Long getLongValue(final Connection conn, final String sql, final Object[] params, final int[] types)
        throws SQLException {
        Statement stmt = null;
        PreparedStatement pstm;
        ResultSet rs = null;

        try {
            if (params == null) {
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);
            }
            else {
                pstm = conn.prepareStatement(sql);
                for (int i = 0; i < params.length; i++) {
                    if (types == null) {
                        pstm.setObject(i + 1, params[i]);
                    }
                    else {
                        pstm.setObject(i + 1, params[i], types[i]);
                    }
                }

                rs = pstm.executeQuery();
                stmt = pstm;
            }

            if (rs.next()) {
                long tempLong = rs.getLong(1);
                if (rs.wasNull()) {
                    return null;
                }

                return tempLong;
            }
            return null;
        }
        catch (SQLException e) {
            log.error("error getting long value from sql:\n" + sql, e);
            throw e;
        }
        finally {
            close(rs, stmt);
            rs = null;
            stmt = null;
            pstm = null;
        }
    }

    /**
     * @param conn connection
     * @param query sql
     * @param params parameters
     * @param types types of parameters
     * @return int count of updated records
     * @throws SQLException on error
     */
    public static int runSQL(final Connection conn, final String query, final Object[] params, final int[] types)
        throws SQLException {
        int n = 0;
        Statement stmt = null;
        PreparedStatement pstm = null;

        try {
            if (params == null) {
                stmt = conn.createStatement();
                n = stmt.executeUpdate(query);
            } else {
                pstm = conn.prepareStatement(query);
                for (int i = 0; i < params.length; i++) {
                    if (params[i] != null)
                        pstm.setObject(i + 1, params[i], types[i]);
                    else
                        pstm.setNull(i + 1, types[i]);
                }

                n = pstm.executeUpdate();
                stmt = pstm;
            }
        }
        catch (SQLException e) {
            log.error("SQL query:\n" + query);
            try {
                if (params != null) {
                    for (int ii = 0; ii < params.length; ii++)
                        log.error("parameter #" + (ii + 1) + ": " + (params[ii] != null ? params[ii].toString() : null));
                }
            }
            catch (Throwable e1) {
                log.error("Error while output parameters: " + e1.toString());
            }
            log.error("SQLException", e);
            throw e;
        }
        finally {
            close(stmt);
            stmt = null;
            pstm = null;
        }
        return n;
    }

    public static void setLong(final PreparedStatement ps, final int index, final Long data)
        throws SQLException {
        if (data != null) {
            ps.setLong(index, data);
        }
        else {
            ps.setNull(index, Types.NUMERIC);
        }
    }

    /**
     * String f - name of field
     * 
     * @param rs ResultSet
     * @param f name of field
     * @return value as Long
     * @throws java.sql.SQLException on error
     */
    public static Boolean getBoolean(final ResultSet rs, final String f) throws SQLException {
        return getBoolean(rs, f, null);
    }

    /**
     * @param rs - ResultSet
     * @param f - name of field
     * @param def - default value
     * @return value of column as Long
     * @throws java.sql.SQLException on error
     */
    public static Boolean getBoolean(final ResultSet rs, final String f, final Boolean def) throws SQLException {

        if (rs == null || f == null) {
            return def;
        }

        try {
            boolean temp = rs.getBoolean(f);
            if (rs.wasNull()) {
                return def;
            }

            return temp;
        }
        catch (SQLException exc) {
            log.error("Error get Boolean field '" + f + "'", exc);
            throw exc;
        }
    }

    /**
     * String f - name of field
     *
     * @param rs ResultSet
     * @param f name of field
     * @return value as Long
     * @throws java.sql.SQLException on error
     */
    public static Long getLong(final ResultSet rs, final String f)
        throws SQLException {
        return getLong(rs, f, null);
    }

    /**
     * @param rs - ResultSet
     * @param f - name of field
     * @param def - default value
     * @return value of column as Long
     * @throws java.sql.SQLException on error
     */
    public static Long getLong(final ResultSet rs, final String f, final Long def)
        throws SQLException {

        if (rs == null || f == null) {
            return def;
        }

        try {
            long temp = rs.getLong(f);
            if (rs.wasNull()) {
                return def;
            }

            return temp;
        }
        catch (SQLException exc) {
            log.error("Error get Long field '" + f + "'", exc);
            throw exc;
        }
    }

    /**
     * ResultSet rs - ResultSet
     * String f - name of field
     */
    public static Integer getInteger(final ResultSet rs, final String f)
        throws SQLException {
        return getInteger(rs, f, null);
    }

    /**
     * ResultSet rs - ResultSet
     * String f - name of field
     * int def - default value
     */
    public static Integer getInteger(final ResultSet rs, final String f, final Integer def)
        throws SQLException {
        if (rs == null || f == null) {
            return def;
        }

        try {
            int temp = rs.getInt(f);
            if (rs.wasNull()) {
                return def;
            }

            return temp;
        }
        catch (SQLException exc) {
            log.error("Error get Integer field '" + f + "' from ResultSet", exc);
            throw exc;
        }
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
    public static String getString(final ResultSet rs, final String f, final String def)
        throws SQLException {
        if (rs == null || f == null) {
            return def;
        }

        try {
            Object obj = rs.getObject(f);
            if (rs.wasNull()) {
                return def;
            }

            return obj.toString();
        }
        catch (SQLException exc) {
            log.error("Error get String field '" + f + "' from ResultSet, sql error code ", exc);
            throw exc;
        }
    }

    public static void close(final ResultSet rs, final Statement st) {
        if (rs != null) {
            try {
                rs.close();
            }
            catch (Exception e01) {
                // catch SQLException
            }
        }
        if (st != null) {
            try {
                st.close();
            }
            catch (Exception e02) {
                // catch SQLException
            }
        }
    }

    public static void close(final Statement st) {
        if (st != null) {
            try {
                st.close();
            }
            catch (SQLException e201) {
                // catch SQLException
            }
        }
    }

    public static void close(final Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            }
            catch (SQLException e201) {
                // catch SQLException
            }
            try {
                conn.close();
            }
            catch (SQLException e201) {
                // catch SQLException
            }
        }
    }
}
