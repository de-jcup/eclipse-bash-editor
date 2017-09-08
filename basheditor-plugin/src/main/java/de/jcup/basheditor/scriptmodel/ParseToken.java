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

public class ParseToken {

	ParseToken() {

	}

	ParseToken(String text) {
		this(text, 0, 0);
	}

	ParseToken(String text, int start, int end) {
		if (text == null) {
			text = "";
		}
		this.text = text;
		this.start = start;
		this.end = end;
	}

	String text;
	int start;
	int end;

	@Override
	public String toString() {
		return text;
	}

	public boolean isComment() {
		return text.startsWith("#");
	}

	public boolean isSingleString() {
		return text.startsWith("'");
	}

	public boolean isDoubleString() {
		return text.startsWith("\"");
	}

	public boolean isDoubleTickedString() {
		return text.startsWith("`");
	}

	public boolean isString() {
		boolean isString = isSingleString() || isDoubleString() || isDoubleTickedString();
		return isString;
	}

	public boolean isFunctionKeyword() {
		return "function".equals(text);
	}

	public boolean isFunctionName() {
		return endsWithFunctionBrackets() && !isComment() && text.length()>2 && !isString();
	}
	
	public boolean endsWithFunctionBrackets(){
		return text.endsWith("()");
	}
	
	public boolean hasLength(int length){
		return text.length()==length;
	}
	
	public String getTextAsFunctionName() {
		//String name = token.text;
		 if (text.endsWith("()")){
			 return text.substring(0,text.length()-2);
		 }
		return text;
	}

	public boolean isOpenBlock() {
		 return text.length()==1 && text.endsWith("{");
	}
	
	public boolean isCloseBlock() {
		 return text.length()==1 && text.endsWith("}");
	}

	public boolean isDo() {
		return text.equals("do");
	}
	
	public boolean isDone() {
		return text.equals("done");
	}
	
	public boolean isIf() {
		return text.equals("if");
	}
	
	public boolean isFi() {
		return text.equals("fi");
	}
}
