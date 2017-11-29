package de.jcup.basheditor.document;

import org.eclipse.jface.text.rules.ICharacterScanner;

class SimpleTestCharacterScanner implements ICharacterScanner{
	int column;
	private String text;

	public SimpleTestCharacterScanner(String text){
		this.text=text;
	}
	

	@Override
	public char[][] getLegalLineDelimiters() {
		char[][] chars = new char[1][];
		chars[0]="\n".toCharArray();
		return chars;
	}

	@Override
	public int getColumn() {
		return column;
	}

	@Override
	public int read() {
		if (column>=text.length()){
			return EOF;
		}
		char c = text.substring(column, column+1).toCharArray()[0];
		column++;
		return c;
	}

	@Override
	public void unread() {
		column--;
		
	}
	
}