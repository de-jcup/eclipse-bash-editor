package de.jcup.basheditor.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleBashParser {
	private static String REGEXP_FUNCTION_SCAN="^\\s*function\\s+([a-zA-Z0-9]+)";
	private static Pattern PATTERN_FUNCTION_SCAN= Pattern.compile(REGEXP_FUNCTION_SCAN);
	/**
	 * Parses given script
	 * @param bashScript
	 * @return a simple model with some information about bash script
	 */
	public BashScriptModel parse(String bashScript) {
		BashScriptModel model = new BashScriptModel();
		parseFunctions(bashScript, model);
		return model;
	}

	/**
	 * Scans for functions like:
	 * <pre>
		 * #!/bin/bash 
		 * function quit { 
		 * exit 
		 * } 
		 * function hello { 
		 * 		echo Hello! 
		 * } 
		 * hello
		 * quit echo foo
	 * </pre>
	 * 
	 * @param bashScript
	 */
	void parseFunctions(String bashScript, BashScriptModel model) {
		Matcher matcher = PATTERN_FUNCTION_SCAN.matcher(bashScript);
		while (matcher.find()){
			BashFunction bashFunction = new BashFunction();
			bashFunction.name=matcher.group(1);
			bashFunction.position=matcher.start();
			
			model.functions.add(bashFunction);
			
		}

	}

}
