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
package de.jcup.basheditor.script.parser;

import static de.jcup.basheditor.script.parser.ParserState.*;

import java.util.ArrayList;
import java.util.List;

import de.jcup.basheditor.script.parser.ParseContext.VariableContext;

public class TokenParser {

	public List<ParseToken> parse(String bashScript) {
		if (bashScript == null) {
			return new ArrayList<>();
		}
		ParseContext parseContext = new ParseContext();
		parseContext.chars = bashScript.toCharArray();

		for (int i = 0; i < parseContext.chars.length; i++) {
			parseContext.pos = i;
			parse(parseContext);
		}
		// add last token if existing
		parseContext.addTokenAndResetText();
		return parseContext.tokens;
	}

	private void parse(ParseContext context) {
		char c = context.getCharAtPos();
		
		/* +++++++++++++++++++++++++++ */
		/* ++++++ Handle variables +++ */
		/* +++++++++++++++++++++++++++ */
		if (context.inState(VARIABLE)) {
			VariableContext variableContext = context.getVariableContext();
			if (c == '$') {
				context.appendCharToText();
				/* as described at http://tldp.org/LDP/abs/html/special-chars.html "$$" is a special variable holding the process id
				 so in this case it terminates the variable!*/
				if (context.getCharBefore()=='$'){
					context.addTokenAndResetText();
					context.switchTo(CODE);
				}
				return;
			}
			if (c == '#') {
				context.appendCharToText();
				return;
			}
			if (c == '[') {
				variableContext.variableArrayOpened();
				context.appendCharToText();
				return;
			}
			if (c == ']') {
				variableContext.variableArrayClosed();
				context.appendCharToText();
				return;
			}
			if (c == '{' || c == '}') {
				context.appendCharToText();
				if (c == '{' ) {
					variableContext.incrementVariableOpenCurlyBraces();
				}
				if (c == '}') {
					variableContext.incrementVariableCloseCurlyBraces();
				}
				if (c == '}' && variableContext.areVariableCurlyBracesBalanced()) {
					context.addTokenAndResetText();
					context.switchTo(CODE);
				}
				return;
			}

			if (Character.isWhitespace(c)) {
				context.addTokenAndResetText();
				return;
			}
			if (variableContext.isInsideVariableArray()) {
				if (isStringChar(c)){
					context.appendCharToText();
					return;
				}
				context.appendCharToText();
				return;
			}else{
				/* normal variable or array closed*/
				if (Character.isWhitespace(c)) {
					context.addTokenAndResetText();
					context.switchTo(ParserState.CODE);
					return;
				}
				if (isStringChar(c)){
					/* this is a string char - means end of variable def*/
					context.addTokenAndResetText();
					context.switchTo(ParserState.CODE);
					/* no return, handle normal!*/
					
				}else{
					context.appendCharToText();
					return;
				}
				
			}
		}
		
		if (context.inState(INSIDE_COMMENT)) {
			/* in comment state */
			if (c == '\n') {
				context.addTokenAndResetText();
				context.switchTo(CODE);
				return;
			} else {
				context.appendCharToText();
			}
			return;
		}
		/* +++++++++++++++++++++++++++++++++++++++++++ */
		/* ++++++ Not in variable or comment state +++ */
		/* +++++++++++++++++++++++++++++++++++++++++++ */

		/* handle single string */
		if (c == '\'') {
			handleString(INSIDE_SINGLE_STRING, context, INSIDE_DOUBLE_TICKED, INSIDE_DOUBLE_STRING);
			return;
		}
		/* handle double string */
		if (c == '\"') {
			handleString(INSIDE_DOUBLE_STRING, context, INSIDE_DOUBLE_TICKED, INSIDE_SINGLE_STRING);
			return;
		}
		/* handle double ticked string */
		if (c == '`') {
			handleString(INSIDE_DOUBLE_TICKED, context, INSIDE_SINGLE_STRING, INSIDE_DOUBLE_STRING);
			return;
		}
		if (context.insideString()) {
			context.appendCharToText();
			return;
		}
		/* +++++++++++++++++++++++++++++++++++++++++++++++++++ */
		/* ++++++ Not in comment, string or variable state +++ */
		/* +++++++++++++++++++++++++++++++++++++++++++++++++++ */
		if (c == '\r') {
			/*
			 * ignore - we only use \n inside the data parsed so we will handle
			 * easy \r\n and \n
			 */
			context.moveCurrentPosWhenEmptyText();
			return;
		}
		if (c == '\n') {
			context.addTokenAndResetText();
			if (context.inState(INSIDE_COMMENT)) {
				context.switchTo(CODE);
			}
			return;
		}

		if (c == ';') {
			// special bash semicolon operator, separates only commands so
			// handle like a whitespace
			context.addTokenAndResetText();
			context.switchTo(CODE);
			return;
		}
		if (c == '=') {
			// special assign operator
			context.appendCharToText();
			if (!context.inState(VARIABLE)) {
				context.addTokenAndResetText();
			}
			return;
		}
		if (c == '$') {
			context.addTokenAndResetText();
			context.appendCharToText();
			context.switchTo(VARIABLE);
			return;
		}

		if (c == '{' || c == '}') {
			// block start/ end found, add as own token
			context.addTokenAndResetText();
			context.appendCharToText();
			context.addTokenAndResetText();
			context.switchTo(CODE);
			return;
		}

		if (c == '#') {
			context.addTokenAndResetText();
			context.switchTo(INSIDE_COMMENT);
			context.appendCharToText();
		} else {
			/*
			 * not inside a comment build token nor in string, so whitespaces
			 * are not necessary!
			 */
			if (Character.isWhitespace(c)) {
				context.addTokenAndResetText();
				return;
			}
			context.appendCharToText();
		}

	}

	private boolean isStringChar(char c) {
		boolean isStringChar = c=='\"';
		isStringChar = isStringChar ||c=='\'';
		isStringChar = isStringChar ||c=='`';
		return isStringChar;
	}

	private void handleString(ParserState stringState, ParseContext context, ParserState... otherStringStates) {
		for (ParserState otherStringState : otherStringStates) {
			if (context.inState(otherStringState)) {
				/* inside other string - ignore */
				context.appendCharToText();
				return;
			}

		}
		if (context.getCharBefore() == '\\') {
			/* escaped */
			context.appendCharToText();
			return;
		}
		if (context.inState(stringState)) {
			/* close single string */
			context.appendCharToText();
			context.restoreStateBeforeString();
			return;
		}
		if (context.inState(ParserState.VARIABLE)) {
			context.appendCharToText();
			return;
		}
		context.switchToStringState(stringState);
		context.appendCharToText();
		return;

	}

}
