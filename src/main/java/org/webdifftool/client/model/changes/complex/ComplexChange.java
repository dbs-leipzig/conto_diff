package org.webdifftool.client.model.changes.complex;

import java.util.List;

import org.webdifftool.client.model.changes.Change;

public class ComplexChange extends Change {
	
	public ComplexChange() {
		super();
	}
	
	public ComplexChange(String id, String name, List<String[]> changeValues) {
		super(id, name, changeValues);
	}
	
	public String toCellBrowserString()
	{
		return super.toCellBrowserString();
	}
}
