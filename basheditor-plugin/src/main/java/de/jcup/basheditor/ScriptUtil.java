package de.jcup.basheditor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ScriptUtil {

	public static String loadScript(File file) throws IOException {
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
	
	public static void saveScript(File file, String source) throws IOException {
		if (file.exists()){
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(file,false))){
			bw.write(source);
		}
	}
}
