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
package org.riverock.dbrevision.system;

import java.io.FileInputStream;

import org.riverock.dbrevision.annotation.schema.db.DbSchema;
import org.riverock.dbrevision.annotation.schema.db.DbTable;
import org.riverock.dbrevision.annotation.schema.db.DbView;
import org.riverock.dbrevision.utils.Utils;

/**
 * Author: mill
 * Date: Nov 28, 2002
 * Time: 3:10:19 PM
 *
 * $Id: CheckLengthName.java 1141 2006-12-14 14:43:29Z serg_main $
 */
/**
 * �������� ���� ������ � view �� ���������� 18 ��������. ������ ����������� ���� � IBM DB2,
 * � ��������� ����� ����� ���� ������ � view ����� ���� ������
 */
public class CheckLengthName {

    public CheckLengthName() {
    }

    public static void main(String args[]) throws Exception {

        System.out.println("Unmarshal data from file");
        FileInputStream stream = new FileInputStream("webmill-schema.xml");
        DbSchema millSchema = Utils.getObjectFromXml(DbSchema.class, stream);

        for (DbTable table : millSchema.getTables()) {
            if (table.getName().length() > 18)
                System.out.println("Name of table '" + table.getName() +
                    "' is wrong. Exceed " + (table.getName().length() - 18) + " of chars");
        }

        for (DbView view : millSchema.getViews()) {
            if (view.getName().length() > 18)
                System.out.println("Name of view '" + view.getName() +
                    "' is wrong. Exceed " + (view.getName().length() - 18) + " of chars");
        }
    }
}
