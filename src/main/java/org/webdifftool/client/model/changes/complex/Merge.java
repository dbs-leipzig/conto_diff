/*
 *
 *  * Copyright © 2014 - 2021 Leipzig University (Database Research Group)
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, version 3.
 *  *
 *  * This program is distributed in the hope that it will be useful, but
 *  * WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  * General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.webdifftool.client.model.changes.complex;
 
import java.util.List;

public class Merge extends ComplexChange
{

	public Merge()
	{
		super();
	}

	public Merge(String id, String name, List<String[]> changeValues)
	{
		super(id, name, changeValues);
	}

	public String getSimpleHTMLRepresenation()
	{
		String result = "<FONT COLOR=\"#800080\"><b>" + this.name + "</b></FONT>(";
		String[] oldConcepts = values.get(0);
		String newConcept = values.get(1)[0];

		if (oldConcepts.length == 1)
		{
			result += "<FONT COLOR=\"#FF0000\"><i " + this.getNameAsToolTip(oldConcepts[0]) + ">" + oldConcepts[0] + "</i></FONT>, ";
		} else
		{
			result += "[";
			for (String oldValue : oldConcepts)
			{
				result += "<FONT COLOR=\"#FF0000\"><i " + this.getNameAsToolTip(oldValue) + ">" + oldValue + "</i></FONT>, ";
			}
			result = result.substring(0, result.length()-2);
			result += "], ";
		}

		result += "<FONT COLOR=\"#008000\"><i " + this.getNameAsToolTip(newConcept) + ">" + newConcept + "</i></FONT> )";

		return result;
	}
}