/*
 * Copyright 2017 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
 package de.jcup.basheditor.document;

public enum BashDocumentIdentifiers implements BashDocumentIdentifier {
	
	BASH_KEYWORD,
	
	BASH_SYSTEM_KEYWORD ,
	
	COMMENT,
	
	SINGLE_STRING,
	
	DOUBLE_STRING,
	
	BACKTICK_STRING,
	
	GRADLE_KEYWORD,
	
	INCLUDE_KEYWORD,
	
	BASH_COMMAND,
	
	KNOWN_VARIABLES,
	
	VARIABLES,
	
	PARAMETER,
	
	HERE_DOCUMENT,
	;


	@Override
	public String getId() {
		return name();
	}
	public static String[] allIdsToStringArray(){
		return allIdsToStringArray(null);
	}
	public static String[] allIdsToStringArray(String additionalDefaultId){
		BashDocumentIdentifiers[] values = values();
		int size = values.length;
		if (additionalDefaultId!=null){
			size+=1;
		}
		String[] data = new String[size];
		int pos=0;
		if (additionalDefaultId!=null){
			data[pos++]=additionalDefaultId;
		}
		for (BashDocumentIdentifiers d: values){
			data[pos++]=d.getId();
		}
		return data;
	}

}
