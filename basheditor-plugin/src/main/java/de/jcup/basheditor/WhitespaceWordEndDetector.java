package de.jcup.basheditor;

public class WhitespaceWordEndDetector implements WordEndDetector{

	@Override
	public boolean isWordEnd(char c) {
		return Character.isWhitespace(c);
	}

}
