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

import java.util.Vector;

/*
 * This class was automatically generated with
 * <a href="http://www.castor.org">Castor 0.9.4.3</a>, using an XML
 * Schema.
 * $Id: MainDbDefinitionList.java 1075 2006-11-24 18:08:42Z serg_main $
 */
/**
 * Class MainDbDefinitionListType.
 * 
 * @version $Revision: 1075 $ $Date: 2006-11-24 21:08:42 +0300 (Пт, 24 ноя 2006) $
 */
public class MainDbDefinitionList implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _mainDbDefinitionList
     */
    private java.util.Vector _mainDbDefinitionList;


      //----------------/
     //- Constructors -/
    //----------------/

    public MainDbDefinitionList() {
        super();
        _mainDbDefinitionList = new Vector();
    } //-- MainDbDefinitionListType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addMainDbDefinition
     * 
     * @param index
     * @param vMainDbDefinition
     */
    public void addMainDbDefinition(int index, MainDbDefinitionItem vMainDbDefinition)
        throws java.lang.IndexOutOfBoundsException
    {
        _mainDbDefinitionList.insertElementAt(vMainDbDefinition, index);
    } //-- void addMainDbDefinition(int, MainDbDefinitionItem)

    /**
     * Method addMainDbDefinition
     *
     * @param vMainDbDefinition
     */
    public void addMainDbDefinition(MainDbDefinitionItem vMainDbDefinition)
        throws java.lang.IndexOutOfBoundsException
    {
        _mainDbDefinitionList.add(vMainDbDefinition);
    } //-- void addMainDbDefinition(int, MainDbDefinitionItem)

    /**
     * Method enumerateMainDbDefinition
     */
    public java.util.Enumeration enumerateMainDbDefinition()
    {
        return _mainDbDefinitionList.elements();
    } //-- java.util.Enumeration enumerateMainDbDefinition() 

    /**
     * Method getMainDbDefinition
     * 
     * @param index
     */
    public MainDbDefinitionItem getMainDbDefinition(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _mainDbDefinitionList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (MainDbDefinitionItem) _mainDbDefinitionList.elementAt(index);
    } //-- MainDbDefinitionItem getMainDbDefinition(int)

    /**
     * Method getMainDbDefinition
     */
    public MainDbDefinitionItem[] getMainDbDefinition()
    {
        int size = _mainDbDefinitionList.size();
        MainDbDefinitionItem[] mArray = new MainDbDefinitionItem[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (MainDbDefinitionItem) _mainDbDefinitionList.elementAt(index);
        }
        return mArray;
    } //-- MainDbDefinitionItem[] getMainDbDefinition()

    /**
     * Method getMainDbDefinitionAsReferenceReturns a reference to
     * 'mainDbDefinition'. No type checking is performed on any
     * modications to the Vector.
     * 
     * @return returns a reference to the Vector.
     */
    public java.util.Vector getMainDbDefinitionAsReference()
    {
        return _mainDbDefinitionList;
    } //-- java.util.Vector getMainDbDefinitionAsReference() 

    /**
     * Method getMainDbDefinitionCount
     */
    public int getMainDbDefinitionCount()
    {
        return _mainDbDefinitionList.size();
    } //-- int getMainDbDefinitionCount() 

    /**
     * Method removeAllMainDbDefinition
     */
    public void removeAllMainDbDefinition()
    {
        _mainDbDefinitionList.removeAllElements();
    } //-- void removeAllMainDbDefinition() 

    /**
     * Method removeMainDbDefinition
     * 
     * @param index
     */
    public MainDbDefinitionItem removeMainDbDefinition(int index)
    {
        java.lang.Object obj = _mainDbDefinitionList.elementAt(index);
        _mainDbDefinitionList.removeElementAt(index);
        return (MainDbDefinitionItem) obj;
    } //-- MainDbDefinitionItem removeMainDbDefinition(int)

    /**
     * Method setMainDbDefinition
     * 
     * @param index
     * @param vMainDbDefinition
     */
    public void setMainDbDefinition(int index, MainDbDefinitionItem vMainDbDefinition)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _mainDbDefinitionList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _mainDbDefinitionList.setElementAt(vMainDbDefinition, index);
    } //-- void setMainDbDefinition(int, MainDbDefinitionItem)

    /**
     * Method setMainDbDefinition
     * 
     * @param mainDbDefinitionArray
     */
    public void setMainDbDefinition(MainDbDefinitionItem[] mainDbDefinitionArray)
    {
        //-- copy array
        _mainDbDefinitionList.removeAllElements();
        for (int i = 0; i < mainDbDefinitionArray.length; i++) {
            _mainDbDefinitionList.addElement(mainDbDefinitionArray[i]);
        }
    } //-- void setMainDbDefinition(MainDbDefinitionItem)

    /**
     * Method setMainDbDefinitionSets the value of
     * 'mainDbDefinition' by copying the given Vector.
     * 
     * @param mainDbDefinitionVector the Vector to copy.
     */
    public void setMainDbDefinition(java.util.Vector mainDbDefinitionVector)
    {
        //-- copy vector
        _mainDbDefinitionList.removeAllElements();
        for (int i = 0; i < mainDbDefinitionVector.size(); i++) {
            _mainDbDefinitionList.addElement(mainDbDefinitionVector.elementAt(i));
        }
    } //-- void setMainDbDefinition(java.util.Vector) 

    /**
     * Method setMainDbDefinitionAsReferenceSets the value of
     * 'mainDbDefinition' by setting it to the given Vector. No
     * type checking is performed.
     * 
     * @param mainDbDefinitionVector the Vector to copy.
     */
    public void setMainDbDefinitionAsReference(java.util.Vector mainDbDefinitionVector)
    {
        _mainDbDefinitionList = mainDbDefinitionVector;
    } //-- void setMainDbDefinitionAsReference(java.util.Vector) 

}
