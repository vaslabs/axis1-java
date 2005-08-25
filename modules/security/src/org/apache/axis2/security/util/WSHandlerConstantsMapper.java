/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.axis2.security.util;

import java.util.Hashtable;

import org.apache.axis2.security.handler.WSSHandlerConstants;
import org.apache.ws.security.handler.WSHandlerConstants;

/**
 * 
 * 
 * @author Ruchith Fernando (ruchith.fernando@gmail.com)
 */
public class WSHandlerConstantsMapper {

	private static Hashtable inHandlerConstants = new Hashtable();
	
	private static Hashtable outHandlerConstants = new Hashtable();

	
	static {
		//Mapping the in handler constants
		inHandlerConstants.put(WSHandlerConstants.ACTION,WSSHandlerConstants.In.ACTION);
		
		
		
		//Mapping the out handler constants
		outHandlerConstants.put(WSHandlerConstants.ACTION, WSSHandlerConstants.Out.ACTION);
		
		
	}
	
	/**
	 * If the mapping is there then the mapped value will be returned
	 * Otherwise the original value will be returned since no mapping was required
	 * @param axiskey
	 * @return
	 */
	public static String getMapping(String axiskey, boolean inHandler) {
		String newKey = null;
		if(inHandler) {
			newKey = (String)inHandlerConstants.get(axiskey);
		} else {
			newKey = (String)outHandlerConstants.get(axiskey);
		}
		return (newKey == null)?axiskey:newKey;
	}
	
}
