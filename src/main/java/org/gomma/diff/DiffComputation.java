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

package org.gomma.diff;

import org.gomma.diff.model.ActionData;
import org.semanticweb.owlapi.model.OWLOntology;
import org.webdifftool.client.model.DiffEvolutionMapping;
import org.webdifftool.client.model.changes.Change;
import org.webdifftool.client.model.changes.basic.*;
import org.webdifftool.client.model.changes.complex.*;
import org.webdifftool.server.OWLManager;
import org.webdifftool.server.StopWords;

import java.util.*;

public class DiffComputation {


    public DiffEvolutionMapping computeDiff(OWLOntology first, OWLOntology second) {
        String prefix = String.valueOf(Math.abs(new Random().nextLong()));
        //Globals.addPrefix(prefix);
        DiffExecutor.getSingleton().setupRepository();

        OWLManager owl = new OWLManager();

        System.out.println("Loading ontology versions");
        owl.parseAndIntegrateChanges(first, second);
//		owl.parseAndIntegrateChanges(owl.getOBOContentFromFile(oldVersion), owl.getOBOContentFromFile(newVersion));

        Map<String, String> conceptNames = owl.conceptNames;
        System.out.println("Loading rules");
        this.loadConfigForDiffExecutor();
        System.out.println("Applying rules");
        DiffExecutor.getSingleton().applyRules();
        // currentStatus = "Aggregation of changes";
        System.out.println("Aggregation of changes");
        DiffExecutor.getSingleton().mergeResultActions();
        // currentStatus = "Building of final diff result";
        System.out.println("Building of final diff result");
        DiffExecutor.getSingleton().retrieveAndStoreHighLevelActions();
        DiffEvolutionMapping diffResult = new DiffEvolutionMapping(this.getFullDiffMapping(conceptNames), this.getCompactDiffMapping(), this.getBasicDiffMapping());
        this.computeWordFrequencies(diffResult);
        diffResult.computeDependenciesForBasicChanges();

        // Set version sizes
        diffResult.newVersionConceptSize = owl.newVersionConceptSize;
        diffResult.newVersionRelationshipSize = owl.newVersionRelationshipSize;
        diffResult.newVersionAttributeSize = owl.newVersionAttributeSize;
        diffResult.oldVersionConceptSize = owl.oldVersionConceptSize;
        diffResult.oldVersionRelationshipSize = owl.oldVersionRelationshipSize;
        diffResult.oldVersionAttributeSize = owl.oldVersionAttributeSize;

        // Clean repository
        DiffExecutor.getSingleton().destroyRepository();

        // currentStatus = "Sending results";
        System.out.println("Sending results");
        return diffResult;
    }

    private void loadConfigForDiffExecutor() {
        // System.out.println("Call from: "+getThreadLocalRequest().getRemoteHost());
        DiffExecutor.getSingleton().loadChangeActionDesc("rules/ChangeActions.xml");
        DiffExecutor.getSingleton().loadRules("rules/Rule_OBO.xml");
    }

    private HashMap<String, Change> getFullDiffMapping(Map<String, String> acc2Name) {
        HashMap<String, Change> result = new HashMap<String, Change>();
        DiffExecutor diffExec = DiffExecutor.getSingleton();
        for (ActionData change : diffExec.lowLevelActions) {
            List<String[]> changeValues = new Vector<String[]>();
            for (int i = 0; i < change.dataValues.size(); i++) {
                if (change.changeActionDesc.multipleValues.get(i)) {
                    changeValues.add(change.dataValues.get(i).split("#"));
                } else {
                    changeValues.add(new String[] { change.dataValues.get(i) });
                }
            }

            Change tmpChange = this.getChangeForDiffMapping(change.md5Key, change.changeActionDesc.name, changeValues);
            tmpChange.buildAccessionToNameMap(acc2Name);
            tmpChange.mapsTo = diffExec.allRuleMappings.get(change.md5Key);
            result.put(tmpChange.id, tmpChange);
        }

        for (ActionData change : diffExec.highLevelActions) {
            List<String[]> changeValues = new Vector<String[]>();
            for (int i = 0; i < change.dataValues.size(); i++) {
                if (change.changeActionDesc.multipleValues.get(i)) {
                    changeValues.add(change.dataValues.get(i).split("#"));
                } else {
                    changeValues.add(new String[] { change.dataValues.get(i) });
                }
            }

            Change tmpChange = this.getChangeForDiffMapping(change.md5Key, change.changeActionDesc.name, changeValues);
            tmpChange.buildAccessionToNameMap(acc2Name);
            tmpChange.mapsTo = diffExec.allRuleMappings.get(change.md5Key);
            result.put(tmpChange.id, tmpChange);

        }

        return result;
    }

