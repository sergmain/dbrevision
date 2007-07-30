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
package org.riverock.dbrevision.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.apache.log4j.Logger;

/**
 * @author Sergei Maslyukov
 *         Date: 14.12.2006
 *         Time: 17:10:38
 *         <p/>
 *         $Id$
 */
public class DbUtils {
    private final static Logger log = Logger.getLogger( DbUtils.class );

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
    public static Long getLong(final ResultSet rs, final String f)
        throws SQLException {
        return getLong(rs, f, null);
    }

    /**
     * ResultSet rs - ResultSet
     * String f - name of field
     * Long def - default value
     */
    public static Long getLong(final ResultSet rs, final String f, final Long def)
        throws SQLException {

        if (rs == null || f == null)
            return def;

        try {
            long temp = rs.getLong(f);
            if (rs.wasNull())
                return def;

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
        if (rs == null || f == null)
            return def;

        try {
            int temp = rs.getInt(f);
            if (rs.wasNull())
                return def;

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
    public static String getString( final ResultSet rs, final String f, final String def )
        throws SQLException
    {
        if ( rs==null || f==null )
            return def;

        try {
            Object obj = rs.getObject(f);
            if (rs.wasNull())
                return def;

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
}
