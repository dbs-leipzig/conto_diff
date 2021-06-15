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
