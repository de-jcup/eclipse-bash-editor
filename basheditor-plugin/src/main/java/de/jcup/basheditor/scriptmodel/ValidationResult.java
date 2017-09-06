package de.jcup.basheditor.scriptmodel;

public interface ValidationResult {
	
	public enum Type{
		ERROR
	}

	int getStart();

	int getEnd();

	String getMessage();
	
	Type getType();
	
}