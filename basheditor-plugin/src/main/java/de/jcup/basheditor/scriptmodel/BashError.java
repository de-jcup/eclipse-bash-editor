package de.jcup.basheditor.scriptmodel;

public class BashError {
	
	private int end;
	private int start;
	private String message;

	public BashError(int start, int end, String message){
		this.start=start;
		this.end=end;
		this.message=message;
	}


	public int getStart() {
		return start;
	}

	public String getMessage() {
		return message;
	}

	public int getEnd() {
		return end;
	}
}
