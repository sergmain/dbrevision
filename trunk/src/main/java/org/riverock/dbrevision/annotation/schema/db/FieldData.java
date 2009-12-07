/*
 * Copyright 2007 Sergei Maslyukov at riverock.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.03.14 at 03:36:49 PM MSK 
//


package org.riverock.dbrevision.annotation.schema.db;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TypeField">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="string"/>
 *               &lt;enumeration value="date"/>
 *               &lt;enumeration value="number"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;choice>
 *           &lt;element name="StringData" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *           &lt;element name="DateData" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *           &lt;element name="NumberData" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="fieldName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="isNull" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "typeField",
    "stringData",
    "dateData",
    "numberData"
})
@XmlRootElement(name = "FieldData", namespace = "")
public class FieldData {

    @XmlElement(name = "TypeField", required = true)
    protected String typeField;
    @XmlElement(name = "StringData")
    protected String stringData;
    @XmlElement(name = "DateData")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateData;
    @XmlElement(name = "NumberData")
    protected BigDecimal numberData;
    @XmlAttribute(required = true)
    protected String fieldName;
    @XmlAttribute(required = true)
    protected String isNull;

    /**
     * Gets the value of the typeField property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypeField() {
        return typeField;
    }

    /**
     * Sets the value of the typeField property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypeField(String value) {
        this.typeField = value;
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
     * Gets the value of the fieldName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Sets the value of the fieldName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFieldName(String value) {
        this.fieldName = value;
    }

    /**
     * Gets the value of the isNull property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsNull() {
        return isNull;
    }

    /**
     * Sets the value of the isNull property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsNull(String value) {
        this.isNull = value;
    }

}
