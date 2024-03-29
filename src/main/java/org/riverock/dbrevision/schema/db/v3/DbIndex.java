//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.12.13 at 02:37:56 PM MSK 
//


package org.riverock.dbrevision.schema.db.v3;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DbIndex complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DbIndex">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="C" type="{http://dbrevision.sourceforge.net/xsd/dbrevision-structure-3.0.xsd}DbIndexColumn" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="c" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="s" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="t" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="i" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="isNonUnique" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="indexQualifier" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="cardinality" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="pages" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="filterCondition" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DbIndex", propOrder = {
    "columns"
})
public class DbIndex {

    @XmlElement(name = "C", required = true)
    protected List<DbIndexColumn> columns;
    @XmlAttribute
    protected String c;
    @XmlAttribute
    protected String s;
    @XmlAttribute(required = true)
    protected String t;
    @XmlAttribute
    protected String i;
    @XmlAttribute(name = "isNonUnique")
    protected Boolean nonUnique;
    @XmlAttribute
    protected String indexQualifier;
    @XmlAttribute
    protected Integer type;
    @XmlAttribute
    protected Integer cardinality;
    @XmlAttribute
    protected Integer pages;
    @XmlAttribute
    protected String filterCondition;

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
     * {@link DbIndexColumn }
     * 
     * 
     */
    public List<DbIndexColumn> getColumns() {
        if (columns == null) {
            columns = new ArrayList<DbIndexColumn>();
        }
        return this.columns;
    }

    /**
     * Gets the value of the c property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getC() {
        return c;
    }

    /**
     * Sets the value of the c property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setC(String value) {
        this.c = value;
    }

    /**
     * Gets the value of the s property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getS() {
        return s;
    }

    /**
     * Sets the value of the s property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setS(String value) {
        this.s = value;
    }

    /**
     * Gets the value of the t property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getT() {
        return t;
    }

    /**
     * Sets the value of the t property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setT(String value) {
        this.t = value;
    }

    /**
     * Gets the value of the i property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getI() {
        return i;
    }

    /**
     * Sets the value of the i property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setI(String value) {
        this.i = value;
    }

    /**
     * Gets the value of the nonUnique property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNonUnique() {
        return nonUnique;
    }

    /**
     * Sets the value of the nonUnique property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNonUnique(Boolean value) {
        this.nonUnique = value;
    }

    /**
     * Gets the value of the indexQualifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIndexQualifier() {
        return indexQualifier;
    }

    /**
     * Sets the value of the indexQualifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIndexQualifier(String value) {
        this.indexQualifier = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setType(Integer value) {
        this.type = value;
    }

    /**
     * Gets the value of the cardinality property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCardinality() {
        return cardinality;
    }

    /**
     * Sets the value of the cardinality property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCardinality(Integer value) {
        this.cardinality = value;
    }

    /**
     * Gets the value of the pages property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPages() {
        return pages;
    }

    /**
     * Sets the value of the pages property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPages(Integer value) {
        this.pages = value;
    }

    /**
     * Gets the value of the filterCondition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFilterCondition() {
        return filterCondition;
    }

    /**
     * Sets the value of the filterCondition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilterCondition(String value) {
        this.filterCondition = value;
    }

}
