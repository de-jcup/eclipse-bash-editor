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

import java.util.ArrayList;
import java.util.List;


class ParseContext{
	
	List<ParseToken> tokens = new ArrayList<ParseToken>();
	StringBuilder sb;
	char[] chars;
	int pos;
	private State state = State.INIT;
	private ParseToken currentToken;
	private State stateBeforeString;
	
	public ParseContext(){
		currentToken=createToken();
	}

	public void appendCharToText(){
		getSb().append(getCharAtPos());
	}
	
	public StringBuilder getSb() {
		if (sb==null){
			sb=new StringBuilder();
		}
		return sb;
	}
	public String getText(){
		return getSb().toString();
	}
	
	private ParseToken createToken(){
		ParseToken token = new ParseToken();
		token.start=pos;
		return token;
	}

	public void addTokenAndResetText(){
		if (moveCurrentPosWhenEmptyText()){
			return;
		}
		
		currentToken.text=sb.toString();
		currentToken.end=pos;
		tokens.add(currentToken);
		
		/* new token on next position */
		currentToken=createToken();
		currentToken.start=pos+1;
		
		resetText();
	}

	public boolean moveCurrentPosWhenEmptyText() {
		if (getSb().length()==0){
			currentToken.start++;
			return true;
		}
		return false;
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

	public void switchToStringState(State newStringState) {
		this.stateBeforeString=getState();
		switchTo(newStringState);
	}
	
	public void restoreStateBeforeString() {
		switchTo(stateBeforeString);
	}
}