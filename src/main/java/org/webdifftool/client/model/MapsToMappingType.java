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
 * A MapsToMappingType // Third level
 */
public class MapsToMappingType 
{
	private final String mapsToMappingTypeName;
	private final List<String> furtherMapToTypes = new ArrayList<String>();

	public MapsToMappingType(String mapsToType) 
	{
		this.mapsToMappingTypeName = mapsToType;
	}

	public void addFurtherMapToType(String mapTo) 
	{
		furtherMapToTypes.add(mapTo);
	}

	public String getMapsToMappingTypeName() 
	{
		return mapsToMappingTypeName;
	}

	/**
	 * Return the list of mapTo Types.
	 */
	public List<String> getMapToTypes() 
	{
		return furtherMapToTypes;
	}
}