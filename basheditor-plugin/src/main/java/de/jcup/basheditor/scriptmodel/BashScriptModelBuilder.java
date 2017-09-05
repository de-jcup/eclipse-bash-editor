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
package de.jcup.basheditor.scriptmodel;

import java.util.List;

/**
 * A bash script model builder
 * 
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
		/*
		 * A little bit ugly but this works: we use the simple scan approach for
		 * functions and combine it with a token parser result for only comment
		 * tokens. So it is possible to check if the function text is inside a a
		 * comment or its code part. Maybe in future the token parser could be
		 * used inside this builder to iterate over code tokens as well and to
		 * handle the logic by the token only - without own string scannnig etc.
		 */
		TokenParser parser = new TokenParser();
		parser.setFilterCodeTokens(true);
		parser.setFilterCommentTokens(false);

		List<ParseToken> commentTokens = parser.parse(bashScript);

		String scanString = "function ";
		scanForFunctions(bashScript, model, commentTokens, scanString, 0);
	}

	private void scanForFunctions(String bashScript, BashScriptModel model, List<ParseToken> commentTokens,
			String scanString, int pos) {
		while (pos < bashScript.length()) {
			
			pos = bashScript.indexOf(scanString, pos);
			if (pos < 0) {
				/* no longer found*/
				return;
			}
			if (pos > 0) {
				/* check if before is only a whitespace */
				char before = bashScript.charAt(pos - 1);
				if (!Character.isWhitespace(before) && before!=';') {
					pos++;
					/* not a function but e.g. XFunction */
					continue;
				}
			}
			/* check if its inside a comment */
			boolean isInsideComment = false;
			for (ParseToken comment : commentTokens) {
				if (comment.start < pos) {
					if (pos < comment.end) {
						/* is inside comment */
						isInsideComment = true;
						pos = comment.end;
						break;
					}
				}
			}
			if (isInsideComment) {
				continue;
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

			while (namePos < bashScript.length()) {
				char charAt = bashScript.charAt(namePos);
				if (Character.isLetterOrDigit(charAt) || charAt == '_' || charAt == '-') {
					sb.append(charAt);
					namePos++;
				} else {
					break;
				}
			}
			int lengthToName = namePos - pos;
			/* ++++++++++++ */
			/* + validate + */
			/* ++++++++++++ */
			int amountOfCurlyOpen = 0;
			int amountOfCurlyCose = 0;
			boolean curlyScanStarted = false;
			/* after name there must curly braces */
			while (namePos < bashScript.length() && (!curlyScanStarted || (amountOfCurlyOpen != amountOfCurlyCose))) {
				char charAt = bashScript.charAt(namePos++);
				if (Character.isWhitespace(charAt)) {
					continue;
				}
				if (charAt == '(') {
					continue;
				}
				if (charAt == ')') {
					continue;
				}
				if (charAt == '{') {
					curlyScanStarted = true;
					amountOfCurlyOpen++;
					continue;
				}
				if (charAt == '}') {
					amountOfCurlyCose++;
					continue;
				}
			}
			boolean failed = checkFailed(model, pos, namePos, amountOfCurlyOpen, amountOfCurlyCose, curlyScanStarted, sb);
			if (failed){
				return;
			}
			/* next create the function and add... */
			addFunction(model, pos, sb, lengthToName);

			pos = namePos + 1;
		}
	}

	private boolean checkFailed(BashScriptModel model, int pos, int end, int amountOfCurlyOpen, int amountOfCurlyCose,
			boolean curlyScanStarted, StringBuilder sb) {
		boolean failed=false;
		if (!curlyScanStarted) {
			/* means no real function end - illegal */
			model.errors.add(new BashError(pos, end, "Function '"+sb.toString()+"' has no curly braces defined:"));
			failed=true;
		}
		if (amountOfCurlyOpen != amountOfCurlyCose) {
			/* means no real function end - illegal */
			model.errors.add(
					new BashError(pos, end, "Function '"+sb.toString()+"'has no ending curly brace!"));
			failed=true;
		}
		return failed;
	}

	private void addFunction(BashScriptModel model, int pos, StringBuilder sb, int lengthToName) {
		BashFunction bashFunction = new BashFunction();
		bashFunction.name = sb.toString();
		bashFunction.position = pos;
		bashFunction.lengthToNameEnd = lengthToName;

		model.functions.add(bashFunction);
	}

}
