package de.jcup.basheditor.document;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class BashVariableRule implements IPredicateRule {

	private BashVariableDetector bashVariableDetector;
	private IToken token;

	public BashVariableRule(BashVariableDetector bashVariableDetector, IToken token) {
		this.bashVariableDetector = bashVariableDetector;
		this.token = token;
	}

	@Override
	public IToken getSuccessToken() {
		return token;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		return evaluate(scanner, false);
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		char start =(char) scanner.read();
		if (! bashVariableDetector.isWordStart(start)){
			scanner.unread();
			return Token.UNDEFINED;
		}
		
		/* okay is a variable, so read until end reached */
		do{
			char c = (char) scanner.read();
			if (ICharacterScanner.EOF== c || (! bashVariableDetector.isWordPart(c))){
				scanner.unread();
				break;
			}
		}while(true);
		return getSuccessToken();
	}

}
