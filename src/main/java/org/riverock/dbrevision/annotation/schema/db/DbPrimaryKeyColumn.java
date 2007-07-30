//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.07.30 at 11:03:46 PM MSD 
//


package org.riverock.dbrevision.annotation.schema.db;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DbPrimaryKeyColumn complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DbPrimaryKeyColumn">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="catalogName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="columnName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="keySeq" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="pkName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="schemaName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="tableName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DbPrimaryKeyColumn")
public class DbPrimaryKeyColumn {

    @XmlAttribute
    protected String catalogName;
    @XmlAttribute(required = true)
    protected String columnName;
    @XmlAttribute(required = true)
    protected int keySeq;
    @XmlAttribute
    protected String pkName;
    @XmlAttribute
    protected String schemaName;
    @XmlAttribute(required = true)
    protected String tableName;

    /**
     * Gets the value of the catalogName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCatalogName() {
        return catalogName;
    }

    /**
     * Sets the value of the catalogName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCatalogName(String value) {
        this.catalogName = value;
    }

    /**
     * Gets the value of the columnName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Sets the value of the columnName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColumnName(String value) {
        this.columnName = value;
    }

    /**
     * Gets the value of the keySeq property.
     * 
     */
    public int getKeySeq() {
        return keySeq;
    }

    /**
     * Sets the value of the keySeq property.
     * 
     */
    public void setKeySeq(int value) {
        this.keySeq = value;
    }

    /**
     * Gets the value of the pkName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPkName() {
        return pkName;
    }

    /**
     * Sets the value of the pkName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPkName(String value) {
        this.pkName = value;
    }

    /**
     * Gets the value of the schemaName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * Sets the value of the schemaName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSchemaName(String value) {
        this.schemaName = value;
    }

    /**
     * Gets the value of the tableName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Sets the value of the tableName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTableName(String value) {
        this.tableName = value;
    }

}
