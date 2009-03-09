//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.03.08 at 09:33:59 PM MSK 
//


package org.riverock.dbrevision.annotation.schema.db;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DbSequence complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DbSequence">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="minValue" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="maxValue" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="incrementBy" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="isCycle" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="isOrder" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="cacheSize" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="lastNumber" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DbSequence")
public class DbSequence {

    @XmlAttribute(required = true)
    protected String name;
    @XmlAttribute(required = true)
    protected int minValue;
    @XmlAttribute(required = true)
    protected String maxValue;
    @XmlAttribute(required = true)
    protected int incrementBy;
    @XmlAttribute
    protected Boolean isCycle;
    @XmlAttribute
    protected Boolean isOrder;
    @XmlAttribute
    protected Integer cacheSize;
    @XmlAttribute(required = true)
    protected long lastNumber;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the minValue property.
     * 
     */
    public int getMinValue() {
        return minValue;
    }

    /**
     * Sets the value of the minValue property.
     * 
     */
    public void setMinValue(int value) {
        this.minValue = value;
    }

    /**
     * Gets the value of the maxValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaxValue() {
        return maxValue;
    }

    /**
     * Sets the value of the maxValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaxValue(String value) {
        this.maxValue = value;
    }

    /**
     * Gets the value of the incrementBy property.
     * 
     */
    public int getIncrementBy() {
        return incrementBy;
    }

    /**
     * Sets the value of the incrementBy property.
     * 
     */
    public void setIncrementBy(int value) {
        this.incrementBy = value;
    }

    /**
     * Gets the value of the isCycle property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsCycle() {
        return isCycle;
    }

    /**
     * Sets the value of the isCycle property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsCycle(Boolean value) {
        this.isCycle = value;
    }

    /**
     * Gets the value of the isOrder property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsOrder() {
        return isOrder;
    }

    /**
     * Sets the value of the isOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsOrder(Boolean value) {
        this.isOrder = value;
    }

    /**
     * Gets the value of the cacheSize property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCacheSize() {
        return cacheSize;
    }

    /**
     * Sets the value of the cacheSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCacheSize(Integer value) {
        this.cacheSize = value;
    }

    /**
     * Gets the value of the lastNumber property.
     * 
     */
    public long getLastNumber() {
        return lastNumber;
    }

    /**
     * Sets the value of the lastNumber property.
     * 
     */
    public void setLastNumber(long value) {
        this.lastNumber = value;
    }

}
