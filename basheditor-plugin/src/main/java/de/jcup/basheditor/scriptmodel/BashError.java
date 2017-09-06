package de.jcup.basheditor.scriptmodel;

public class BashError implements ValidationResult {
	
	private int end;
	private int start;
	private String message;

	public BashError(int start, int end, String message){
		this.start=start;
		this.end=end;
		this.message=message;
	}

	@Override
	public int getStart() {
		return start;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public int getEnd() {
		return end;
	}
	
	@Override
	public Type getType() {
		return Type.ERROR;
	}
}
