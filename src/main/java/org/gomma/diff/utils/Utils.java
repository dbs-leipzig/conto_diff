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

package org.gomma.diff.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import org.gomma.diff.Globals;

public class Utils {
	
	private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
        	int halfbyte = (data[i] >>> 4) & 0x0F;
        	int two_halfs = 0;
        	do {
	        	if ((0 <= halfbyte) && (halfbyte <= 9))
	                buf.append((char) ('0' + halfbyte));
	            else
	            	buf.append((char) ('a' + (halfbyte - 10)));
	        	halfbyte = data[i] & 0x0F;
        	} while(two_halfs++ < 1);
        }
        return buf.toString();
    }
 
	public static String MD5(String text)  {
			try {
				MessageDigest md;
				md = MessageDigest.getInstance("MD5");
				byte[] md5hash = new byte[32];
				md.update(text.getBytes("iso-8859-1"), 0, text.length());
				md5hash = md.digest();
				return convertToHex(md5hash);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				return null;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
	}
	
	public static String MD5(List<String> values) {
		StringBuffer result = new StringBuffer();
		for (int i=0;i<values.size();i++) {
			result.append(values.get(i));
		}
		return MD5(result.toString());
	}
	
	public static String MD5(String[] values) {
		StringBuffer result = new StringBuffer();
		for (int i=0;i<values.length;i++) {
			result.append(values[i]);
		}
		return MD5(result.toString());
	}

	public static String eleminateDuplicates(String currentValues) {
		StringBuffer result;
		String[] values = currentValues.split(Globals.DELIMITER);
		if (values==null||values.length==1) {
			return currentValues;
		} else {
			Arrays.sort(values);
			result = new StringBuffer(values[0]);
			for (int i=0;i<values.length-1;i++) {
				if (!values[i].equals(values[i+1])) {
					result.append(Globals.DELIMITER+values[i+1]);
				}
			}
		}
		return result.toString();
	}
}
