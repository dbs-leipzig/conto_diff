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

package org.webdifftool.client.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A SpecificCompactMappingTyp // Second level
 */
public class SpecificCompactMappingType 
{
	private final String specificCompactMappingTypeName;
	private final List<MapsToMappingType> mapToTypes = new ArrayList<MapsToMappingType>();

	public SpecificCompactMappingType(String compactType) 
	{
		this.specificCompactMappingTypeName = compactType;
	}

	public void addMapToType(MapsToMappingType mapTo) 
	{
		mapToTypes.add(mapTo);
	}

	public String getSpecificCompactTypeName() 
	{
		return specificCompactMappingTypeName;
	}

	/**
	 * Return the list of mapTo Types.
	 */
	public List<MapsToMappingType> getMapToTypes() 
	{
		return mapToTypes;
	}
}
