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
