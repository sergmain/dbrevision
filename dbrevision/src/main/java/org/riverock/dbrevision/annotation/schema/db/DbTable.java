/*
 * org.riverock.dbrevision - Database revision engine
 * For more information about DbRevision, please visit project site
 * http://www.riverock.org
 *
 * Copyright (C) 2006-2006, Riverock Software, All Rights Reserved.
 *
 * Riverock - The Open-source Java Development Community
 * http://www.riverock.org
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.2-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2006.11.24 at 10:53:49 AM MSK 
//


package org.riverock.dbrevision.annotation.schema.db;

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
 *     &lt;extension base="{http://generic.riverock.org/xsd/riverock-database-structure.xsd}DbAbstractTable">
 *       &lt;sequence>
 *         &lt;element name="PrimaryKey" type="{http://generic.riverock.org/xsd/riverock-database-structure.xsd}DbPrimaryKey" minOccurs="0"/>
 *         &lt;element name="Fields" type="{http://generic.riverock.org/xsd/riverock-database-structure.xsd}DbField" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ImportedKeys" type="{http://generic.riverock.org/xsd/riverock-database-structure.xsd}DbImportedPKColumn" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Data" type="{http://generic.riverock.org/xsd/riverock-database-structure.xsd}DbDataTable" minOccurs="0"/>
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
    "primaryKey",
    "fields",
    "importedKeys",
    "data"
})
public class DbTable
    extends DbAbstractTable
{

    @XmlElement(name = "PrimaryKey")
    protected DbPrimaryKey primaryKey;
    @XmlElement(name = "Fields")
    protected List<DbField> fields;
    @XmlElement(name = "ImportedKeys")
    protected List<DbImportedPKColumn> importedKeys;
    @XmlElement(name = "Data")
    protected DbDataTable data;

    /**
     * Gets the value of the primaryKey property.
     * 
     * @return
     *     possible object is
     *     {@link DbPrimaryKey }
     *     
     */
    public DbPrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    /**
     * Sets the value of the primaryKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link DbPrimaryKey }
     *     
     */
    public void setPrimaryKey(DbPrimaryKey value) {
        this.primaryKey = value;
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
     * Gets the value of the importedKeys property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the importedKeys property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImportedKeys().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DbImportedPKColumn }
     * 
     * 
     */
    public List<DbImportedPKColumn> getImportedKeys() {
        if (importedKeys == null) {
            importedKeys = new ArrayList<DbImportedPKColumn>();
        }
        return this.importedKeys;
    }

    /**
     * Gets the value of the data property.
     * 
     * @return
     *     possible object is
     *     {@link DbDataTable }
     *     
     */
    public DbDataTable getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     * 
     * @param value
     *     allowed object is
     *     {@link DbDataTable }
     *     
     */
    public void setData(DbDataTable value) {
        this.data = value;
    }

}
