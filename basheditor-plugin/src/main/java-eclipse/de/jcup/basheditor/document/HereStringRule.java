package de.jcup.basheditor.document;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import de.jcup.basheditor.script.parser.HereStringParserSupport;

public class HereStringRule implements IPredicateRule{

	private IToken token;
	private HereStringParserSupport hereStringSupport;
	boolean trace;

	public HereStringRule(IToken token) {
		this.token=token;
		hereStringSupport=new HereStringParserSupport();
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		return evaluate(scanner, false);
	}

	@Override
	public IToken getSuccessToken() {
		return token;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		int r = scanner.read();
		scanner.unread();
		/* fast guard closing:*/
		if (ICharacterScanner.EOF==r || r!='<'){
			return Token.UNDEFINED;
		}
		
		ICharacterScannerCodePosSupport codePosSupport = new ICharacterScannerCodePosSupport(scanner);
		if (hereStringSupport.isHereStringStateHandled(codePosSupport)){
			return getSuccessToken();
		}
		codePosSupport.resetToStartPos();
		return Token.UNDEFINED;
	}

}