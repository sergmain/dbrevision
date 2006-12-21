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

import java.util.Date;

/**
 * Class MainDbDefinitionItem.
 * 
 * @version $Revision: 1075 $ $Date: 2006-11-24 21:08:42 +0300 (Пт, 24 ноя 2006) $
 */
public class MainDbDefinitionItem implements java.io.Serializable {

    /**
     * Field _idDbDefinition
     */
    private long idDbDefinition;

    /**
     * keeps track of state for field: _idDbDefinition
     */
    private boolean has_idDbDefinition;

    /**
     * Field _nameDefinition
     */
    private java.lang.String nameDefinition;

    /**
     * Field _aplayDate
     */
    private java.util.Date applayDate;

    public MainDbDefinitionItem() {
    }

    public long getIdDbDefinition() {
        return idDbDefinition;
    }

    public void setIdDbDefinition(long idDbDefinition) {
        this.idDbDefinition = idDbDefinition;
    }

    public boolean isHas_idDbDefinition() {
        return has_idDbDefinition;
    }

    public void setHas_idDbDefinition(boolean has_idDbDefinition) {
        this.has_idDbDefinition = has_idDbDefinition;
    }

    public String getNameDefinition() {
        return nameDefinition;
    }

    public void setNameDefinition(String nameDefinition) {
        this.nameDefinition = nameDefinition;
    }

    public Date getApplayDate() {
        return applayDate;
    }

    public void setApplayDate(Date applayDate) {
        this.applayDate = applayDate;
    }
}
