//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.08.02 at 08:43:54 PM MSD 
//


package org.riverock.dbrevision.annotation.schema.db;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for DbDataFieldData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DbDataFieldData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="JavaTypeField" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="StringData" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DateData" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="NumberData" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="IsNull" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *       &lt;attribute name="decimalDigit" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="size" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DbDataFieldData", propOrder = {
    "javaTypeField",
    "stringData",
    "dateData",
    "numberData",
    "isNull"
})
public class DbDataFieldData {

    @XmlElement(name = "JavaTypeField")
    protected int javaTypeField;
    @XmlElement(name = "StringData")
    protected String stringData;
    @XmlElement(name = "DateData")
    protected XMLGregorianCalendar dateData;
    @XmlElement(name = "NumberData")
    protected BigDecimal numberData;
    @XmlElement(name = "IsNull")
    protected boolean isNull;
    @XmlAttribute
    protected Integer decimalDigit;
    @XmlAttribute
    protected Integer size;

    /**
     * Gets the value of the javaTypeField property.
     * 
     */
    public int getJavaTypeField() {
        return javaTypeField;
    }

    /**
     * Sets the value of the javaTypeField property.
     * 
     */
    public void setJavaTypeField(int value) {
        this.javaTypeField = value;
    }

    /**
     * Gets the value of the stringData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStringData() {
        return stringData;
    }

    /**
     * Sets the value of the stringData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStringData(String value) {
        this.stringData = value;
    }

    /**
     * Gets the value of the dateData property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateData() {
        return dateData;
    }

    /**
     * Sets the value of the dateData property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateData(XMLGregorianCalendar value) {
        this.dateData = value;
    }

    /**
     * Gets the value of the numberData property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getNumberData() {
        return numberData;
    }

    /**
     * Sets the value of the numberData property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setNumberData(BigDecimal value) {
        this.numberData = value;
    }

    /**
     * Gets the value of the isNull property.
     * 
     */
    public boolean isIsNull() {
        return isNull;
    }

    /**
     * Sets the value of the isNull property.
     * 
     */
    public void setIsNull(boolean value) {
        this.isNull = value;
    }

    /**
     * Gets the value of the decimalDigit property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDecimalDigit() {
        return decimalDigit;
    }

    /**
     * Sets the value of the decimalDigit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDecimalDigit(Integer value) {
        this.decimalDigit = value;
    }

    /**
     * Gets the value of the size property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSize() {
        return size;
    }

    /**
     * Sets the value of the size property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSize(Integer value) {
        this.size = value;
    }

}
