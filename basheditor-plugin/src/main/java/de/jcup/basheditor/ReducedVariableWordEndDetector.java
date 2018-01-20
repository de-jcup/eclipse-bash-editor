package de.jcup.basheditor;

public class ReducedVariableWordEndDetector implements WordEndDetector{

	@Override
	public boolean isWordEnd(char c) {
		return Character.isWhitespace(c)|| c=='=' || c=='[';
	}

}
