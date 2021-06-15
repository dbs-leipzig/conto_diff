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
