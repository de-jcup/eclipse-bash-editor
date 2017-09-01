package de.jcup.basheditor.scriptmodel;

import java.util.ArrayList;
import java.util.List;

public class TokenParser {

	private boolean filterCodeTokens;
	private boolean filterCommentTokens = true;

	public void setFilterCodeTokens(boolean filterCodeTokens) {
		this.filterCodeTokens = filterCodeTokens;
	}

	public boolean isFilterCodeTokens() {
		return filterCodeTokens;
	}
	public boolean isFilterCommentTokens() {
		return filterCommentTokens;
	}
	
	public void setFilterCommentTokens(boolean filterCommentTokens) {
		this.filterCommentTokens = filterCommentTokens;
	}

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
		if (context.inState(State.INSIDE_COMMENT)){
			/* in comment state */
			if (c == '\n') {
				if (!filterCommentTokens){
					context.addTokenAndResetText();
				}
				context.switchTo(State.START_TEXT);
				return;
			}else{
				if (!filterCommentTokens){
					context.appendCharToText();
				}
			}
			return;
		}
		
		/* not in comment state */
		if (c == '\r') {
			return;
		}
		if (c == '\n') {
			context.switchTo(State.START_TEXT);
			return;
		}
		if (c == '#') {
			if (!filterCodeTokens){
				context.addTokenAndResetText();
			}
			context.switchTo(State.INSIDE_COMMENT);
			if (!filterCommentTokens){
				context.appendCharToText();
			}
		}else{
			/* not inside a comment build token */
			if (!filterCodeTokens){
				if (Character.isWhitespace(c)){
					context.addTokenAndResetText();
					return;
				}
				context.appendCharToText();
			}
		}
	
	}

}
