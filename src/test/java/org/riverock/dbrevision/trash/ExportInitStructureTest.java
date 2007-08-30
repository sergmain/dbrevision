package org.riverock.dbrevision.trash;

import org.riverock.dbrevision.annotation.schema.db.DbSchema;
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
