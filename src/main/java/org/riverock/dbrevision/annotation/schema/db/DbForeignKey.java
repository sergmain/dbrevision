//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.03.14 at 03:36:49 PM MSK 
//


package org.riverock.dbrevision.annotation.schema.db;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DbForeignKey complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DbForeignKey">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Column" type="{http://dbrevision.sourceforge.net/xsd/dbrevision-structure.xsd}DbForeignKeyColumn" maxOccurs="unbounded"/>
 *         &lt;element name="UpdateRule" type="{http://dbrevision.sourceforge.net/xsd/dbrevision-structure.xsd}DbKeyActionRule" minOccurs="0"/>
 *         &lt;element name="DeleteRule" type="{http://dbrevision.sourceforge.net/xsd/dbrevision-structure.xsd}DbKeyActionRule" minOccurs="0"/>
 *         &lt;element name="Deferrability" type="{http://dbrevision.sourceforge.net/xsd/dbrevision-structure.xsd}DbKeyActionRule" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="pkSchemaName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="pkTableName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="pkName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fkSchemaName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fkTableName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fkName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DbForeignKey", propOrder = {
    "columns",
    "updateRule",
    "deleteRule",
    "deferrability"
})
public class DbForeignKey {

    @XmlElement(name = "Column", required = true)
    protected List<DbForeignKeyColumn> columns;
    @XmlElement(name = "UpdateRule")
    protected DbKeyActionRule updateRule;
    @XmlElement(name = "DeleteRule")
    protected DbKeyActionRule deleteRule;
    @XmlElement(name = "Deferrability")
    protected DbKeyActionRule deferrability;
    @XmlAttribute
    protected String pkSchemaName;
    @XmlAttribute
    protected String pkTableName;
    @XmlAttribute(required = true)
    protected String pkName;
    @XmlAttribute
    protected String fkSchemaName;
    @XmlAttribute(required = true)
    protected String fkTableName;
    @XmlAttribute
    protected String fkName;

    /**
     * Gets the value of the columns property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the columns property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getColumns().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DbForeignKeyColumn }
     * 
     * 
     */
    public List<DbForeignKeyColumn> getColumns() {
        if (columns == null) {
            columns = new ArrayList<DbForeignKeyColumn>();
        }
        return this.columns;
    }

    /**
     * Gets the value of the updateRule property.
     * 
     * @return
     *     possible object is
     *     {@link DbKeyActionRule }
     *     
     */
    public DbKeyActionRule getUpdateRule() {
        return updateRule;
    }

    /**
     * Sets the value of the updateRule property.
     * 
     * @param value
     *     allowed object is
     *     {@link DbKeyActionRule }
     *     
     */
    public void setUpdateRule(DbKeyActionRule value) {
        this.updateRule = value;
    }

    /**
     * Gets the value of the deleteRule property.
     * 
     * @return
     *     possible object is
     *     {@link DbKeyActionRule }
     *     
     */
    public DbKeyActionRule getDeleteRule() {
        return deleteRule;
    }

    /**
     * Sets the value of the deleteRule property.
     * 
     * @param value
     *     allowed object is
     *     {@link DbKeyActionRule }
     *     
     */
    public void setDeleteRule(DbKeyActionRule value) {
        this.deleteRule = value;
    }

    /**
     * Gets the value of the deferrability property.
     * 
     * @return
     *     possible object is
     *     {@link DbKeyActionRule }
     *     
     */
    public DbKeyActionRule getDeferrability() {
        return deferrability;
    }

    /**
     * Sets the value of the deferrability property.
     * 
     * @param value
     *     allowed object is
     *     {@link DbKeyActionRule }
     *     
     */
    public void setDeferrability(DbKeyActionRule value) {
        this.deferrability = value;
    }

    /**
     * Gets the value of the pkSchemaName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPkSchemaName() {
        return pkSchemaName;
    }

    /**
     * Sets the value of the pkSchemaName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPkSchemaName(String value) {
        this.pkSchemaName = value;
    }

    /**
     * Gets the value of the pkTableName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPkTableName() {
        return pkTableName;
    }

    /**
     * Sets the value of the pkTableName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPkTableName(String value) {
        this.pkTableName = value;
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
     * Gets the value of the fkSchemaName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFkSchemaName() {
        return fkSchemaName;
    }

    /**
     * Sets the value of the fkSchemaName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFkSchemaName(String value) {
        this.fkSchemaName = value;
    }

    /**
     * Gets the value of the fkTableName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFkTableName() {
        return fkTableName;
    }

    /**
     * Sets the value of the fkTableName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFkTableName(String value) {
        this.fkTableName = value;
    }

    /**
     * Gets the value of the fkName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFkName() {
        return fkName;
    }

    /**
     * Sets the value of the fkName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFkName(String value) {
        this.fkName = value;
    }

}
