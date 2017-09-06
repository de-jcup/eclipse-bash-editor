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
}
