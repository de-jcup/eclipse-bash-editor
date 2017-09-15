package de.jcup.basheditor.document;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.Token;

public class BashStringRule extends PatternRule {

	private static final boolean BREAKS_ON_EOL = false; // support multi line strings!
	private static final boolean BREAKS_ON_EOF = true;
	private static final boolean ESCAPE_CONTINUES_LINE = false;


	public BashStringRule(String startSequence, String endSequence, IToken token) {
		super(startSequence, endSequence, token, '\\', BREAKS_ON_EOL, BREAKS_ON_EOF, ESCAPE_CONTINUES_LINE);
	}

	
	protected IToken doEvaluate(ICharacterScanner scanner, boolean resume) {

		if (resume) {

			if (endSequenceDetected(scanner))
				return fToken;

		} else {
			int c= scanner.read();
			if (c == fStartSequence[0]) {
				scanner.unread();
				scanner.unread();
				int before = scanner.read();
				
				if (before=='\\'){
					return Token.UNDEFINED;
				}
				scanner.read();
				
				if (sequenceDetected(scanner, fStartSequence, false)) {
					if (endSequenceDetected(scanner))
						return fToken;
				}
			}
		}

		scanner.unread();
		return Token.UNDEFINED;
	}
	
}
