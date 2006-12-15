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
package org.riverock.dbrevision.test;

import org.apache.log4j.Logger;

import org.riverock.dbrevision.db.DatabaseAdapter;

import org.riverock.dbrevision.db.DatabaseManager;
import org.riverock.dbrevision.db.DatabaseStructureManager;
import org.riverock.dbrevision.annotation.schema.db.DbTable;
import org.riverock.dbrevision.annotation.schema.db.DbSchema;
import org.riverock.dbrevision.annotation.schema.db.DbImportedKeyList;
import org.riverock.dbrevision.utils.StartupApplication;

/**
 * Author: mill
 * Date: Nov 28, 2002
 * Time: 3:10:19 PM
 *
 * $Id: TestForeignKeys.java 1141 2006-12-14 14:43:29Z serg_main $
 */
public class TestForeignKeys
{
    private static Logger cat = Logger.getLogger("org.riverock.test.TestForeignKeys");

    public TestForeignKeys(){}

    public static void main(String args[])
        throws Exception
    {
        StartupApplication.init();

        DatabaseAdapter db_=null;
//        db_ = DatabaseAdapter.getInstance( "ORACLE" );
        DbSchema schema = DatabaseManager.getDbStructure(db_ );
        DbTable testTable = null;

        for (DbTable table : schema.getTables()) {
            if ( "A_TEST_1".equalsIgnoreCase(table.getName()) )
            {
                testTable = table;
/*
                DbImportedPKColumn[] fkColumnList = DbService.getFkNames(table);
                System.out.println("key count: "+fkColumnList.length );
                for (int p=0; p<fkColumnList.length; p++)
                    System.out.println("key: "+fkColumnList[p].getFkName() );

                for (int p=0; p<fkColumnList.length; p++)
                {
                    DbImportedPKColumn fkColumn = fkColumnList[p];

                    String sql =
                        "alter table "+table.getName()+" "+
                        "add CONSTRAINT "+
                        (
                        fkColumn.getFkName()==null || fkColumn.getFkName().length()==0
                        ?table.getName()+p+"_fk"
                        :fkColumn.getFkName()
                        ) +
                        " PRIMARY KEY (";

                    int seq = Integer.MIN_VALUE;
                    boolean isFirst = true;
                    for ( int i=0; i<table.getImportedKeysCount();i++ )
                    {
                        DbImportedPKColumn currFkCol = table.getImportedKeys(i);
                        if (
                            (currFkCol.getPkSchemaName()==null && fkColumn.getPkSchemaName()!=currFkCol.getPkSchemaName()) ||
                            (currFkCol.getPkSchemaName()!=null && !currFkCol.getPkSchemaName().equals(fkColumn.getPkSchemaName()) ) ||

                            (currFkCol.getPkTableName()==null && fkColumn.getPkTableName()!=currFkCol.getPkTableName()) ||
                            (currFkCol.getPkTableName()!=null && !currFkCol.getPkTableName().equals(fkColumn.getPkTableName()) ) ||

                            (currFkCol.getFkSchemaName()==null && fkColumn.getFkSchemaName()!=currFkCol.getFkSchemaName()) ||
                            (currFkCol.getFkSchemaName()!=null && !currFkCol.getFkSchemaName().equals(fkColumn.getFkSchemaName()) ) ||

                            (currFkCol.getFkTableName()==null && fkColumn.getFkTableName()!=currFkCol.getFkTableName()) ||
                            (currFkCol.getFkTableName()!=null && !currFkCol.getFkTableName().equals(fkColumn.getFkTableName()) )
                        )
                            continue;

                        DbImportedPKColumn column = null;
                        int seqTemp = Integer.MAX_VALUE;
                        for ( int k=0; k<table.getImportedKeysCount(); k++ )
                        {
                            DbImportedPKColumn columnTemp = table.getImportedKeys(k);
                            if (
                                (columnTemp.getPkSchemaName()==null && fkColumn.getPkSchemaName()!=columnTemp.getPkSchemaName()) ||
                                (columnTemp.getPkSchemaName()!=null && !columnTemp.getPkSchemaName().equals(fkColumn.getPkSchemaName()) ) ||

                                (columnTemp.getPkTableName()==null && fkColumn.getPkTableName()!=columnTemp.getPkTableName()) ||
                                (columnTemp.getPkTableName()!=null && !columnTemp.getPkTableName().equals(fkColumn.getPkTableName()) ) ||

                                (columnTemp.getFkSchemaName()==null && fkColumn.getFkSchemaName()!=columnTemp.getFkSchemaName()) ||
                                (columnTemp.getFkSchemaName()!=null && !columnTemp.getFkSchemaName().equals(fkColumn.getFkSchemaName()) ) ||

                                (columnTemp.getFkTableName()==null && fkColumn.getFkTableName()!=columnTemp.getFkTableName()) ||
                                (columnTemp.getFkTableName()!=null && !columnTemp.getFkTableName().equals(fkColumn.getFkTableName()) )
                            )
                                continue;
                            if (seq < columnTemp.getKeySeq() && columnTemp.getKeySeq() < seqTemp )
                            {
                                seqTemp = columnTemp.getKeySeq();
                                column = columnTemp;
                            }
                        }
                        seq = column.getKeySeq();

                        if (!isFirst)
                            sql += ",";
                        else
                            isFirst = !isFirst;

                        sql += column.getFkColumnName();
                    }
                    sql += ")\n";
                    System.out.println( sql );
                }
*/
            }
        }

        DbImportedKeyList fk = new DbImportedKeyList();
        fk.getKeys().addAll( testTable.getImportedKeys() );
        DatabaseStructureManager.createForeignKey(db_, fk );

    }
}
