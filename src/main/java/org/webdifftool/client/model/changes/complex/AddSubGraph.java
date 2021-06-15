package org.webdifftool.client.model.changes.complex;
 
import java.util.List;

public class AddSubGraph extends ComplexChange {
	
	public AddSubGraph() {
		super();
	}
	
	public AddSubGraph(String id, String name, List<String[]> changeValues) {
		super(id, name, changeValues);
	}
	
	public String getSimpleHTMLRepresenation()
	{
		String result = "<FONT COLOR=\"#800080\"><b>" + this.name + "</b></FONT>(";
		String concept = values.get(0)[0];
		String[] newConcepts = values.get(1);

		result += "<FONT COLOR=\"#008000\"><i " + this.getNameAsToolTip(concept) + ">" + concept + "</i></FONT>, ";
		
		if (newConcepts.length == 1)
		{
			result += "<FONT COLOR=\"#008000\"><i " + this.getNameAsToolTip(newConcepts[0]) + ">" + newConcepts[0] + "</i></FONT>)";
		} else
		{
			result += "[";
			for (String singleConcept : newConcepts)
			{
				result += "<FONT COLOR=\"#008000\"><i " + this.getNameAsToolTip(singleConcept) + ">" + singleConcept + "</i></FONT>, ";
			}
			result = result.substring(0, result.length()-2);
			result += "])";
		}

		return result;
	}
}