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

public class Globals {
	//DB connection data
	public static final String DB_URL = "jdbc:h2:./COntoDiff;MODE=MYSQL;AUTO_SERVER=TRUE";
	
	//Delimiter for multiple values
	public static String DELIMITER = "#";
	
	//Equation types
	public static String EQUATION_EQUAL = "equal";
	public static String EQUATION_UNEQUAL = "unequal";
	public static String EQUATION_IN_SET = "in";
	
	//Schema information
	public static String VERSION_TABLE_BASE = "evol_versions";
	public static String VERSION_TABLE = "evol_versions";
	public static String WORKING_TABLE_BASE = "evol_change";
	public static String WORKING_TABLE = "evol_change";
	public static String HIGH_LEVEL_RESULT_TABLE_BASE = "high_level_actions";
	public static String HIGH_LEVEL_RESULT_TABLE = "high_level_actions";
	public static String LOW_LEVEL_RESULT_TABLE_BASE = "low_level_actions";
	public static String LOW_LEVEL_RESULT_TABLE = "low_level_actions";
	public static String HIGH_TO_LOW_LEVEL_MAPPING_TABLE_BASE = "high_to_low_mapping";
	public static String HIGH_TO_LOW_LEVEL_MAPPING_TABLE = "high_to_low_mapping";
	
	public static String VERSION_TABLE_SCHEMA = "CREATE TABLE IF NOT EXISTS "+VERSION_TABLE+" ("+
													  "version varchar(20) NOT NULL,"+
													  "type varchar(50) NOT NULL,"+
													  "base_type varchar(50) NOT NULL,"+
													  "values_md5 varchar(50) NOT NULL,"+
													  "value1 varchar(2000) default NULL,"+
													  "value2 varchar(2000) default NULL,"+
													  "value3 varchar(2000) default NULL,"+
													  "value4 varchar(2000) default NULL,"+
													  "value5 varchar(2000) default NULL,"+
													  "value6 varchar(2000) default NULL,"+
													  "KEY ver_version (version,base_type,type),"+
													  "KEY ver_value1 (value1),"+
													  "KEY ver_value2 (value2),"+
													  "KEY ver_value3 (value3),"+
													  "KEY ver_value4 (value4),"+
													  "KEY ver_value5 (value5),"+
													  "KEY ver_value6 (value6),"+
													  "KEY ver_version_2 (version,type,base_type,values_md5),"+
													  "KEY ver_values_md5 (values_md5))";
	public static String WORKING_TABLE_SCHEMA = "CREATE TABLE IF NOT EXISTS "+WORKING_TABLE+" ("+
													  "actionMD5 varchar(50) default NULL,"+
													  "change_action varchar(50) NOT NULL,"+
													  "reduce int(11) default '0',"+
													  "value1 varchar(2000) default NULL,"+
													  "value2 varchar(6000) default NULL,"+
													  "value3 varchar(6000) default NULL,"+
													  "value4 varchar(2000) default NULL,"+
													  "value5 varchar(2000) default NULL,"+
													  "value6 varchar(2000) default NULL,"+
													  "UNIQUE KEY wor_actionMD5 (actionMD5),"+
													  "KEY wor_change_action (change_action),"+
													  "KEY wor_reduce (reduce),"+
													  "KEY wor_value1 (value1),"+
													  "KEY wor_value2 (value2),"+
													  "KEY wor_value3 (value3),"+
													  "KEY wor_value4 (value4),"+
													  "KEY wor_value5 (value5),"+
													  "KEY wor_value6 (value6))";
	public static String HIGH_LEVEL_RESULT_SCHEMA = "CREATE TABLE IF NOT EXISTS "+HIGH_LEVEL_RESULT_TABLE+" ("+
													  "actionMD5 varchar(50) default NULL,"+
													  "change_action varchar(50) NOT NULL,"+
													  "value1 varchar(2000) default NULL,"+
													  "value2 varchar(6000) default NULL,"+
													  "value3 varchar(6000) default NULL,"+
													  "value4 varchar(2000) default NULL,"+
													  "value5 varchar(2000) default NULL,"+
													  "value6 varchar(2000) default NULL,"+
													  "UNIQUE KEY hlv_actionMD5 (actionMD5),"+
													  "KEY hlv_change_action (change_action),"+
													  "KEY hlv_value1 (value1),"+
													  "KEY hlv_value2 (value2),"+
													  "KEY hlv_value3 (value3),"+
													  "KEY hlv_value4 (value4),"+
													  "KEY hlv_value5 (value5),"+
													  "KEY hlv_value6 (value6))";
	public static String LOW_LEVEL_RESULT_SCHEMA = "CREATE TABLE IF NOT EXISTS "+LOW_LEVEL_RESULT_TABLE+" ("+
													  "actionMD5 varchar(50) default NULL,"+
													  "change_action varchar(50) NOT NULL,"+
													  "value1 varchar(2000) default NULL,"+
													  "value2 varchar(2000) default NULL,"+
													  "value3 varchar(6000) default NULL,"+
													  "value4 varchar(2000) default NULL,"+
													  "value5 varchar(2000) default NULL,"+
													  "value6 varchar(2000) default NULL,"+
													  "UNIQUE KEY llv_actionMD5 (actionMD5),"+
													  "KEY llv_change_action (change_action),"+
													  "KEY llv_value1 (value1),"+
													  "KEY llv_value2 (value2),"+
													  "KEY llv_value3 (value3),"+
													  "KEY llv_value4 (value4),"+
													  "KEY llv_value5 (value5),"+
													  "KEY llv_value6 (value6))";
	public static String HIGH_TO_LOW_LEVEL_MAPPING_SCHEMA = "CREATE TABLE IF NOT EXISTS "+HIGH_TO_LOW_LEVEL_MAPPING_TABLE+" ("+
													  "high_level_action_MD5 varchar(50) NOT NULL,"+
													  "low_level_action_MD5 varchar(50) NOT NULL,"+
													  "KEY htl_high_level_action_MD5 (high_level_action_MD5),"+
													  "KEY htl_low_level_action_MD5 (low_level_action_MD5))";
	
