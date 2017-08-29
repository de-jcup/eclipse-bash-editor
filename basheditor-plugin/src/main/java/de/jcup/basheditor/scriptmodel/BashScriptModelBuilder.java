package de.jcup.basheditor.scriptmodel;

/**
 * A bash script model builder
 * @author Albert Tregnaghi
 *
 */
public class BashScriptModelBuilder {
	/**
	 * Parses given script and creates a bash script model
	 * 
	 * @param bashScript
	 * @return a simple model with some information about bash script
	 */
	public BashScriptModel build(String bashScript) {
		BashScriptModel model = new BashScriptModel();
		buildFunctions(bashScript, model);
		return model;
	}

	/**
	 * Scans for functions like:
	 * 
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
	void buildFunctions(String bashScript, BashScriptModel model) {
		scanForIdentifierFollowedByWord(bashScript, model, "function ");
	}

	
	private void scanForIdentifierFollowedByWord(String bashScript, BashScriptModel model, String scanString) {
		int pos = 0;
		while (true) {
			if (pos >= bashScript.length()) {
				break;
			}
			pos = bashScript.indexOf(scanString, pos);
			if (pos < 0) {
				break;
			}
			if (pos > 0) {
				/* check if before is only a whitespace */
				char before = bashScript.charAt(pos - 1);
				if (!Character.isWhitespace(before)) {
					pos++;
					/* not a function but e.g. XFunction */
					continue;
				}
			}
			int namePos = pos + scanString.length();
			while (true) {
				char charAt = bashScript.charAt(namePos);
				if (namePos >= bashScript.length()) {
					break;
				}
				if (!Character.isWhitespace(charAt)) {
					break;
				}
				namePos++;
			}
			/* +++++++++++++ */
			/* + build name+ */
			/* +++++++++++++ */
			StringBuilder sb = new StringBuilder();

			while (true) {
				if (namePos >= bashScript.length()) {
					break;
				}
				char charAt = bashScript.charAt(namePos);
				if (Character.isLetterOrDigit(charAt)|| charAt=='_') {
					sb.append(charAt);
					namePos++;
				} else {
					break;
				}
			}

			/* next create the function and add... */
			BashFunction bashFunction = new BashFunction();
			bashFunction.name = sb.toString();
			bashFunction.position = pos;
			bashFunction.length = namePos - pos;

			model.functions.add(bashFunction);

			pos = namePos + 1;
		}
	}

}
