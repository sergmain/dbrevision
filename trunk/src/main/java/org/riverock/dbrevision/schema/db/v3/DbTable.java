//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.12.08 at 04:08:44 AM MSK 
//


package org.riverock.dbrevision.schema.db.v3;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DbTable complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DbTable">
 *   &lt;complexContent>
 *     &lt;extension base="{http://dbrevision.sourceforge.net/xsd/dbrevision-structure-3.0.xsd}DbAbstractTable">
 *       &lt;sequence>
 *         &lt;element name="Pk" type="{http://dbrevision.sourceforge.net/xsd/dbrevision-structure-3.0.xsd}DbPrimaryKey" minOccurs="0"/>
 *         &lt;element name="F" type="{http://dbrevision.sourceforge.net/xsd/dbrevision-structure-3.0.xsd}DbField" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Fk" type="{http://dbrevision.sourceforge.net/xsd/dbrevision-structure-3.0.xsd}DbForeignKey" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="I" type="{http://dbrevision.sourceforge.net/xsd/dbrevision-structure-3.0.xsd}DbIndex" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="D" type="{http://dbrevision.sourceforge.net/xsd/dbrevision-structure-3.0.xsd}DbDataTable" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DbTable", propOrder = {
    "pk",
    "fields",
    "foreignKeys",
    "indexes",
    "d"
})
public class DbTable
    extends DbAbstractTable
{

    @XmlElement(name = "Pk")
    protected DbPrimaryKey pk;
    @XmlElement(name = "F")
    protected List<DbField> fields;
    @XmlElement(name = "Fk")
    protected List<DbForeignKey> foreignKeys;
    @XmlElement(name = "I")
    protected List<DbIndex> indexes;
    @XmlElement(name = "D")
    protected DbDataTable d;

    /**
     * Gets the value of the pk property.
     * 
     * @return
     *     possible object is
     *     {@link DbPrimaryKey }
     *     
     */
    public DbPrimaryKey getPk() {
        return pk;
    }

    /**
     * Sets the value of the pk property.
     * 
     * @param value
     *     allowed object is
     *     {@link DbPrimaryKey }
     *     
     */
    public void setPk(DbPrimaryKey value) {
        this.pk = value;
    }

    /**
     * Gets the value of the fields property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fields property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFields().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DbField }
     * 
     * 
     */
    public List<DbField> getFields() {
        if (fields == null) {
            fields = new ArrayList<DbField>();
        }
        return this.fields;
    }

    /**
     * Gets the value of the foreignKeys property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the foreignKeys property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getForeignKeys().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DbForeignKey }
     * 
     * 
     */
    public List<DbForeignKey> getForeignKeys() {
        if (foreignKeys == null) {
            foreignKeys = new ArrayList<DbForeignKey>();
        }
        return this.foreignKeys;
    }

    /**
     * Gets the value of the indexes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the indexes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIndexes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DbIndex }
     * 
     * 
     */
    public List<DbIndex> getIndexes() {
        if (indexes == null) {
            indexes = new ArrayList<DbIndex>();
        }
        return this.indexes;
    }

    /**
     * Gets the value of the d property.
     * 
     * @return
     *     possible object is
     *     {@link DbDataTable }
     *     
     */
    public DbDataTable getD() {
        return d;
    }

    /**
     * Sets the value of the d property.
     * 
     * @param value
     *     allowed object is
     *     {@link DbDataTable }
     *     
     */
    public void setD(DbDataTable value) {
        this.d = value;
    }

}