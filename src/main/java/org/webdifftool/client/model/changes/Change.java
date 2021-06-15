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

package org.webdifftool.client.model.changes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.webdifftool.client.model.DiffEvolutionMapping;

public class Change {
	public String name;
	public String id;
	public List<String> mapsTo;
	public List<String[]> values;
	public boolean isHighLevel;
	public Map<String,String> accToNames;
	
	public Change() {
		this.mapsTo = new Vector<String>();
	}
	
	public Change(String id, String name, List<String[]> values) {
		this.name = name;
		this.id = id;
		this.values = values;
		this.mapsTo = new Vector<String>();
		this.isHighLevel = false;
	}
	
	public String toString() {
		return "<html>"+this.getSimpleHTMLRepresenation()+"</html>";
		/*String result = /*this.id+": "+*\/this.name+"(";
		for (String[] value : values) {
			if (value.length==1) {
				result += value[0]+",";
			} else {
				result += Arrays.toString(value)+",";
			}
		}
		result = result.substring(0,result.length()-1)+")";
		return result;*/
	}
	
	public String exportString() {
		String result = /*this.id+": "+*/this.name+"(";
		for (String[] value : values) {
			if (value.length==1) {
				result += value[0]+",";
			} else {
				result += Arrays.toString(value)+",";
			}
		}
		result = result.substring(0,result.length()-1)+")";
		return result;
	}
	
	public String toCellBrowserString() {
		/*String result = "<i>"+this.name+"</i>"+"(";
		for (String[] value : values) {
			if (value.length==1) {
				result += value[0]+",";
			} else {
				result += Arrays.toString(value)+",";
			}
		}
		result = result.substring(0,result.length()-1)+")";*/
		return this.getSimpleHTMLRepresenation();
	}
	
	public String getSimpleHTMLRepresenation() {
		String result = "<FONT COLOR=\"#800080\"><b>"+this.name+"</b></FONT>(";
		for (String[] value : values) {
			if (value.length==1) {
				result += value[0]+",";
			} else {
				result += Arrays.toString(value)+",";
			}
		}
		result = result.substring(0,result.length()-1)+")";
		return result;
	}
	
	public String getSimpleWordRepresentation() {
		String result = " " + this.name + " ";
		for (String[] value : values) {
			if (value.length==1) {
				result += value[0]+" ";
			} else {
				result += Arrays.toString(value)+" ";
			}
		}
		return result;
	}
	
	public String getParameterValuesAsString() {
		if (values.size()==0) {
			return "()";
		}
		StringBuffer result = new StringBuffer("(");
		for (String[] value : values) {
			if (value.length==1) {
				result.append(value[0]);
			} else {
				result.append(Arrays.toString(value));
			}
			result.append(",");
		}
		result.deleteCharAt(result.length()-1);
		result.append(")");
		return result.toString();
	}
	
	public boolean containsAccessionNumber(String accessionNumber) {
		for (String[] value : values) {
			for (String simpleValue : value) {
				if (simpleValue.equals(accessionNumber))
					return true;
			}
		}
		return false;
	}
	
	public List<String> getAllWords() {
		List<String> allWords = new Vector<String>();
		for (String[] value : values) {
			for (String singleValue : value) {
				String[] words = singleValue.split(" ");
				for (String singleWord : words) {
					allWords.add(singleWord);
				}
			}
		}
		return allWords;
	}
	
	public int getExactOccurence(String key) {
		int occ = 0;
		for (String[] setValue : values) {
			for (String singleValue : setValue) {
				if (singleValue.equalsIgnoreCase(key)) {
					occ++;
				}
			}
		}
		return occ;
	}
	
	public HashMap<String, Integer> getWordOccurences() {
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		for (String[] setValue : values) {
			for (String singleValue : setValue) {
				String[] singleWords = singleValue.split(" ");
				for (String singleWord : singleWords) {
					singleWord = singleWord.toLowerCase();
					if (result.containsKey(singleWord)) {
						result.put(singleWord, result.get(singleWord)+1);
					} else {
						result.put(singleWord, 1);
					}
				}
			}
		}
		return result;
	}
	
	public void buildAccessionToNameMap(Map<String, String> fullMap) {
		this.accToNames = new HashMap<String, String>();
		for (String[] entry : this.values) {
			for (String simpleEntry : entry) {
				if (fullMap.get(simpleEntry)!=null) {
					this.accToNames.put(simpleEntry, fullMap.get(simpleEntry));
				}
			}
		}
	}
	
	public String getNameAsToolTip(String accession) {
		String name = this.accToNames.get(accession);
		if (name!=null) {
			return "title=\""+name+"\"";
		} else {
			return "";
		}
	}
	
	public boolean coversChange(Change change, DiffEvolutionMapping diff) {
		if (this.id==change.id)
			return true;
		if (this.mapsTo==null) {
			return false;
		}
		if (this.mapsTo.contains(change.id)) {
			return true;
		}
		for (String childID : this.mapsTo) {
			if (diff.allChanges.get(childID).coversChange(change, diff)) {
				return true;
			}
		}
		return false;
	}
}
