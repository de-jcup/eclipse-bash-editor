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
 package de.jcup.basheditor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TestScriptLoader {
	private static File testScriptRootFolder = new File("./basheditor-other");
	static{
		if (!testScriptRootFolder.exists()){
			// workaround for difference between eclipse test and gradle execution (being in root folder...)
			testScriptRootFolder = new File("./../basheditor-other/");
		}
	}
	public static String loadScriptFromTestScripts(String testScriptName) throws IOException{
		if (!testScriptRootFolder.exists()){
			throw new IllegalArgumentException("Test setup corrupt! Root folder of test scripts not found:"+testScriptRootFolder);
		}
		
		File file = new File(testScriptRootFolder,"testscripts/"+testScriptName);
		if (!file.exists()){
			throw new IllegalArgumentException("Test case corrupt! Test script file does not exist:"+file);
		}
		StringBuilder sb = new StringBuilder();
		try(BufferedReader br = new BufferedReader(new FileReader(file))){
			String line = null;
			while ((line=br.readLine())!=null){
				sb.append(line);
				sb.append("\n");
			}
		}
		return sb.toString();
	}
}
