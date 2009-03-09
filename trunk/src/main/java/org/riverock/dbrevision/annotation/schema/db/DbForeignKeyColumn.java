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
 * <p>Java class for DbForeignKeyColumn complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DbForeignKeyColumn">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="pkColumnName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fkColumnName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="keySeq" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DbForeignKeyColumn")
public class DbForeignKeyColumn {

    @XmlAttribute
    protected String pkColumnName;
    @XmlAttribute
    protected String fkColumnName;
    @XmlAttribute
    protected Integer keySeq;

    /**
     * Gets the value of the pkColumnName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPkColumnName() {
        return pkColumnName;
    }

    /**
     * Sets the value of the pkColumnName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPkColumnName(String value) {
        this.pkColumnName = value;
    }

    /**
     * Gets the value of the fkColumnName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFkColumnName() {
        return fkColumnName;
    }

    /**
     * Sets the value of the fkColumnName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFkColumnName(String value) {
        this.fkColumnName = value;
    }

    /**
     * Gets the value of the keySeq property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getKeySeq() {
        return keySeq;
    }

    /**
     * Sets the value of the keySeq property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setKeySeq(Integer value) {
        this.keySeq = value;
    }

}
