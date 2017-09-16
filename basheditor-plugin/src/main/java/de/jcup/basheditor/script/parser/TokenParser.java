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
		/* ++++++++++++++++++++++++++++++++ */
		/* ++++++ Not in comment state +++ */
		/* ++++++++++++++++++++++++++++++++ */

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
		/* +++++++++++++++++++++++++++++++ */
		/* ++++++ Not in string state +++ */
		/* +++++++++++++++++++++++++++++++ */
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
			if (context.inState(INSIDE_COMMENT)){
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
			if (! context.inState(VARIABLE)){
				context.addTokenAndResetText();
			}
			return;
		}

		if (c== '$'){
			context.appendCharToText();
			context.switchTo(VARIABLE);
			return;
		}

		
		if (c == '{' || c == '}') {
			if (context.inState(VARIABLE)){
				context.appendCharToText();
				if (c=='}'){
					context.addTokenAndResetText();
					context.switchTo(CODE);
				}
				return;
				
			}
			// block start/ end found, add as own token
			context.addTokenAndResetText();
			context.appendCharToText();
			context.addTokenAndResetText();
			context.switchTo(CODE);
			return;
		}

		if (c == '#' && ! context.inState(VARIABLE)) {
			context.addTokenAndResetText();
			context.switchTo(INSIDE_COMMENT);
			context.appendCharToText();
		} else {
			/* not inside a comment build token nor in string, so whitespaces are not necessary!*/
			if (Character.isWhitespace(c)) {
				context.addTokenAndResetText();
				return;
			}
			context.appendCharToText();
		}

	}

	private void handleString(ParserState stringState, ParseContext context, ParserState ...otherStringStates) {
		for (ParserState otherStringState: otherStringStates){
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
		context.switchToStringState(stringState);
		context.appendCharToText();
		return;
		
	}

}
