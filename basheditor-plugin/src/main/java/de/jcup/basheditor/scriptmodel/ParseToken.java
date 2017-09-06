package de.jcup.basheditor.scriptmodel;

public class ParseToken {

	ParseToken(){
		
	}
	ParseToken(String text){
		this(text,0,0);
	}
	ParseToken(String text, int start, int end){
		this.text=text;
		this.start=start;
		this.end=end;
	}
	
	String text;
	int start;
	int end;
	
	@Override
	public String toString() {
		return "PT:"+text+"["+start+":"+end+"]";
	}
}
