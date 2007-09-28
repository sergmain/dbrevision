//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.08.06 at 08:17:38 PM MSD 
//


package org.riverock.dbrevision.annotation.schema.db;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
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
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PreviousName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element ref="{}Validator" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{}Action"/>
 *           &lt;element ref="{}CustomClassAction"/>
 *           &lt;element ref="{}SqlAction"/>
 *           &lt;element ref="{}AddTableFieldAction"/>
 *           &lt;element ref="{}TableData"/>
 *           &lt;element name="Table" type="{http://dbrevision.sourceforge.net/xsd/dbrevision-structure.xsd}DbTable"/>
 *           &lt;element name="Sequence" type="{http://dbrevision.sourceforge.net/xsd/dbrevision-structure.xsd}DbSequence"/>
 *           &lt;element name="ForeignKey" type="{http://dbrevision.sourceforge.net/xsd/dbrevision-structure.xsd}DbForeignKey"/>
 *           &lt;element name="Index" type="{http://dbrevision.sourceforge.net/xsd/dbrevision-structure.xsd}DbIndex"/>
 *           &lt;element name="PrimaryKey" type="{http://dbrevision.sourceforge.net/xsd/dbrevision-structure.xsd}DbPrimaryKey"/>
 *           &lt;element name="View" type="{http://dbrevision.sourceforge.net/xsd/dbrevision-structure.xsd}DbView"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="isProcessed" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "previousName",
    "validator",
    "actionOrCustomClassActionOrSqlAction"
})
@XmlRootElement(name = "Patch", namespace = "")
public class Patch {

    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlElement(name = "PreviousName")
    protected String previousName;
    @XmlElement(name = "Validator")
    protected Validator validator;
    @XmlElements({
        @XmlElement(name = "View", type = DbView.class),
        @XmlElement(name = "TableData", type = TableData.class),
        @XmlElement(name = "ForeignKey", type = DbForeignKey.class),
        @XmlElement(name = "PrimaryKey", type = DbPrimaryKey.class),
        @XmlElement(name = "Index", type = DbIndex.class),
        @XmlElement(name = "Table", type = DbTable.class),
        @XmlElement(name = "Action", type = Action.class),
        @XmlElement(name = "Sequence", type = DbSequence.class),
        @XmlElement(name = "SqlAction", type = SqlAction.class),
        @XmlElement(name = "CustomClassAction", type = CustomClassAction.class),
        @XmlElement(name = "AddTableFieldAction", type = AddTableFieldAction.class)
    })
    protected List<Object> actionOrCustomClassActionOrSqlAction;
    @XmlAttribute(name = "isProcessed")
    protected Boolean processed;

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
     * Gets the value of the previousName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPreviousName() {
        return previousName;
    }

    /**
     * Sets the value of the previousName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPreviousName(String value) {
        this.previousName = value;
    }

    /**
     * Gets the value of the validator property.
     * 
     * @return
     *     possible object is
     *     {@link Validator }
     *     
     */
    public Validator getValidator() {
        return validator;
    }

    /**
     * Sets the value of the validator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Validator }
     *     
     */
    public void setValidator(Validator value) {
        this.validator = value;
    }

    /**
     * Gets the value of the actionOrCustomClassActionOrSqlAction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the actionOrCustomClassActionOrSqlAction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getActionOrCustomClassActionOrSqlAction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DbView }
     * {@link TableData }
     * {@link DbForeignKey }
     * {@link DbPrimaryKey }
     * {@link DbIndex }
     * {@link DbTable }
     * {@link Action }
     * {@link DbSequence }
     * {@link SqlAction }
     * {@link CustomClassAction }
     * {@link AddTableFieldAction }
     * 
     * 
     */
    public List<Object> getActionOrCustomClassActionOrSqlAction() {
        if (actionOrCustomClassActionOrSqlAction == null) {
            actionOrCustomClassActionOrSqlAction = new ArrayList<Object>();
        }
        return this.actionOrCustomClassActionOrSqlAction;
    }

    /**
     * Gets the value of the processed property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isProcessed() {
        return processed;
    }

    /**
     * Sets the value of the processed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setProcessed(Boolean value) {
        this.processed = value;
    }

}