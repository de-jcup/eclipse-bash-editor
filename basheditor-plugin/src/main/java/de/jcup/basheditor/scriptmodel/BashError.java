package de.jcup.basheditor.scriptmodel;

public class BashError {
	
	public BashError(int line, String message){
		this.position=line;
		this.message=message;
	}

	private int position;
	private String message;

	public int getPosition() {
		return position;
	}

	public String getMessage() {
		return message;
	}
}