	public static void addPrefix(String prefix) {
		VERSION_TABLE = "P" + prefix + "_" + VERSION_TABLE_BASE;
		WORKING_TABLE = "P" + prefix + "_" + WORKING_TABLE_BASE;
		HIGH_LEVEL_RESULT_TABLE = "P" + prefix + "_" + HIGH_LEVEL_RESULT_TABLE_BASE;
		LOW_LEVEL_RESULT_TABLE = "P" + prefix + "_" + LOW_LEVEL_RESULT_TABLE_BASE;
		HIGH_TO_LOW_LEVEL_MAPPING_TABLE = "P" + prefix + "_" + HIGH_TO_LOW_LEVEL_MAPPING_TABLE_BASE;
		
		VERSION_TABLE_SCHEMA = "CREATE TABLE IF NOT EXISTS "+VERSION_TABLE+" ("+
		  "version varchar(20) NOT NULL,"+
		  "type varchar(50) NOT NULL,"+
		  "base_type varchar(50) NOT NULL,"+
		  "values_md5 varchar(50) NOT NULL,"+
		  "value1 varchar(2000) default NULL,"+
		  "value2 varchar(2000) default NULL,"+
		  "value3 varchar(2000) default NULL,"+
		  "value4 varchar(2000) default NULL,"+
		  "value5 varchar(2000) default NULL,"+
		  "value6 varchar(2000) default NULL,"+
		  "KEY ver_version (version,base_type,type),"+
		  "KEY ver_value1 (value1),"+
		  "KEY ver_value2 (value2),"+
		  "KEY ver_value3 (value3),"+
		  "KEY ver_value4 (value4),"+
		  "KEY ver_value5 (value5),"+
		  "KEY ver_value6 (value6),"+
		  "KEY ver_version_2 (version,type,base_type,values_md5),"+
		  "KEY ver_values_md5 (values_md5))";
		WORKING_TABLE_SCHEMA = "CREATE TABLE IF NOT EXISTS "+WORKING_TABLE+" ("+
		  "actionMD5 varchar(50) default NULL,"+
		  "change_action varchar(50) NOT NULL,"+
		  "reduce int(11) default '0',"+
		  "value1 varchar(2000) default NULL,"+
		  "value2 varchar(6000) default NULL,"+
		  "value3 varchar(6000) default NULL,"+
		  "value4 varchar(2000) default NULL,"+
		  "value5 varchar(2000) default NULL,"+
		  "value6 varchar(2000) default NULL,"+
		  "UNIQUE KEY wor_actionMD5 (actionMD5),"+
		  "KEY wor_change_action (change_action),"+
		  "KEY wor_reduce (reduce),"+
		  "KEY wor_value1 (value1),"+
		  "KEY wor_value2 (value2),"+
		  "KEY wor_value3 (value3),"+
		  "KEY wor_value4 (value4),"+
		  "KEY wor_value5 (value5),"+
		  "KEY wor_value6 (value6))";
		HIGH_LEVEL_RESULT_SCHEMA = "CREATE TABLE IF NOT EXISTS "+HIGH_LEVEL_RESULT_TABLE+" ("+
		  "actionMD5 varchar(50) default NULL,"+
		  "change_action varchar(50) NOT NULL,"+
		  "value1 varchar(2000) default NULL,"+
		  "value2 varchar(6000) default NULL,"+
		  "value3 varchar(6000) default NULL,"+
		  "value4 varchar(2000) default NULL,"+
		  "value5 varchar(2000) default NULL,"+
		  "value6 varchar(2000) default NULL,"+
		  "UNIQUE KEY hlv_actionMD5 (actionMD5),"+
		  "KEY hlv_change_action (change_action),"+
		  "KEY hlv_value1 (value1),"+
		  "KEY hlv_value2 (value2),"+
		  "KEY hlv_value3 (value3),"+
		  "KEY hlv_value4 (value4),"+
		  "KEY hlv_value5 (value5),"+
		  "KEY hlv_value6 (value6))";
		LOW_LEVEL_RESULT_SCHEMA = "CREATE TABLE IF NOT EXISTS "+LOW_LEVEL_RESULT_TABLE+" ("+
		  "actionMD5 varchar(50) default NULL,"+
		  "change_action varchar(50) NOT NULL,"+
		  "value1 varchar(2000) default NULL,"+
		  "value2 varchar(6000) default NULL,"+
		  "value3 varchar(6000) default NULL,"+
		  "value4 varchar(2000) default NULL,"+
		  "value5 varchar(2000) default NULL,"+
		  "value6 varchar(2000) default NULL,"+
		  "UNIQUE KEY llv_actionMD5 (actionMD5),"+
		  "KEY llv_change_action (change_action),"+
		  "KEY llv_value1 (value1),"+
		  "KEY llv_value2 (value2),"+
		  "KEY llv_value3 (value3),"+
		  "KEY llv_value4 (value4),"+
		  "KEY llv_value5 (value5),"+
		  "KEY llv_value6 (value6))";
		HIGH_TO_LOW_LEVEL_MAPPING_SCHEMA = "CREATE TABLE "+HIGH_TO_LOW_LEVEL_MAPPING_TABLE+" ("+
		  "high_level_action_MD5 varchar(50) NOT NULL,"+
		  "low_level_action_MD5 varchar(50) NOT NULL,"+
		  "KEY htl_high_level_action_MD5 (high_level_action_MD5),"+
		  "KEY htl_low_level_action_MD5 (low_level_action_MD5))";
	}
}
