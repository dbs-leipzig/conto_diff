package org.webdifftool.client.model.changes.basic;

import java.util.List;

public class AddAttribute extends BasicChange {

	public AddAttribute() {
		super();
	}
	
	public AddAttribute(String id, String name, List<String[]> changeValues) {
		super(id,name,changeValues);
	}
	
	public String getSimpleHTMLRepresenation()
	{
		String result = "<FONT COLOR=\"#800080\"><b>" + this.name + "</b></FONT>(";
		String concept = values.get(0)[0];
		String attributeName = values.get(1)[0];
		String value = values.get(2)[0];

		result += "<i " + this.getNameAsToolTip(concept) + ">" + concept + "</i>, ";
		result += "<b>" + attributeName + "</b>, ";
		result += "<FONT COLOR=\"#008000\">" + value + "</FONT> )";

		return result;
	}

}
