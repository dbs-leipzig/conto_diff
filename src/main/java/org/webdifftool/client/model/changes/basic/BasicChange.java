package org.webdifftool.client.model.changes.basic;

import java.util.List;

import org.webdifftool.client.model.changes.Change;

public class BasicChange extends Change {
	
	public String belongsTo;
	
	public BasicChange()
	{
		super();
	}

	public BasicChange(String id, String name, List<String[]> changeValues)
	{
		super(id, name, changeValues);
	}

	public String toCellBrowserString()
	{
		return super.toCellBrowserString();
	}

}
