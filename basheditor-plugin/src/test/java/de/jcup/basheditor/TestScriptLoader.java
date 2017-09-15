package de.jcup.basheditor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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
