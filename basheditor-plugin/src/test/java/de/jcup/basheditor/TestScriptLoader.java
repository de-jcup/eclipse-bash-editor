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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestScriptLoader {
	private static File testScriptRootFolder = new File("./basheditor-other/testscripts");
	static{
		if (!testScriptRootFolder.exists()){
			// workaround for difference between eclipse test and gradle execution (being in root folder...)
			testScriptRootFolder = new File("./../basheditor-other/testscripts");
		}
	}
	
	public static List<File> fetchAllTestScriptFiles() {
		assertTestscriptFolderExists();
		List<File> list = new ArrayList<>();
		File folder = testScriptRootFolder;
		fetchTestScriptNames(list, folder);
		return list;
	}

	private static void fetchTestScriptNames(List<File> list, File folder) {
		for (File file: folder.listFiles()){
			if (file.isFile()) {
				list.add(file);
			}
			if (file.isDirectory()) {
				fetchTestScriptNames(list,file);
			}
		}
	}
	
	public static String loadScriptFromTestScripts(String testScriptName) throws IOException{
		assertTestscriptFolderExists();
		
		File file = getTestScriptFile(testScriptName);
		return loadScript(file);
	}


	public static File getTestScriptFile(String testScriptName) {
		File file = new File(testScriptRootFolder,testScriptName);
		return file;
	}

	private static void assertTestscriptFolderExists() {
		if (!testScriptRootFolder.exists()){
			throw new IllegalArgumentException("Test setup corrupt! Root folder of test scripts not found:"+testScriptRootFolder);
		}
	}

	public static String loadScript(File scriptFile) throws IOException {
		return ScriptUtil.loadScript(scriptFile);
	}
}
