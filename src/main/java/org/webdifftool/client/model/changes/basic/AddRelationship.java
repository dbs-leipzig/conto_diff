package org.webdifftool.client.model.changes.basic;
 
import java.util.List;

public class AddRelationship extends BasicChange
{

	public AddRelationship()
	{
		super();
	}

	public AddRelationship(String id, String name, List<String[]> changeValues)
	{
		super(id, name, changeValues);
	}

	public String getSimpleHTMLRepresenation()
	{
		String result = "<FONT COLOR=\"#800080\"><b>" + this.name + "</b></FONT>(";
		String concept1 = values.get(0)[0];
		String relation = values.get(1)[0];
		String concept2 = values.get(2)[0];

		result += "<i " + this.getNameAsToolTip(concept1) + ">" + concept1 + "</i>, ";
		result += "<b>" + relation + "</b>, ";
		result += "<FONT COLOR=\"#008000\"><i " + this.getNameAsToolTip(concept2) + ">" + concept2 + "</i></FONT> )";

		return result;
	}
}
