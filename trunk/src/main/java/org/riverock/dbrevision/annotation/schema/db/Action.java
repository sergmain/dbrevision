//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.07.30 at 03:53:10 PM MSD 
//


package org.riverock.dbrevision.annotation.schema.db;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="Type">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="CUSTOM_SQL"/>
 *               &lt;enumeration value="CUSTOM_CLASS_ACTION"/>
 *               &lt;enumeration value="CREATE_SEQUENCE"/>
 *               &lt;enumeration value="CREATE_TABLE"/>
 *               &lt;enumeration value="ADD_TABLE_COLUMN"/>
 *               &lt;enumeration value="DROP_TABLE_COLUMN"/>
 *               &lt;enumeration value="ADD_PRIMARY_KEY"/>
 *               &lt;enumeration value="ADD_FOREIGN_KEY"/>
 *               &lt;enumeration value="DROP_PRIMARY_KEY"/>
 *               &lt;enumeration value="DROP_FOREIGN_KEY"/>
 *               &lt;enumeration value="DROP_TABLE"/>
 *               &lt;enumeration value="DROP_SEQUENCE"/>
 *               &lt;enumeration value="DELETE_BEFORE_FK"/>
 *               &lt;enumeration value="COPY_COLUMN"/>
 *               &lt;enumeration value="CLONE_COLUMN"/>
 *               &lt;enumeration value="COPY_TABLE"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element ref="{}ActionParameter" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "type",
    "actionParameters"
})
@XmlRootElement(name = "Action", namespace = "")
public class Action {

    @XmlElement(name = "Type", required = true)
    protected String type;
    @XmlElement(name = "ActionParameter")
    protected List<ActionParameter> actionParameters;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the actionParameters property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the actionParameters property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getActionParameters().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ActionParameter }
     * 
     * 
     */
    public List<ActionParameter> getActionParameters() {
        if (actionParameters == null) {
            actionParameters = new ArrayList<ActionParameter>();
        }
        return this.actionParameters;
    }

}