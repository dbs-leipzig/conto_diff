package org.webdifftool.client.model.changes.complex;
 
import java.util.List;

public class ChgAttValue extends ComplexChange {
	
	public ChgAttValue() {
		super();
	}
	
	public ChgAttValue(String id, String name, List<String[]> changeValues) {
		super(id, name, changeValues);
	}
	
	public String getSimpleHTMLRepresenation() {
		String result = "<FONT COLOR=\"#800080\"><b>"+this.name+"</b></FONT>(";
		String concept = values.get(0)[0];
		String attributeName = values.get(1)[0];
		String[] oldValues = values.get(2);
		String[] newValues = values.get(3);
		
		result += "<i " + this.getNameAsToolTip(concept) + ">"+concept+"</i>, ";
		result += "<b>"+attributeName+"</b>, ";
		if (oldValues.length==1) {
			result += "<FONT COLOR=\"#FF0000\">"+oldValues[0]+"</FONT>, ";
		} else {
			result += "[";
			for (String oldValue : oldValues) {
				result += "<FONT COLOR=\"#FF0000\">"+oldValue+"</FONT>, ";
			}
			result = result.substring(0, result.length()-2);
			result += "], ";
		}
		
		if (newValues.length==1) {
			result += "<FONT COLOR=\"#008000\">"+newValues[0]+"</FONT>)";
		} else {
			result += "[";
			for (String newValue : newValues) {
				result += "<FONT COLOR=\"#008000\">"+newValue+"</FONT>, ";
			}
			result = result.substring(0, result.length()-2);
			result += "])";
		}
		
		return result;
	}
}