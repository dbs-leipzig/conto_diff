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