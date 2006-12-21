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
 * <p>Java class for DbSchema complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DbSchema">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Tables" type="{http://generic.riverock.org/xsd/riverock-database-structure.xsd}DbTable" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Views" type="{http://generic.riverock.org/xsd/riverock-database-structure.xsd}DbView" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Sequences" type="{http://generic.riverock.org/xsd/riverock-database-structure.xsd}DbSequence" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="BigTextTable" type="{http://generic.riverock.org/xsd/riverock-database-structure.xsd}DbBigTextTable" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DbSchema", propOrder = {
    "tables",
    "views",
    "sequences",
    "bigTextTable"
})
public class DbSchema {

    @XmlElement(name = "Tables")
    protected List<DbTable> tables;
    @XmlElement(name = "Views")
    protected List<DbView> views;
    @XmlElement(name = "Sequences")
    protected List<DbSequence> sequences;
    @XmlElement(name = "BigTextTable")
    protected List<DbBigTextTable> bigTextTable;

    /**
     * Gets the value of the tables property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tables property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTables().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DbTable }
     * 
     * 
     */
    public List<DbTable> getTables() {
        if (tables == null) {
            tables = new ArrayList<DbTable>();
        }
        return this.tables;
    }

    /**
     * Gets the value of the views property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the views property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getViews().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DbView }
     * 
     * 
     */
    public List<DbView> getViews() {
        if (views == null) {
            views = new ArrayList<DbView>();
        }
        return this.views;
    }

    /**
     * Gets the value of the sequences property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sequences property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSequences().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DbSequence }
     * 
     * 
     */
    public List<DbSequence> getSequences() {
        if (sequences == null) {
            sequences = new ArrayList<DbSequence>();
        }
        return this.sequences;
    }

    /**
     * Gets the value of the bigTextTable property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the bigTextTable property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBigTextTable().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DbBigTextTable }
     * 
     * 
     */
    public List<DbBigTextTable> getBigTextTable() {
        if (bigTextTable == null) {
            bigTextTable = new ArrayList<DbBigTextTable>();
        }
        return this.bigTextTable;
    }

}