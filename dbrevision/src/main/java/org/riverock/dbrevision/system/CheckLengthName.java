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
package org.riverock.dbrevision.system;

import java.io.FileInputStream;

import org.riverock.dbrevision.annotation.schema.db.DbSchema;
import org.riverock.dbrevision.annotation.schema.db.DbTable;
import org.riverock.dbrevision.annotation.schema.db.DbView;
import org.riverock.dbrevision.config.GenericConfig;
import org.riverock.dbrevision.utils.Utils;
import org.riverock.dbrevision.utils.StartupApplication;

/**
 * Author: mill
 * Date: Nov 28, 2002
 * Time: 3:10:19 PM
 *
 * $Id: CheckLengthName.java 1141 2006-12-14 14:43:29Z serg_main $
 */
/**
 * ѕроверка имен таблиц и view на превышение 18 символов. ƒанное ограничение есть в IBM DB2,
 * в остальных базах длина имен таблиц и view может быть больше
 */
public class CheckLengthName {

    public CheckLengthName() {
    }

    public static void main(String args[]) throws Exception {
        StartupApplication.init();

        System.out.println("Unmarshal data from file");
        FileInputStream stream = new FileInputStream(GenericConfig.getGenericDebugDir() + "webmill-schema.xml");
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
