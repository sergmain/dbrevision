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

/**
 * User: SergeMaslyukov
 * Date: 17.03.2009
 * Time: 14:41:33
 */
public class DatabaseMetadata {

    public static int getMaxLengthStringField() {
        throw new RuntimeException("Not implemented");
    }

    public static String getDefaultTimestampValue(Database.Family family) {
        throw new RuntimeException("Not implemented");
    }

    public static String getOnDeleteSetNull(Database.Family family) {
        throw new RuntimeException("Not implemented");
    }

    public static boolean isBatchUpdate(Database.Family family) {
        throw new RuntimeException("Not implemented");
    }

    public static boolean isNeedUpdateBracket(Database.Family family) {
        throw new RuntimeException("Not implemented");

    }

    public static boolean isByteArrayInUtf8(Database.Family family) {
        throw new RuntimeException("Not implemented");

    }

    public static boolean isSchemaSupports(Database.Family family) {
        throw new RuntimeException("Not implemented");

    }

    public static boolean isForeignKeyControlSupports(Database.Family family) {
        throw new RuntimeException("Not implemented");
        
    }

    public static int getMaxLengthStringField(Database.Family family) {
        throw new RuntimeException("Not implemented");
    }

}
