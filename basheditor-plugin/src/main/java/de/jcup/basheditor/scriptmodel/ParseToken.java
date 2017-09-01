package de.jcup.basheditor.scriptmodel;

public class ParseToken {

	String text;
	int start;
	int end;
	
	@Override
	public String toString() {
		return "PT:"+text+"["+start+":"+end+"]";
	}
}
