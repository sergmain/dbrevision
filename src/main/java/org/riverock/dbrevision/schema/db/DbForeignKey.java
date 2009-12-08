//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.12.08 at 04:08:44 AM MSK 
//


package org.riverock.dbrevision.schema.db;

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
 *         &lt;element name="C" type="{http://dbrevision.sourceforge.net/xsd/dbrevision-structure-3.0.xsd}DbForeignKeyColumn" maxOccurs="unbounded"/>
 *         &lt;element name="URule" type="{http://dbrevision.sourceforge.net/xsd/dbrevision-structure-3.0.xsd}DbKeyActionRule" minOccurs="0"/>
 *         &lt;element name="DRule" type="{http://dbrevision.sourceforge.net/xsd/dbrevision-structure-3.0.xsd}DbKeyActionRule" minOccurs="0"/>
 *         &lt;element name="Defer" type="{http://dbrevision.sourceforge.net/xsd/dbrevision-structure-3.0.xsd}DbKeyActionRule" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="pkSchema" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="pkTable" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="pk" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fkSchema" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fkTable" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fk" type="{http://www.w3.org/2001/XMLSchema}string" />
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
    "uRule",
    "dRule",
    "defer"
})
public class DbForeignKey {

    @XmlElement(name = "C", required = true)
    protected List<DbForeignKeyColumn> columns;
    @XmlElement(name = "URule")
    protected DbKeyActionRule uRule;
    @XmlElement(name = "DRule")
    protected DbKeyActionRule dRule;
    @XmlElement(name = "Defer")
    protected DbKeyActionRule defer;
    @XmlAttribute
    protected String pkSchema;
    @XmlAttribute
    protected String pkTable;
    @XmlAttribute(required = true)
    protected String pk;
    @XmlAttribute
    protected String fkSchema;
    @XmlAttribute(required = true)
    protected String fkTable;
    @XmlAttribute
    protected String fk;

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
     * Gets the value of the uRule property.
     * 
     * @return
     *     possible object is
     *     {@link DbKeyActionRule }
     *     
     */
    public DbKeyActionRule getURule() {
        return uRule;
    }

    /**
     * Sets the value of the uRule property.
     * 
     * @param value
     *     allowed object is
     *     {@link DbKeyActionRule }
     *     
     */
    public void setURule(DbKeyActionRule value) {
        this.uRule = value;
    }

    /**
     * Gets the value of the dRule property.
     * 
     * @return
     *     possible object is
     *     {@link DbKeyActionRule }
     *     
     */
    public DbKeyActionRule getDRule() {
        return dRule;
    }

    /**
     * Sets the value of the dRule property.
     * 
     * @param value
     *     allowed object is
     *     {@link DbKeyActionRule }
     *     
     */
    public void setDRule(DbKeyActionRule value) {
        this.dRule = value;
    }

    /**
     * Gets the value of the defer property.
     * 
     * @return
     *     possible object is
     *     {@link DbKeyActionRule }
     *     
     */
    public DbKeyActionRule getDefer() {
        return defer;
    }

    /**
     * Sets the value of the defer property.
     * 
     * @param value
     *     allowed object is
     *     {@link DbKeyActionRule }
     *     
     */
    public void setDefer(DbKeyActionRule value) {
        this.defer = value;
    }

    /**
     * Gets the value of the pkSchema property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPkSchema() {
        return pkSchema;
    }

    /**
     * Sets the value of the pkSchema property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPkSchema(String value) {
        this.pkSchema = value;
    }

    /**
     * Gets the value of the pkTable property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPkTable() {
        return pkTable;
    }

    /**
     * Sets the value of the pkTable property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPkTable(String value) {
        this.pkTable = value;
    }

    /**
     * Gets the value of the pk property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPk() {
        return pk;
    }

    /**
     * Sets the value of the pk property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPk(String value) {
        this.pk = value;
    }

    /**
     * Gets the value of the fkSchema property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFkSchema() {
        return fkSchema;
    }

    /**
     * Sets the value of the fkSchema property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFkSchema(String value) {
        this.fkSchema = value;
    }

    /**
     * Gets the value of the fkTable property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFkTable() {
        return fkTable;
    }

    /**
     * Sets the value of the fkTable property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFkTable(String value) {
        this.fkTable = value;
    }

    /**
     * Gets the value of the fk property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFk() {
        return fk;
    }

    /**
     * Sets the value of the fk property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFk(String value) {
        this.fk = value;
    }

}
