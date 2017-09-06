package de.jcup.basheditor.scriptmodel;

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
		if (context.inState(State.INSIDE_COMMENT)) {
			/* in comment state */
			if (c == '\n') {
				context.addTokenAndResetText();
				context.switchTo(State.CODE);
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
			if (context.inState(State.INSIDE_DOUBLE_STRING)) {
				/* inside other string - ignore */
				context.appendCharToText();
				return;
			}
			if (context.inState(State.INSIDE_DOUBLE_TICKED)) {
				/* inside other string - ignore */
				context.appendCharToText();
				return;
			}
			if (context.getCharBefore() == '\\') {
				/* escaped */
				context.appendCharToText();
				return;
			}
			if (context.inState(State.INSIDE_SINGLE_STRING)) {
				/* close single string */
				context.appendCharToText();
				context.switchTo(State.CODE);
				return;
			}
			context.switchTo(State.INSIDE_SINGLE_STRING);
			return;
		}
		/* handle double string */
		if (c == '\"') {
			if (context.inState(State.INSIDE_SINGLE_STRING)) {
				/* inside other string - ignore */
				context.appendCharToText();
				return;
			}
			if (context.inState(State.INSIDE_DOUBLE_TICKED)) {
				/* inside other string - ignore */
				context.appendCharToText();
				return;
			}
			if (context.getCharBefore() == '\\') {
				/* escaped */
				context.appendCharToText();
				return;
			}
			if (context.inState(State.INSIDE_DOUBLE_STRING)) {
				/* close double string */
				context.appendCharToText();
				context.switchTo(State.CODE);
				return;
			}
			context.switchTo(State.INSIDE_DOUBLE_STRING);
			return;
		}
		/* handle double ticked string */
		if (c == '`') {
			if (context.inState(State.INSIDE_SINGLE_STRING)) {
				/* inside other string - ignore */
				context.appendCharToText();
				return;
			}
			if (context.inState(State.INSIDE_DOUBLE_STRING)) {
				/* inside other string - ignore */
				context.appendCharToText();
				return;
			}
			if (context.getCharBefore() == '\\') {
				/* escaped */
				context.appendCharToText();
				return;
			}
			if (context.inState(State.INSIDE_DOUBLE_TICKED)) {
				/* close double string */
				context.appendCharToText();
				context.switchTo(State.CODE);
				return;
			}
			context.switchTo(State.INSIDE_DOUBLE_TICKED);
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
			context.switchTo(State.CODE);
			return;
		}

		if (c == ';') {
			// special bash semicolon operator, separates only commands so
			// handle like a whitespace
			context.addTokenAndResetText();
			context.switchTo(State.CODE);
			return;
		}

		if (c == '{' || c == '}') {
			// block start/ endf found, add as own token
			context.addTokenAndResetText();
			context.appendCharToText();
			context.addTokenAndResetText();
			context.switchTo(State.CODE);
			return;
		}

		if (c == '#') {
			context.addTokenAndResetText();
			context.switchTo(State.INSIDE_COMMENT);
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

}
