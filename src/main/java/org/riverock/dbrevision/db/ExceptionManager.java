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

package org.riverock.dbrevision.db;

import java.sql.SQLException;

/**
 * User: SergeMaslyukov
 * Date: 17.03.2009
 * Time: 14:50:26
 */
public class ExceptionManager {

    public static boolean isConstraintNonExist(Database.Family family, Throwable e) {
        switch (family) {
            case ORACLE:
                return e != null && (e instanceof SQLException) && ((((SQLException)e).getErrorCode()==2443));
        }
        return false;
    }

    public static boolean isIndexUniqueKey(Database.Family family, Throwable e) {
        switch (family) {
            case DB2:
                break;
            case HYPERSONIC:
                break;
            case INTERBASE:
                break;
            case MAXDB:
                break;
            case MYSQL:
                break;
            case ORACLE:
                break;
            case POSTGREES:
                break;
            case SQLSERVER:
                break;
        }
        return e != null && (e instanceof SQLException) && ((e.toString().indexOf("ORA-00001") != -1));
    }

    public static boolean isTableExists(Database.Family family, Throwable e) {
        return e != null && (e instanceof SQLException) && (e.toString().indexOf("ORA-00955") != -1);
    }

    public static boolean isViewExists(Database.Family family, Throwable e) {
        return e != null && (e instanceof SQLException) && (e.toString().indexOf("ORA-00955") != -1);
    }

    public static boolean isSequenceExists(Database.Family family, Throwable e) {
        return e != null && (e instanceof SQLException) && (e.toString().indexOf("ORA-00955") != -1);
    }

    public static boolean isConstraintExists(Database.Family family, Throwable e) {
        return e != null && (e instanceof SQLException) && (e.toString().indexOf("ORA-02275") != -1);
    }

}
