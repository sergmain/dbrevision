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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DbViewReplacement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DbViewReplacement">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Family" maxOccurs="unbounded">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="oracle"/>
 *               &lt;enumeration value="mysql"/>
 *               &lt;enumeration value="hypersonic"/>
 *               &lt;enumeration value="sqlserver"/>
 *               &lt;enumeration value="postgrees"/>
 *               &lt;enumeration value="db2"/>
 *               &lt;enumeration value="maxdb"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="View" type="{http://dbrevision.sourceforge.net/xsd/dbrevision-structure-3.0.xsd}DbView"/>
 *         &lt;element name="Skip" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DbViewReplacement", propOrder = {
    "family",
    "view",
    "skip"
})
public class DbViewReplacement {

    @XmlElement(name = "Family", required = true)
    protected List<String> family;
    @XmlElement(name = "View", required = true)
    protected DbView view;
    @XmlElement(name = "Skip")
    protected Boolean skip;

    /**
     * Gets the value of the family property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the family property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFamily().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getFamily() {
        if (family == null) {
            family = new ArrayList<String>();
        }
        return this.family;
    }

    /**
     * Gets the value of the view property.
     * 
     * @return
     *     possible object is
     *     {@link DbView }
     *     
     */
    public DbView getView() {
        return view;
    }

    /**
     * Sets the value of the view property.
     * 
     * @param value
     *     allowed object is
     *     {@link DbView }
     *     
     */
    public void setView(DbView value) {
        this.view = value;
    }

    /**
     * Gets the value of the skip property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSkip() {
        return skip;
    }

    /**
     * Sets the value of the skip property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSkip(Boolean value) {
        this.skip = value;
    }

}
