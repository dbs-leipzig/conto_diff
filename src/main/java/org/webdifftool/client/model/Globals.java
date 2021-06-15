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

import java.util.HashMap;
import java.util.Map;

public class Globals
{
	//public static final String baseURL = "http://127.0.0.1:8888/";
	public static final String baseURL = "http://dbserv2.informatik.uni-leipzig.de:8080/webdifftool/";
	
	//Lokal
	public static final String CHANGE_ACTION_LOCATION = "files/ChangeActions.xml";
	public static final String RULES_LOCATION = "files/Rule_OBO.xml";
	
	//Server
	//public static final String CHANGE_ACTION_LOCATION = "/var/lib/tomcat6/webapps/webdifftool/files/ChangeActions.xml";
	//public static final String RULES_LOCATION = "/var/lib/tomcat6/webapps/webdifftool/files/Rule_OBO.xml";
	
	public static final Map<String, String> ChangeColorMap = new HashMap<String, String>()
	{
		private static final long serialVersionUID = 8067968142054878561L;

		{
            put("addA",          "C1FFC1");
            put("addC",          "00FF00");
            put("addR",          "00D200");
            put("delA",          "FFA3A3");
            put("delC",          "FF0000");
            put("delR",          "D60000");
            put("mapC",          "00B0F0");
            put("mapR",			 "80D8F8");
            put("addInner",      "11FF11");
            put("addLeaf",       "7DFF7D");
            put("addSubGraph",   "007E39");
            put("chgAttValue",   "C5D9F1");
            put("delInner",      "DE4A00");
            put("delLeaf",       "FF9966");
            put("delSubGraph",   "8A2100");
            put("merge",         "CC66FF");
            put("move",          "31869B");
            put("revokeObsolete","99CC00");
            put("split",         "33CCCC");
            put("substitute",    "FFFF00");
            put("toObsolete",    "FF9933");
        }
	};
}
