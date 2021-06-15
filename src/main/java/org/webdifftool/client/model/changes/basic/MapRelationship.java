package org.webdifftool.client.model.changes.basic;
 
import java.util.List;

public class MapRelationship extends BasicChange
{

	public MapRelationship()
	{
		super();
	}

	public MapRelationship(String id, String name, List<String[]> changeValues)
	{
		super(id, name, changeValues);
	}

	public String getSimpleHTMLRepresenation()
	{
		String result = "<FONT COLOR=\"#800080\"><b>" + this.name + "</b></FONT>(";
		String concept1 = values.get(0)[0];
		String relation_old = values.get(1)[0];
		String concept2 = values.get(2)[0];
		String relation_new = values.get(4)[0];

		result += "<i " + this.getNameAsToolTip(concept1) + ">" + concept1 + "</i>, ";
		result += "<i " + this.getNameAsToolTip(concept2) + ">" + concept2 + "</i>, ";
		result += "<FONT COLOR=\"#FF0000\"><b>" + relation_old + "</b></FONT>, ";
		result += "<FONT COLOR=\"#00FF00\"><b>" + relation_new + "</b></FONT> )";
		
		return result;
	}
}
