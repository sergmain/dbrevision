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

import java.util.List;
import java.util.ArrayList;

/**
 * Class MainDbDefinitionListType.
 * 
 * @version $Revision: 1075 $ $Date: 2006-11-24 21:08:42 +0300 (Пт, 24 ноя 2006) $
 */
public class MainDbDefinitionList implements java.io.Serializable {

    private List<MainDbDefinitionItem> mainDbDefinitionList = new ArrayList<MainDbDefinitionItem>();

    public MainDbDefinitionList() {
    }

    public List<MainDbDefinitionItem> getMainDbDefinitionList() {
        return mainDbDefinitionList;
    }

    public void setMainDbDefinitionList(List<MainDbDefinitionItem> mainDbDefinitionList) {
        this.mainDbDefinitionList = mainDbDefinitionList;
    }
}
