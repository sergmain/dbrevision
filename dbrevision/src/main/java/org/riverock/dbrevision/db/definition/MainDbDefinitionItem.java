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
package org.riverock.dbrevision.db.definition;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/


/*
 * This class was automatically generated with
 * <a href="http://www.castor.org">Castor 0.9.4.3</a>, using an XML
 * Schema.
 * $Id: MainDbDefinitionItem.java 1075 2006-11-24 18:08:42Z serg_main $
 */
/**
 * Class MainDbDefinitionItem.
 * 
 * @version $Revision: 1075 $ $Date: 2006-11-24 21:08:42 +0300 (Пт, 24 ноя 2006) $
 */
public class MainDbDefinitionItem implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _idDbDefinition
     */
    private long _idDbDefinition;

    /**
     * keeps track of state for field: _idDbDefinition
     */
    private boolean _has_idDbDefinition;

    /**
     * Field _nameDefinition
     */
    private java.lang.String _nameDefinition;

    /**
     * Field _aplayDate
     */
    private java.util.Date _aplayDate;


      //----------------/
     //- Constructors -/
    //----------------/

    public MainDbDefinitionItem() {
        super();
    } //-- org.riverock.schema.core.MainDbDefinitionItem()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method deleteIdDbDefinition
     */
    public void deleteIdDbDefinition()
    {
        this._has_idDbDefinition= false;
    } //-- void deleteIdDbDefinition() 

    /**
     * Method getAplayDateReturns the value of field 'aplayDate'.
     * 
     * @return the value of field 'aplayDate'.
     */
    public java.util.Date getAplayDate()
    {
        return this._aplayDate;
    } //-- java.util.Date getAplayDate() 

    /**
     * Method getIdDbDefinitionReturns the value of field
     * 'idDbDefinition'.
     * 
     * @return the value of field 'idDbDefinition'.
     */
    public long getIdDbDefinition()
    {
        return this._idDbDefinition;
    } //-- long getIdDbDefinition() 

    /**
     * Method getNameDefinitionReturns the value of field
     * 'nameDefinition'.
     * 
     * @return the value of field 'nameDefinition'.
     */
    public java.lang.String getNameDefinition()
    {
        return this._nameDefinition;
    } //-- java.lang.String getNameDefinition() 

    /**
     * Method hasIdDbDefinition
     */
    public boolean hasIdDbDefinition()
    {
        return this._has_idDbDefinition;
    } //-- boolean hasIdDbDefinition() 

    /**
     * Method setAplayDateSets the value of field 'aplayDate'.
     * 
     * @param aplayDate the value of field 'aplayDate'.
     */
    public void setAplayDate(java.util.Date aplayDate)
    {
        this._aplayDate = aplayDate;
    } //-- void setAplayDate(java.util.Date) 

    /**
     * Method setIdDbDefinitionSets the value of field
     * 'idDbDefinition'.
     * 
     * @param idDbDefinition the value of field 'idDbDefinition'.
     */
    public void setIdDbDefinition(long idDbDefinition)
    {
        this._idDbDefinition = idDbDefinition;
        this._has_idDbDefinition = true;
    } //-- void setIdDbDefinition(long) 

    /**
     * Method setNameDefinitionSets the value of field
     * 'nameDefinition'.
     * 
     * @param nameDefinition the value of field 'nameDefinition'.
     */
    public void setNameDefinition(java.lang.String nameDefinition)
    {
        this._nameDefinition = nameDefinition;
    } //-- void setNameDefinition(java.lang.String) 

}
