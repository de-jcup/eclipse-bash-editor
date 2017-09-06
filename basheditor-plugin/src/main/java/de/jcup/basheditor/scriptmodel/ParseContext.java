package de.jcup.basheditor.scriptmodel;

import java.util.ArrayList;
import java.util.List;


class ParseContext{
	
	List<ParseToken> tokens = new ArrayList<ParseToken>();
	StringBuilder sb;
	char[] chars;
	int pos;
	int posNextToken;
	private State state = State.INIT;

	public void appendCharToText(){
		getSb().append(getCharAtPos());
	}
	
	public StringBuilder getSb() {
		if (sb==null){
			posNextToken=pos;
			sb=new StringBuilder();
		}
		return sb;
	}
	public String getText(){
		return getSb().toString();
	}
	
	public void addTokenAndResetText(){
		if (getSb().length()==0){
			// no token, nothing to reset 
			return;
		}

		ParseToken token = new ParseToken();
		token.text=sb.toString();
		token.start=posNextToken;
		token.end=pos;
		tokens.add(token);
		
		resetText();
		
		posNextToken=pos;
	}
	
	public void resetText(){
		sb=null;
	}
	
	public State getState() {
		if (state==null){
			state=State.UNKNOWN;
		}
		return state;
	}
	
	public void switchTo(State state) {
		this.state = state;
	}
	
	public char getCharAtPos(){
		return chars[pos];
	}
	public boolean inState(State state) {
		return getState().equals(state);
	}

	public char getCharBefore() {
		int posBefore = pos-1;
		if (posBefore>=0){
			if (chars.length>0){
				return chars[posBefore];
			}
		}
		return 0;
	}

	public boolean insideString() {
		boolean inString = false;
		inString= inString || inState(State.INSIDE_DOUBLE_STRING);
		inString= inString || inState(State.INSIDE_DOUBLE_TICKED);
		inString= inString || inState(State.INSIDE_SINGLE_STRING);
		return inString;
	}
}