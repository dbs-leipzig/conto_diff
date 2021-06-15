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

package org.webdifftool.client.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.webdifftool.client.model.changes.Change;
import org.webdifftool.client.model.changes.basic.BasicChange;

public class DiffEvolutionMapping {

	public Map<String, Change> allChanges;
	public Map<String, List<String>> diffCompact;
	public Map<String, List<String>> diffBasic;
	public Map<String, Integer> wordFrequencies;
	
	public int oldVersionConceptSize;
	public int oldVersionRelationshipSize;
	public int oldVersionAttributeSize;

	public int newVersionConceptSize;
	public int newVersionRelationshipSize;
	public int newVersionAttributeSize;
	
	public DiffEvolutionMapping() {
		this.allChanges = new HashMap<String, Change>();
		this.diffCompact = new HashMap<String, List<String>>();
		this.diffBasic = new HashMap<String, List<String>>();
		this.wordFrequencies = new HashMap<String, Integer>();
	}
	
	public DiffEvolutionMapping(Map<String,Change> allChanges, Map<String, List<String>> diffCompact, Map<String, List<String>> diffBasic) {
		this.allChanges = allChanges;
		this.diffCompact = diffCompact;
		this.diffBasic = diffBasic;
	}
	
	public int getDiffCompactMappingSize() {
		int result = 0;
		for (String changeType : this.diffCompact.keySet()) {
			result += this.diffCompact.get(changeType).size();
		}
		return result;
	}
	
	public int getDiffBasicMappingSize() {
		int result = 0;
		for (String changeType : this.diffBasic.keySet()) {
			result += this.diffBasic.get(changeType).size();
		}
		return result;
	}
	
	public int getDiffFullMappingSize() {
		return this.allChanges.size();
	}
	
	public List<String> getChangeTypesFromCompactMapping() {
		List<String> result = new Vector<String>(this.diffCompact.keySet());
		Collections.sort(result);
		return result;
	}
	
	public List<String> getChangeTypesFromBasicMapping() {
		return new Vector<String>(this.diffBasic.keySet());
	}
	
	public List<Change> getAllChangesFromCompactMapping(String changeType) {
		List<Change> result = new Vector<Change>();
		List<String> changeIDs = this.diffCompact.get(changeType);
		if (changeIDs==null) {
			return result;
		}
		for (String changeID : changeIDs) {
			result.add(this.allChanges.get(changeID));
		}
		return result;
	}
	
	public List<Change> getAllChangesFromBasicMapping(String changeType) {
		List<Change> result = new Vector<Change>();
		List<String> changeIDs = this.diffBasic.get(changeType);
		if (changeIDs==null) {
			return result;
		}
		for (String changeID : changeIDs) {
			result.add(this.allChanges.get(changeID));
		}
		return result;
	}
	
	public List<Change> getAllChangesFromCompactMapping() {
		List<Change> result = new Vector<Change>();
		
		for (List<String> changeIDs : this.diffCompact.values()) {
			for (String changeID : changeIDs) {
				result.add(this.allChanges.get(changeID));
			}
		}
		return result;
	}
	
	public List<Change> getAllChangesFromBasicMapping() {
		List<Change> result = new Vector<Change>();
		
		for (List<String> changeIDs : this.diffBasic.values()) {
			for (String changeID : changeIDs) {
				result.add(this.allChanges.get(changeID));
			}
		}
		return result;
	}
	
	public List<Change> getDerivedChanges(Change c) {
		List<Change> result = new Vector<Change>();
		for (String changeID : c.mapsTo) {
			result.add(this.allChanges.get(changeID));
		}
		return result;
	}
	
	public String getFulltextOfCompactDiff() {
		StringBuffer fullText = new StringBuffer();
		List<Change> allCompactChanges = this.getAllChangesFromCompactMapping();
		for (Change compactChange : allCompactChanges) {
			fullText.append(compactChange.getSimpleWordRepresentation()+"\n");
		}
		return fullText.toString();
	}
	
	public void computeDependenciesForBasicChanges() {
		for (Change chg : getAllChangesFromCompactMapping()) {
			this.traverseAndAssginDependency(chg,chg);
		}
	}
	
	private void traverseAndAssginDependency(Change topMostChg, Change currentChg) {
		if (currentChg==null) {
			return;
		}
		if (currentChg instanceof BasicChange) {
			BasicChange currentChgAsBasic = (BasicChange)currentChg;
			currentChgAsBasic.belongsTo = topMostChg.id;
		} else {
			if (currentChg.mapsTo!=null) {
				for (String childID : currentChg.mapsTo) {
					traverseAndAssginDependency(topMostChg, this.allChanges.get(childID));
				}
			}
		}
	}
}