    private void computeWordFrequencies(DiffEvolutionMapping diffResult) {
        // String fullText = diffResult.getFulltextOfCompactDiff();
        // System.out.println(fullText);
        Map<String, Integer> wordFrequencies = new HashMap<String, Integer>();
        List<String> changeTypes = diffResult.getChangeTypesFromCompactMapping();
        StopWords stop = new StopWords();
        // String[] allWords = fullText.split(" ");
        for (Change chg : diffResult.getAllChangesFromBasicMapping()) {
            if (chg.name.equalsIgnoreCase("addA") || chg.equals("delA")) {
                // List<String> wordsOfChange = chg.getAllWords();
                String[] wordsOfChange = chg.values.get(2)[0].split("[ _]");
                for (String word : wordsOfChange) {
                    word = word.replaceAll("\\[", "");
                    word = word.replaceAll("\\]", "");
                    word = word.replaceAll("\\(", "");
                    word = word.replaceAll("\\)", "");
                    word = word.replaceAll("\\.", "");
                    word = word.replaceAll("\\,", "");
                    word = word.replaceAll("\\;", "");
                    word = word.replaceAll("\\_", " ");
                    // word = word.trim();
                    // word = word.toLowerCase();

                    if (!word.contains(":") && !(word.length() <= 2) && !changeTypes.contains(word) && !stop.is(word)) {
                        if (wordFrequencies.containsKey(word)) {
                            wordFrequencies.put(word, wordFrequencies.get(word) + 1);
                        } else {
                            wordFrequencies.put(word, 1);
                        }
                    }
                }
            }
        }

        diffResult.wordFrequencies = wordFrequencies;
        // System.out.println(wordFrequencies);
    }

    private Change getChangeForDiffMapping(String md5Key, String name, List<String[]> changeValues) {
        if (name.equalsIgnoreCase("addA")) {
            return new AddAttribute(md5Key, name, changeValues);
        } else if (name.equalsIgnoreCase("delA")) {
            return new DelAttribute(md5Key, name, changeValues);
        } else if (name.equalsIgnoreCase("addR")) {
            return new AddRelationship(md5Key, name, changeValues);
        } else if (name.equalsIgnoreCase("delR")) {
            return new DelRelationship(md5Key, name, changeValues);
        } else if (name.equalsIgnoreCase("addC")) {
            return new AddConcept(md5Key, name, changeValues);
        } else if (name.equalsIgnoreCase("delC")) {
            return new DelConcept(md5Key, name, changeValues);
        } else if (name.equalsIgnoreCase("mapC")) {
            return new MapConcept(md5Key, name, changeValues);
        } else if (name.equalsIgnoreCase("addSubGraph")) {
            return new AddSubGraph(md5Key, name, changeValues);
        } else if (name.equalsIgnoreCase("delSubGraph")) {
            return new DelSubGraph(md5Key, name, changeValues);
        } else if (name.equalsIgnoreCase("addInner")) {
            return new AddInner(md5Key, name, changeValues);
        } else if (name.equalsIgnoreCase("addLeaf")) {
            return new AddLeaf(md5Key, name, changeValues);
        } else if (name.equalsIgnoreCase("delInner")) {
            return new DelInner(md5Key, name, changeValues);
        } else if (name.equalsIgnoreCase("delLeaf")) {
            return new DelLeaf(md5Key, name, changeValues);
        } else if (name.equalsIgnoreCase("merge")) {
            return new Merge(md5Key, name, changeValues);
        } else if (name.equalsIgnoreCase("split")) {
            return new Split(md5Key, name, changeValues);
        } else if (name.equalsIgnoreCase("substitute")) {
            return new Substitute(md5Key, name, changeValues);
        } else if (name.equalsIgnoreCase("toObsolete")) {
            return new ToObsolete(md5Key, name, changeValues);
        } else if (name.equalsIgnoreCase("revokeObsolete")) {
            return new RevokeObsolete(md5Key, name, changeValues);
        } else if (name.equalsIgnoreCase("chgAttValue")) {
            return new ChgAttValue(md5Key, name, changeValues);
        } else if (name.equalsIgnoreCase("move")) {
            return new Move(md5Key, name, changeValues);
        } else if (name.equalsIgnoreCase("mapR")) {
            return new MapRelationship(md5Key, name, changeValues);
        }
        return new Change(md5Key, name, changeValues);
    }

    private Map<String, List<String>> getCompactDiffMapping() {
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        Set<String> allDependantChanges = new HashSet<String>();
        DiffExecutor diffExec = DiffExecutor.getSingleton();

        for (String from : diffExec.allRuleMappings.keySet()) {
            allDependantChanges.addAll(diffExec.allRuleMappings.get(from));
        }

        for (ActionData change : diffExec.lowLevelActions) {
            if (!allDependantChanges.contains(change.md5Key)) {
                List<String> currentChanges = result.get(change.changeActionDesc.name);
                if (currentChanges == null) {
                    currentChanges = new Vector<String>();
                }
                currentChanges.add(change.md5Key);
                result.put(change.changeActionDesc.name, currentChanges);
            }
        }

        for (ActionData change : diffExec.highLevelActions) {
            if (!allDependantChanges.contains(change.md5Key)) {
                List<String> currentChanges = result.get(change.changeActionDesc.name);
                if (currentChanges == null) {
                    currentChanges = new Vector<String>();
                }
                currentChanges.add(change.md5Key);
                result.put(change.changeActionDesc.name, currentChanges);
            }
        }
        return result;
    }

    private Map<String, List<String>> getBasicDiffMapping() {
        HashMap<String, List<String>> result = new HashMap<String, List<String>>();
        DiffExecutor diffExec = DiffExecutor.getSingleton();

        for (ActionData change : diffExec.lowLevelActions) {
            List<String> currentChanges = result.get(change.changeActionDesc.name);
            if (currentChanges == null) {
                currentChanges = new Vector<String>();
            }
            currentChanges.add(change.md5Key);
            result.put(change.changeActionDesc.name, currentChanges);
        }
        return result;
    }
}
