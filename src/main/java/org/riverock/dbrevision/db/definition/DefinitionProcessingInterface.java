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

import org.riverock.dbrevision.annotation.schema.db.ActionParameter;
import org.riverock.dbrevision.db.DatabaseAdapter;

/**
 * User: Admin
 * Date: May 20, 2003
 * Time: 10:02:30 PM
 * <p/>
 * $Id: DefinitionProcessingInterface.java 1075 2006-11-24 18:08:42Z serg_main $
 */
public interface DefinitionProcessingInterface {
    public void processAction(DatabaseAdapter adapter, List<ActionParameter> parameters);
}
