package org.gomma.diff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.gomma.diff.model.ActionData;
import org.gomma.diff.model.ActionDesc;
import org.gomma.diff.model.InputAction;
import org.gomma.diff.model.Rule;
import org.gomma.diff.model.Type;
import org.gomma.diff.parser.ActionParser;
import org.gomma.diff.parser.RuleParser;
import org.gomma.diff.utils.DataBaseHandler;
import org.gomma.diff.utils.Utils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class DiffExecutor {
	
	private static DiffExecutor singleton;
	
	public List<String> allPrimitiveTypes;
	public List<Type> allRegisteredTypes;
	
	public HashMap<String,ActionDesc> actionDesc;
	public List<Rule> rules;
	
	public HashMap<String,ActionData> allGeneratedResultActions;
	public List<ActionData> newlyGeneratedResultActions;
	
	public HashMap<String,ActionData> allReducedChangeActionData;
	
	public HashMap<String, List<String>> allRuleMappings;
	public List<ActionData> highLevelActions;
	public List<ActionData> lowLevelActions;
	
	public int round = -1;
	
	public DiffExecutor() {
		this.rules = new Vector<Rule>();
		this.actionDesc = new HashMap<String, ActionDesc>();
	}
	
	public static DiffExecutor getSingleton() {
		if (singleton==null) {
			singleton = new DiffExecutor();
		}
		return singleton;
	}
	
	public void setupRepository() {
		try {
			DataBaseHandler.getInstance().executeDml(Globals.VERSION_TABLE_SCHEMA);
			DataBaseHandler.getInstance().executeDml(Globals.WORKING_TABLE_SCHEMA);
			DataBaseHandler.getInstance().executeDml(Globals.HIGH_LEVEL_RESULT_SCHEMA);
			DataBaseHandler.getInstance().executeDml(Globals.LOW_LEVEL_RESULT_SCHEMA);
			DataBaseHandler.getInstance().executeDml(Globals.HIGH_TO_LOW_LEVEL_MAPPING_SCHEMA);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void destroyRepository() {
		try {
			DataBaseHandler.getInstance().executeDml("DROP TABLE IF EXISTS "+Globals.VERSION_TABLE);
			DataBaseHandler.getInstance().executeDml("DROP TABLE IF EXISTS "+Globals.WORKING_TABLE);
			DataBaseHandler.getInstance().executeDml("DROP TABLE IF EXISTS "+Globals.HIGH_LEVEL_RESULT_TABLE);
			DataBaseHandler.getInstance().executeDml("DROP TABLE IF EXISTS "+Globals.LOW_LEVEL_RESULT_TABLE);
			DataBaseHandler.getInstance().executeDml("DROP TABLE IF EXISTS "+Globals.HIGH_TO_LOW_LEVEL_MAPPING_TABLE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void integrateVersionData(String sqlStatementsFile) {
		System.out.print("Integrating ontology data ...  ");
		long start = System.currentTimeMillis();
		try {
			DataBaseHandler.getInstance().executeDml("TRUNCATE TABLE "+Globals.VERSION_TABLE);
			DataBaseHandler.getInstance().executeDml("TRUNCATE TABLE "+Globals.WORKING_TABLE);
			RandomAccessFile file = new RandomAccessFile(sqlStatementsFile,"r");
			String line;
			String currentQuery = "";
			while ((line=file.readLine())!=null) {
				line = line.trim();
				currentQuery += " "+line;
				if (line.endsWith(";")) {
					DataBaseHandler.getInstance().executeDml(currentQuery);
					currentQuery = "";
				}
			}
			if (currentQuery.trim().length()>0) {
				DataBaseHandler.getInstance().executeDml(currentQuery);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(" Done ! ("+(System.currentTimeMillis()-start)+" ms)");
	}
	
	public void loadChangeActionDesc(String fileLocation) {
		ActionParser p = new ActionParser(fileLocation);
		this.importChangeActions(fileLocation, p);
		for (int i=0;i<p.changeActionDescToImport.size();i++) {
			ActionDesc currentChangeActionDesc = p.changeActionDescToImport.get(i);
			this.actionDesc.put(currentChangeActionDesc.name, currentChangeActionDesc);
		}
		this.allPrimitiveTypes = p.primTypes;
		this.allRegisteredTypes = p.availableTypes;
	}
	
	public void loadRules(String fileLocation) {
		RuleParser p = new RuleParser(fileLocation);
		p.importRules();
		this.rules = p.rulesToImport;
		for (int i=0;i<this.rules.size();i++) {
			this.rules.get(i).buildSQLStatements();
		}
	}

	public void importChangeActions(String changeActionDescFileLocation, ActionParser p) {
		try {
			long start, duration;
			start = System.currentTimeMillis();
			System.out.print("Importing change actions ");
			SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
			File f = new File(changeActionDescFileLocation);
			System.out.println("test "+ f.getAbsolutePath());
			saxParser.parse( f, p);
			duration = (System.currentTimeMillis()-start);
			System.out.println(" ...   "+p.availableTypes.size()+" types and "+
					p.changeActionDescToImport.size()+" change action descriptions parsed ! ("+duration+" ms)");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void computeBasicChanges() {
		this.computeBasicChanges(true);
	}
	
	
	public void computeBasicChanges(boolean cleanTable) {
		try {
			System.out.print("Computation of basic changes ...  ");
			long start = System.currentTimeMillis();
			if (cleanTable) {
				DataBaseHandler.getInstance().executeDml("TRUNCATE TABLE "+Globals.WORKING_TABLE);
			}
			for (int i=0;i<this.allRegisteredTypes.size();i++) {
				Type currentType = this.allRegisteredTypes.get(i);
				String addQuery = this.getQueryForAdd(currentType);
				String delQuery = this.getQueryForDel(currentType);
				DataBaseHandler.getInstance().executeDml(addQuery);
				DataBaseHandler.getInstance().executeDml(delQuery);
			}
			System.out.println(" Done ! ("+(System.currentTimeMillis()-start)+" ms)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void applyRules() {
		this.allGeneratedResultActions = new HashMap<String,ActionData>();
		this.newlyGeneratedResultActions = new Vector<ActionData>();
		this.allReducedChangeActionData = new HashMap<String,ActionData>();
		this.allRuleMappings = new HashMap<String, List<String>>();
		
		markingSameActions();
		
		round = 1;
		do {
			long start = System.currentTimeMillis();
			System.out.print("Applying rules - Round "+round+" ... ");
			newlyGeneratedResultActions.clear();
			for (int i=0;i<this.rules.size();i++) {
				Rule r = this.rules.get(i);
				if (r.applyUntilRound>=this.round) {
					this.applyRule(r);
				}
				
			}
			this.insertNewlyGeneratedResultActions();
			System.out.println("Done ! ("+(System.currentTimeMillis()-start)+" ms)");
			round++;
		} while (newlyGeneratedResultActions.size()>0);
		round = -1;
		long start = System.currentTimeMillis();
		System.out.print("Marking redundant change actions ... ");
		this.markingRedundantChangeActions();
		System.out.println("Done ! ("+(System.currentTimeMillis()-start)+" ms)");
	}
	
	public void mergeResultActions() {
		long start = System.currentTimeMillis();
		System.out.print("Merging actions ... ");
		ResultSet rs;
		for (String key : this.actionDesc.keySet()) {
			ActionDesc currentActionDesc = this.getChangeActionDesc(key);
			if (currentActionDesc.isMergeable()) {
				StringBuffer query = new StringBuffer();
				query.append("SELECT change_action,GROUP_CONCAT(actionMD5 SEPARATOR '"+Globals.DELIMITER+"')");
				for (int j=0;j<currentActionDesc.paramTypes.size();j++) {
					if (currentActionDesc.multipleValues.get(j)) {
						query.append(",GROUP_CONCAT(value"+(j+1)+" SEPARATOR '"+Globals.DELIMITER+"')");
					} else {
						query.append(",value"+(j+1));
					}
				}
				query.append(" FROM "+Globals.WORKING_TABLE+" WHERE change_action='"+currentActionDesc.name+"' AND reduce=0 GROUP BY change_action");
				for (int j=0;j<currentActionDesc.multipleValues.size();j++) {
					if (!currentActionDesc.multipleValues.get(j)) {
						query.append(",value"+(j+1));
					}
				}
				try {
					DataBaseHandler.getInstance().executeDml("SET @group_concat_max_len = 4096");
					rs = DataBaseHandler.getInstance().executeSelect(query.toString());
					List<ActionData> allMergedActions = new Vector<ActionData>();
					while (rs.next()) {
						int currentIndex = 2;
						String inputActions = rs.getString(currentIndex++);
						ActionData currentActionData = new ActionData(currentActionDesc);
						
						for (int j=0;j<currentActionDesc.paramTypes.size();j++) {
							String currentValue = rs.getString(currentIndex++);
							if (currentActionDesc.multipleValues.get(j)) {
								currentValue = Utils.eleminateDuplicates(currentValue);
							}
							currentActionData.addDataValue(currentValue);
						}
						currentActionData.computeDerivedFrom(inputActions);
						allMergedActions.add(currentActionData);
					}
					rs.close();
					//DataBaseHandler.getInstance().executeDml("DELETE FROM "+DiffExecutor.WORKING_TABLE+" WHERE change_action='"+currentActionDesc.name+"'");
					if (currentActionDesc.level>1) {
						DataBaseHandler.getInstance().executeDml("UPDATE "+Globals.WORKING_TABLE+" SET reduce='1' WHERE change_action='"+currentActionDesc.name+"'");
					}
					for (int j=0;j<allMergedActions.size();j++) {
						ActionData actionData = allMergedActions.get(j);
						if (actionData.isDerivedFrom()) {
							this.registerInRuleMapping(actionData);
						}
						String insert = "MERGE INTO "+Globals.WORKING_TABLE+" (actionMD5,change_action,reduce";
						for (int k=0;k<actionData.dataValues.size();k++) {
							insert += ",value"+(k+1);
						}
						insert += ") KEY (actionMD5) VALUES ('"+actionData.getMD5()+"','"+currentActionDesc.name+"','0'";
						for (int k=0;k<actionData.dataValues.size();k++) {
							insert += ",'"+actionData.dataValues.get(k).replace("'", "''")+"'";
						}
						insert += ")";
						DataBaseHandler.getInstance().executeDml(insert);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("Done ! ("+(System.currentTimeMillis()-start)+" ms)");
	}
	
	public void retrieveAndStoreHighLevelActions() {
		try {
			long start = System.currentTimeMillis();
			System.out.print("Building final results ... ");
			
			highLevelActions = new Vector<ActionData>();
			lowLevelActions = new Vector<ActionData>();
			
			for (String actionDescKey : this.actionDesc.keySet()) {
				ActionDesc currentActionDesc = getChangeActionDesc(actionDescKey);
				
				StringBuffer query = new StringBuffer();
				query.append("SELECT actionMD5");
				for (int j=0;j<currentActionDesc.paramTypes.size();j++) {
					query.append(",value"+(j+1));
				}
				query.append(" FROM "+Globals.WORKING_TABLE+" WHERE change_action='"+currentActionDesc.name+"' AND reduce=0");
				ResultSet rs = DataBaseHandler.getInstance().executeSelect(query.toString());
				while (rs.next()) {
					int currentIndex = 1;
					ActionData currentActionData = new ActionData(currentActionDesc);
					currentActionData.md5Key = rs.getString(currentIndex++);
					for (int j=0;j<currentActionDesc.paramTypes.size();j++) {
						String currentValue = rs.getString(currentIndex++);
						currentActionData.addDataValue(currentValue);
					}
					if (currentActionDesc.level>1) {
						highLevelActions.add(currentActionData);
					} else {
						lowLevelActions.add(currentActionData);
					}
				}
				rs.close();
			}
			/*
			HashMap<String,List<String>> highToLowLevelMapping = new HashMap<String, List<String>>();
			Set<String> lowLevelActionMD5s = new HashSet<String>();
			for (ActionData highLevelAction : highLevelActions) {
				List<String> baseActions = new Vector<String>();
				this.getHighToLowLevelMap(highLevelAction.getMD5(), baseActions);
				highToLowLevelMapping.put(highLevelAction.getMD5(), baseActions);
				lowLevelActionMD5s.addAll(baseActions);
			}
			
			StringBuffer query = new StringBuffer("SELECT actionMD5,change_action,value1,value2,value3,value4,value5,value6 FROM "+Globals.WORKING_TABLE+" WHERE actionMD5 IN ('X'");
			for (String lowLevelActionMD5 : lowLevelActionMD5s) {
				query.append(",'"+lowLevelActionMD5+"'");
			}
			query.append(")");
			ResultSet rs = DataBaseHandler.getInstance().executeSelect(query.toString());
			while (rs.next()) {
				ActionData acionData = new ActionData(this.getChangeActionDesc(rs.getString(2)));
				acionData.md5Key = rs.getString(1);
				int currentIndex = 3;
				for (int i=0;i<acionData.changeActionDesc.paramTypes.size();i++) {
					acionData.addDataValue(rs.getString(currentIndex++));
				}
				lowLevelActions.add(acionData);
			}*/
			
			insertHighLevelResultActions(highLevelActions);
			insertLowLevelResultActions(lowLevelActions);
			//insertHighToLowLevelMapping(highToLowLevelMapping);
			insertHighToLowLevelMapping(this.allRuleMappings);
			System.out.println("Done ! ("+(System.currentTimeMillis()-start)+" ms)");
 		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void reduceResultActions() {
		try {
			long start = System.currentTimeMillis();
			System.out.print("Deleting redundant actions ... ");
			String query = "DELETE FROM "+Globals.WORKING_TABLE+" WHERE reduce=1";
			DataBaseHandler.getInstance().executeDml(query);
			System.out.println("Done ! ("+(System.currentTimeMillis()-start)+" ms)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ActionDesc getChangeActionDesc(String name) {
		return this.actionDesc.get(name);
	}
	

	
	private String getQueryForAdd(Type type) {
		StringBuffer query = new StringBuffer("INSERT INTO "+Globals.WORKING_TABLE+" (actionMD5,change_action,reduce");
		for (int j=0;j<type.containedPrimTypes.size();j++) {
			query.append(",value"+(j+1));
		}
		query.append(") SELECT MD5(CONCAT('add"+type.name+"'");
		for (int j=0;j<type.containedPrimTypes.size();j++) {
			query.append(",value"+(j+1));
		}
		query.append(")),'add"+type.name+"',0");
		for (int j=0;j<type.containedPrimTypes.size();j++) {
			query.append(",a.value"+(j+1));
		}
		query.append(" FROM "+Globals.VERSION_TABLE+" a WHERE a.version='new' AND a.type='"+type.name+"' AND a.values_md5");
		query.append(" NOT IN (SELECT b.values_md5");
		query.append(" FROM "+Globals.VERSION_TABLE+" b WHERE b.version='old' AND b.type='"+type.name+"')");
		return query.toString();
	}

	private String getQueryForDel(Type type) {
		StringBuffer query = new StringBuffer("INSERT INTO "+Globals.WORKING_TABLE+" (actionMD5,change_action,reduce");
		for (int j=0;j<type.containedPrimTypes.size();j++) {
			query.append(",value"+(j+1));
		}
		query.append(") SELECT MD5(CONCAT('del"+type.name+"'");
		for (int j=0;j<type.containedPrimTypes.size();j++) {
			query.append(",value"+(j+1));
		}
		query.append(")),'del"+type.name+"',0");
		for (int j=0;j<type.containedPrimTypes.size();j++) {
			query.append(",a.value"+(j+1));
		}
		query.append(" FROM "+Globals.VERSION_TABLE+" a WHERE a.version='old' AND a.type='"+type.name+"' AND a.values_md5");
		query.append(" NOT IN (SELECT b.values_md5");
		query.append(" FROM "+Globals.VERSION_TABLE+" b WHERE b.version='new' AND b.type='"+type.name+"')");
		return query.toString();
	}

	private void applyRule(Rule r) {
		try {
			ResultSet rs;
			/*if (r.resultActionSQLStatement!=null) {
				rs = DataBaseHandler.getInstance().executeSelect(r.resultActionSQLStatement);
				while (rs.next()) {
					ResultActionData data = new ResultActionData(r.resultAction);
					for (int i=0;i<r.resultAction.resultVariables.size();i++) {
						data.addDataValue(rs.getString(i+1));
					}
					String dataMD5 = data.getMD5();
					if (!this.allGeneratedResultActions.containsKey(dataMD5)) {
						this.newlyGeneratedResultActions.add(data);
						this.allGeneratedResultActions.put(dataMD5,data);
					}
				}
				rs.close();
			}*/
			
			if (r.fullActionSQLStatement!=null) {
				rs = DataBaseHandler.getInstance().executeSelect(r.fullActionSQLStatement);
				List<InputAction> compositeActions = r.getCompositeInputActions();
				
				while (rs.next()) {
					//resultierende Action erzeugen
					int currentIndex = 1;
					ActionData resultData = new ActionData(r.resultAction.actionDesc);
					for (int i=0;i<r.resultAction.resultVariables.size();i++) {
						resultData.addDataValue(rs.getString(currentIndex++));
					}
					String resultDataMD5 = resultData.getMD5();
					if (!this.allGeneratedResultActions.containsKey(resultDataMD5)) {
						this.newlyGeneratedResultActions.add(resultData);
						this.allGeneratedResultActions.put(resultDataMD5,resultData);
					}
					//Mapping auf input actions berechnen
					List<String> resultDataInputActions = allRuleMappings.get(resultDataMD5);
					if (resultDataInputActions==null) {
						resultDataInputActions = new Vector<String>();
					}
					for (InputAction inputAction : compositeActions) {
						ActionData inputData = new ActionData(inputAction.actionDesc);
						for (int i=0;i<inputAction.paramVariables.size();i++) {
							inputData.addDataValue(rs.getString(currentIndex++));
						}
						String inputDataMD5 = inputData.getMD5();
						if (!resultDataInputActions.contains(inputDataMD5)&&!resultDataMD5.equals(inputDataMD5)) {
							resultDataInputActions.add(inputData.getMD5());
						}
					}
					allRuleMappings.put(resultDataMD5, resultDataInputActions);
				}
				rs.close();
			}
			
			
			for (int i=0;i<r.reduceActionSQLStatements.size();i++) {
				rs = DataBaseHandler.getInstance().executeSelect(r.reduceActionSQLStatements.get(i));
				while (rs.next()) {
					InputAction inputAction = r.reduceInputActions.get(i);
					ActionData data = new ActionData(inputAction.actionDesc);
					for (int j=0;j<inputAction.paramVariables.size();j++) {
						data.addDataValue(rs.getString(j+1));
					}
					this.allReducedChangeActionData.put(data.getMD5(), data);
					
					//High-to-low-level Mapping anpassen
					//1. Hat zu l�schende Aktion Vorg�nger. Falls ja: child_md5s
					//2. Finde alle Actions, welche zu l�schende Action als child besitzt
					//	2a. Entferne Zuordnung aus Mapping und ordne ggf. child_md5s zu
					List<String> reduceActionChilds = this.allRuleMappings.get(data.getMD5());
					if (reduceActionChilds==null) {
						reduceActionChilds = new Vector<String>();
					}
					for (String md5Key : this.allRuleMappings.keySet()) {
						List<String> childMD5s = this.allRuleMappings.get(md5Key);
						if (childMD5s.contains(data.getMD5())) {
							childMD5s.remove(data.getMD5());
							for (String reduceChild : reduceActionChilds) {
								if (!childMD5s.contains(reduceChild)) {
									childMD5s.add(reduceChild);
								}
							}
						}
					}
				}
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void registerInRuleMapping(ActionData changeAction) {
		List<String> alreadyRegistered = this.allRuleMappings.get(changeAction.getMD5());
		if (alreadyRegistered==null) {
			alreadyRegistered = new Vector<String>();
		}
		
		if (changeAction.changeActionDesc.level>1) {
			for (String md5_derived : changeAction.derivedFrom) {
				List<String> derivedActions = this.allRuleMappings.get(md5_derived);
				if(derivedActions!=null){
					for (String derivedAction : derivedActions) {
						if (!alreadyRegistered.contains(derivedAction)) {
							alreadyRegistered.add(derivedAction);
						}
					}
				}			
			}
		} else {
			alreadyRegistered.addAll(changeAction.derivedFrom);
		}
		
		this.allRuleMappings.put(changeAction.getMD5(), alreadyRegistered);
		
		//Mapping-Anpassung f�r schon existierende Change-Actions n�tig
		//1. Finde Change-Actions welche alle Kinder der neuen Action enthalten
		//2. Falls ja l�sche diese Kinder aus Mapping und ersetze diese durch die neue Action
		for (String md5Key : this.allRuleMappings.keySet()) {
			if (!md5Key.equals(changeAction.getMD5())) {
				List<String> currentChilds = this.allRuleMappings.get(md5Key);
				if (currentChilds.containsAll(changeAction.derivedFrom)) {
					currentChilds.removeAll(changeAction.derivedFrom);
					currentChilds.add(changeAction.getMD5());
				}
			}
		}
		
	}
	
	private void insertNewlyGeneratedResultActions() {
		for (int i=0;i<newlyGeneratedResultActions.size();i++) {
			ActionData action = newlyGeneratedResultActions.get(i);
			String  name = action.changeActionDesc.name;
			int size = action.dataValues.size();
			String insert = "INSERT INTO "+Globals.WORKING_TABLE+" (actionMD5,change_action";
			for (int j=1;j<=size;j++) {
				insert += ",value"+j;
			}
			insert += ") VALUES ('"+action.getMD5()+"','"+name+"'";
			for (int j=0;j<size;j++) {
				if (action.dataValues.get(j).contains("'")) {
					System.out.println(action.dataValues.get(j));
				}
				insert += ",'"+action.dataValues.get(j).replace("'", "''")+"'";
			}
			insert += ")";
			try {
				DataBaseHandler.getInstance().executeDml(insert);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	@Deprecated
	private void getHighToLowLevelMap(String highLevelActionMD5,List<String> lowLevelMD5s) {
		List<String> allSubActions = this.allRuleMappings.get(highLevelActionMD5);
		if (allSubActions!=null&&allSubActions.size()>0) {
			for (int i=0;i<allSubActions.size();i++) {
				getHighToLowLevelMap(allSubActions.get(i), lowLevelMD5s);
			}
		} else {
			lowLevelMD5s.add(highLevelActionMD5);
		}
	}

	private void insertHighLevelResultActions(List<ActionData> actions) {
		try {
			DataBaseHandler.getInstance().executeDml("TRUNCATE TABLE "+Globals.HIGH_LEVEL_RESULT_TABLE);
			for (int i=0;i<actions.size();i++) {
				ActionData action = actions.get(i);
				String name = action.changeActionDesc.name;
				int size = action.dataValues.size();
				String insert = "INSERT INTO "+Globals.HIGH_LEVEL_RESULT_TABLE+" (actionMD5,change_action";
				for (int j=1;j<=size;j++) {
					insert += ",value"+j;
				}
				insert += ") VALUES ('"+action.getMD5()+"','"+name+"'";
				for (int j=0;j<size;j++) {
					insert += ",'"+action.dataValues.get(j).replace("'", "''")+"'";
				}
				insert += ")";
				DataBaseHandler.getInstance().executeDml(insert);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void insertLowLevelResultActions(List<ActionData> actions) {
		try {
			DataBaseHandler.getInstance().executeDml("TRUNCATE TABLE "+Globals.LOW_LEVEL_RESULT_TABLE);
			for (int i=0;i<actions.size();i++) {
				ActionData action = actions.get(i);
				String  name = action.changeActionDesc.name;
				int size = action.dataValues.size();
				String insert = "INSERT INTO "+Globals.LOW_LEVEL_RESULT_TABLE+" (actionMD5,change_action";
				for (int j=1;j<=size;j++) {
					insert += ",value"+j;
				}
				insert += ") VALUES ('"+action.getMD5()+"','"+name+"'";
				for (int j=0;j<size;j++) {
					insert += ",'"+action.dataValues.get(j).replace("'", "''")+"'";
				}
				insert += ")";
				DataBaseHandler.getInstance().executeDml(insert);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void insertHighToLowLevelMapping(HashMap<String,List<String>> map) {
		String query = "INSERT INTO "+Globals.HIGH_TO_LOW_LEVEL_MAPPING_TABLE+" (high_level_action_MD5,low_level_action_MD5) VALUES (?,?)";
		try {
			DataBaseHandler.getInstance().executeDml("TRUNCATE TABLE "+Globals.HIGH_TO_LOW_LEVEL_MAPPING_TABLE);
			PreparedStatement stmt = DataBaseHandler.getInstance().prepareStatement(query);
			for (String highActionMD5 : map.keySet()) {
				List<String> lowActionMD5s = map.get(highActionMD5);
				for (int i=0;i<lowActionMD5s.size();i++) {
					stmt.setString(1, highActionMD5);
					stmt.setString(2, lowActionMD5s.get(i));
					stmt.addBatch();
				}
			}
			stmt.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void markingRedundantChangeActions() {
		String statement = "UPDATE "+Globals.WORKING_TABLE+" SET reduce='1' WHERE actionMD5 IN (";
		
		try {
			StringBuffer md5Actions = new StringBuffer("'X'");
			int count=0;
			for (String md5Key : this.allReducedChangeActionData.keySet()) {
				md5Actions.append(",'"+md5Key+"'");
				count++;
				if (count==20000) {
					DataBaseHandler.getInstance().executeDml(statement+md5Actions.toString()+")");
					md5Actions = new StringBuffer("'X'");
					count=0;
				}
			}
			DataBaseHandler.getInstance().executeDml(statement+md5Actions.toString()+")");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void markingSameActions() {
		List<String> importantObjects = new Vector<String>();
		
		try {
			ResultSet rs = DataBaseHandler.getInstance().executeSelect("SELECT value1,value2 FROM "+Globals.WORKING_TABLE+" WHERE change_action='mapObj' AND value1 != value2");
			while (rs.next()) {
				importantObjects.add(rs.getString(1).trim());
				importantObjects.add(rs.getString(2).trim());
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		StringBuffer queryPart = new StringBuffer("'X'");
		for (int i=0;i<importantObjects.size();i++) {
			queryPart.append(",'"+importantObjects.get(i)+"'");
		}
		String sameObjQuery = "UPDATE "+Globals.WORKING_TABLE+" SET reduce='1' WHERE change_action='mapObj' AND value1=value2 AND (value1 NOT IN ("+queryPart+") OR value2 NOT IN ("+queryPart+"))";
		String sameAssQuery = "UPDATE "+Globals.WORKING_TABLE+" SET reduce='1' WHERE change_action='mapAss' AND value1=value4 AND value2=value5 AND value3=value6";
		String sameAttQuery = "UPDATE "+Globals.WORKING_TABLE+" SET reduce='1' WHERE change_action='mapAtt' AND value1=value4 AND value2=value5 AND value3=value6";
		try {
			DataBaseHandler.getInstance().executeDml(sameObjQuery);
			DataBaseHandler.getInstance().executeDml(sameAssQuery);
			DataBaseHandler.getInstance().executeDml(sameAttQuery);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
