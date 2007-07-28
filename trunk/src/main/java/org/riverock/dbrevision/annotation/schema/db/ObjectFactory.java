//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.06.27 at 04:14:20 PM MSD 
//


package org.riverock.dbrevision.annotation.schema.db;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.riverock.dbrevision.annotation.schema.db package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _FieldData_QNAME = new QName("", "FieldData");
    private final static QName _DefinitionList_QNAME = new QName("", "DefinitionList");
    private final static QName _ActionData_QNAME = new QName("", "ActionData");
    private final static QName _DbFixPack_QNAME = new QName("", "DbFixPack");
    private final static QName _SchemaElement_QNAME = new QName("http://generic.riverock.org/xsd/riverock-database-structure.xsd", "SchemaElement");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.riverock.dbrevision.annotation.schema.db
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DbField }
     * 
     */
    public DbField createDbField() {
        return new DbField();
    }

    /**
     * Create an instance of {@link DbImportedKeyList }
     * 
     */
    public DbImportedKeyList createDbImportedKeyList() {
        return new DbImportedKeyList();
    }

    /**
     * Create an instance of {@link DbImportedPKColumn }
     * 
     */
    public DbImportedPKColumn createDbImportedPKColumn() {
        return new DbImportedPKColumn();
    }

    /**
     * Create an instance of {@link DbPrimaryKey }
     * 
     */
    public DbPrimaryKey createDbPrimaryKey() {
        return new DbPrimaryKey();
    }

    /**
     * Create an instance of {@link DefinitionRecordData }
     * 
     */
    public DefinitionRecordData createDefinitionRecordData() {
        return new DefinitionRecordData();
    }

    /**
     * Create an instance of {@link DefinitionFieldData }
     * 
     */
    public DefinitionFieldData createDefinitionFieldData() {
        return new DefinitionFieldData();
    }

    /**
     * Create an instance of {@link DbDatabase }
     * 
     */
    public DbDatabase createDbDatabase() {
        return new DbDatabase();
    }

    /**
     * Create an instance of {@link DefinitionAction }
     * 
     */
    public DefinitionAction createDefinitionAction() {
        return new DefinitionAction();
    }

    /**
     * Create an instance of {@link DefinitionList }
     * 
     */
    public DefinitionList createDefinitionList() {
        return new DefinitionList();
    }

    /**
     * Create an instance of {@link DbKeyActionRule }
     * 
     */
    public DbKeyActionRule createDbKeyActionRule() {
        return new DbKeyActionRule();
    }

    /**
     * Create an instance of {@link DefinitionTableList }
     * 
     */
    public DefinitionTableList createDefinitionTableList() {
        return new DefinitionTableList();
    }

    /**
     * Create an instance of {@link DbBigTextTable }
     * 
     */
    public DbBigTextTable createDbBigTextTable() {
        return new DbBigTextTable();
    }

    /**
     * Create an instance of {@link DbTable }
     * 
     */
    public DbTable createDbTable() {
        return new DbTable();
    }

    /**
     * Create an instance of {@link DbPrimaryKeyColumn }
     * 
     */
    public DbPrimaryKeyColumn createDbPrimaryKeyColumn() {
        return new DbPrimaryKeyColumn();
    }

    /**
     * Create an instance of {@link DbDataFieldData }
     * 
     */
    public DbDataFieldData createDbDataFieldData() {
        return new DbDataFieldData();
    }

    /**
     * Create an instance of {@link DbViewList }
     * 
     */
    public DbViewList createDbViewList() {
        return new DbViewList();
    }

    /**
     * Create an instance of {@link DbView }
     * 
     */
    public DbView createDbView() {
        return new DbView();
    }

    /**
     * Create an instance of {@link DefinitionActionList }
     * 
     */
    public DefinitionActionList createDefinitionActionList() {
        return new DefinitionActionList();
    }

    /**
     * Create an instance of {@link DbFixPack }
     * 
     */
    public DbFixPack createDbFixPack() {
        return new DbFixPack();
    }

    /**
     * Create an instance of {@link DbSchema }
     * 
     */
    public DbSchema createDbSchema() {
        return new DbSchema();
    }

    /**
     * Create an instance of {@link DefinitionActionData }
     * 
     */
    public DefinitionActionData createDefinitionActionData() {
        return new DefinitionActionData();
    }

    /**
     * Create an instance of {@link DefinitionActionDataList }
     * 
     */
    public DefinitionActionDataList createDefinitionActionDataList() {
        return new DefinitionActionDataList();
    }

    /**
     * Create an instance of {@link DbSequenceList }
     * 
     */
    public DbSequenceList createDbSequenceList() {
        return new DbSequenceList();
    }

    /**
     * Create an instance of {@link DbDataSchema }
     * 
     */
    public DbDataSchema createDbDataSchema() {
        return new DbDataSchema();
    }

    /**
     * Create an instance of {@link DbDataTable }
     * 
     */
    public DbDataTable createDbDataTable() {
        return new DbDataTable();
    }

    /**
     * Create an instance of {@link DbDataRecord }
     * 
     */
    public DbDataRecord createDbDataRecord() {
        return new DbDataRecord();
    }

    /**
     * Create an instance of {@link DbSequence }
     * 
     */
    public DbSequence createDbSequence() {
        return new DbSequence();
    }

    /**
     * Create an instance of {@link Definition }
     * 
     */
    public Definition createDefinition() {
        return new Definition();
    }

    /**
     * Create an instance of {@link DefinitionDataList }
     * 
     */
    public DefinitionDataList createDefinitionDataList() {
        return new DefinitionDataList();
    }

    /**
     * Create an instance of {@link CustomSequence }
     * 
     */
    public CustomSequence createCustomSequence() {
        return new CustomSequence();
    }

    /**
     * Create an instance of {@link PrimaryKey }
     * 
     */
    public PrimaryKey createPrimaryKey() {
        return new PrimaryKey();
    }

    /**
     * Create an instance of {@link DefinitionTableData }
     * 
     */
    public DefinitionTableData createDefinitionTableData() {
        return new DefinitionTableData();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DefinitionFieldData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "FieldData")
    public JAXBElement<DefinitionFieldData> createFieldData(DefinitionFieldData value) {
        return new JAXBElement<DefinitionFieldData>(_FieldData_QNAME, DefinitionFieldData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DefinitionList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "DefinitionList")
    public JAXBElement<DefinitionList> createDefinitionList(DefinitionList value) {
        return new JAXBElement<DefinitionList>(_DefinitionList_QNAME, DefinitionList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DefinitionActionList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ActionData")
    public JAXBElement<DefinitionActionList> createActionData(DefinitionActionList value) {
        return new JAXBElement<DefinitionActionList>(_ActionData_QNAME, DefinitionActionList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DbFixPack }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "DbFixPack")
    public JAXBElement<DbFixPack> createDbFixPack(DbFixPack value) {
        return new JAXBElement<DbFixPack>(_DbFixPack_QNAME, DbFixPack.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DbSchema }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://generic.riverock.org/xsd/riverock-database-structure.xsd", name = "SchemaElement")
    public JAXBElement<DbSchema> createSchemaElement(DbSchema value) {
        return new JAXBElement<DbSchema>(_SchemaElement_QNAME, DbSchema.class, null, value);
    }

}