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

import org.riverock.dbrevision.schema.db.DbSchema;
import org.riverock.dbrevision.utils.Utils;

import javax.xml.bind.JAXBException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * User: SergeMaslyukov
 * Date: 30.08.2007
 * Time: 23:44:22
 */
public class ExportInitStructureTest {
    public static void main(String[] args) throws FileNotFoundException, JAXBException {
        System.out.println("args[0] = " + args[0]);
        FileInputStream stream = new FileInputStream(args[0]);
        DbSchema millSchema = Utils.getObjectFromXml(DbSchema.class, stream);

        System.out.println("Done");

    }
}
