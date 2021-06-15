/*
 *
 *  * Copyright © 2014 - 2021 Leipzig University (Database Research Group)
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, version 3.
 *  *
 *  * This program is distributed in the hope that it will be useful, but
 *  * WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  * General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.webdifftool.client.model.changes.mapping;

import java.util.List;

import org.webdifftool.client.model.changes.Change;

public class MappingChange extends Change {
	
	public String belongsTo;
	
	public MappingChange()
	{
		super();
	}

	public MappingChange(String id, String name, List<String[]> changeValues)
	{
		super(id, name, changeValues);
	}

	public String toCellBrowserString()
	{
		return super.toCellBrowserString();
	}

}
