package org.webdifftool.client.model.changes.complex;
 
import java.util.List;

public class Substitute extends ComplexChange
{

	public Substitute()
	{
		super();
	}

	public Substitute(String id, String name, List<String[]> changeValues)
	{
		super(id, name, changeValues);
	}
	
	public String getSimpleHTMLRepresenation()
	{
		String result = "<FONT COLOR=\"#800080\"><b>" + this.name + "</b></FONT>(";
		String concept = values.get(0)[0];
		String concept2 = values.get(1)[0];

		result += "<FONT COLOR=\"#FF0000\"><i " + this.getNameAsToolTip(concept) + ">" + concept + "</i></FONT>, ";
		result += "<FONT COLOR=\"#008000\"><i " + this.getNameAsToolTip(concept2) + ">" + concept2 + "</i></FONT> )";

		return result;
	}
}