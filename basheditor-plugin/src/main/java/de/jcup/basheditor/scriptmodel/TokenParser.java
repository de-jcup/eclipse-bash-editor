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
				context.switchTo(State.CODE);
				return;
			}else{
				if (!filterCommentTokens){
					context.appendCharToText();
				}
			}
			return;
		}
		/* ++++++++++++++++++++++++++++++++ */
		/* ++++++ Not in comment state  +++ */
		/* ++++++++++++++++++++++++++++++++ */
		
		/* handle single string */
		if (c == '\''){
			if (context.inState(State.INSIDE_DOUBLE_STRING)){
				/* inside other string - ignore*/
				context.appendCharToText();
				return;
			}
			if (context.inState(State.INSIDE_DOUBLE_TICKED)){
				/* inside other string - ignore*/
				context.appendCharToText();
				return;
			}
			if (context.getCharBefore()=='\\'){
				/* escaped*/
				context.appendCharToText();
				return;
			}
			if (context.inState(State.INSIDE_SINGLE_STRING)){
				/* close single string*/
				context.appendCharToText();
				context.switchTo(State.CODE);
				return;
			}
			context.switchTo(State.INSIDE_SINGLE_STRING);
			return;
		}
		/* handle double string */
		if (c == '\"'){
			if (context.inState(State.INSIDE_SINGLE_STRING)){
				/* inside other string - ignore*/
				context.appendCharToText();
				return;
			}
			if (context.inState(State.INSIDE_DOUBLE_TICKED)){
				/* inside other string - ignore*/
				context.appendCharToText();
				return;
			}
			if (context.getCharBefore()=='\\'){
				/* escaped*/
				context.appendCharToText();
				return;
			}
			if (context.inState(State.INSIDE_DOUBLE_STRING)){
				/* close double string*/
				context.appendCharToText();
				context.switchTo(State.CODE);
				return;
			}
			context.switchTo(State.INSIDE_DOUBLE_STRING);
			return;
		}
		/* handle double ticked string */
		if (c == '`'){
			if (context.inState(State.INSIDE_SINGLE_STRING)){
				/* inside other string - ignore*/
				context.appendCharToText();
				return;
			}
			if (context.inState(State.INSIDE_DOUBLE_STRING)){
				/* inside other string - ignore*/
				context.appendCharToText();
				return;
			}
			if (context.getCharBefore()=='\\'){
				/* escaped*/
				context.appendCharToText();
				return;
			}
			if (context.inState(State.INSIDE_DOUBLE_TICKED)){
				/* close double string*/
				context.appendCharToText();
				context.switchTo(State.CODE);
				return;
			}
			context.switchTo(State.INSIDE_DOUBLE_TICKED);
			return;
		}
		if (context.insideString()){
			context.appendCharToText();
			return;
		}
		/* +++++++++++++++++++++++++++++++ */
		/* ++++++ Not in string state  +++ */
		/* +++++++++++++++++++++++++++++++ */
		if (c == '\r') {
			/* ignore - we only use \n inside the data parsed so we will handle easy \r\n and \n*/
			return;
		}
		if (c == '\n') {
			if (context.insideString()){
				context.appendCharToText();
				return;
			}
			context.addTokenAndResetText();
			context.switchTo(State.CODE);
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
