package org.webdifftool.client.model.changes.complex;
 
import java.util.List;

public class AddInner extends ComplexChange
{

	public AddInner()
	{
		super();
	}

	public AddInner(String id, String name, List<String[]> changeValues)
	{
		super(id, name, changeValues);
	}

	public String getSimpleHTMLRepresenation()
	{
		String result = "<FONT COLOR=\"#800080\"><b>" + this.name + "</b></FONT>(";
		String concept = values.get(0)[0];

		result += "<FONT COLOR=\"#008000\"><i " + this.getNameAsToolTip(concept) + ">" + concept + "</i></FONT> )";

		return result;
	}
}