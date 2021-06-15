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
 * A CompactMappingType // First Level.
 */
public class CompactMappingType 
{
	private final String compactMappingTypeName;
	private final int numberOfChanges;
	private final List<SpecificCompactMappingType> specificCompactMappingTypes = new ArrayList<SpecificCompactMappingType>();

	public CompactMappingType(String compactType, int numberOfChanges) 
	{
		this.compactMappingTypeName = compactType;
		this.numberOfChanges = numberOfChanges;
	}

	public void addSpecificCompactMappingType(
			SpecificCompactMappingType specificType) 
	{
		specificCompactMappingTypes.add(specificType);
	}

	public String getCompactMappingTypeName() 
	{
		return compactMappingTypeName;
	}

	public int getNumberOfChanges() 
	{
		return this.numberOfChanges;
	}
	/**
	 * Return SpecificCompactMappingType for this CompactMappingType.
	 */
	public List<SpecificCompactMappingType> getSpecificCompactMappingTypes() 
	{
		return specificCompactMappingTypes;
	}
}
