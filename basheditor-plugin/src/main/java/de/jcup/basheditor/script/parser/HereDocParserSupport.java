package de.jcup.basheditor.script.parser;

public class HereDocParserSupport {

	public boolean isHereDocStateHandled(CodePosSupport codePosSupport) {

		HereDocContext context= createContext(codePosSupport);
		
		if (context.isNoHereDocFound()){
			return false;
		}
		
		step1_scanForLiteral(context);
		if (context.hasNoLiteral()){
			return false;
		}

		setp2_scanForContent(context);
		if (! context.isHereDocValid()){
			return false;
		}
		context.moveToNewEndPosition(context.hereDocPos);
		
		if (codePosSupport instanceof ParseContext){
			/* when the support is a parse context we additional add 
			 * new tokens into
			 */
			ParseContext parseContext = (ParseContext) codePosSupport;
			addtokens(parseContext, context);
		}
		return true;
	}

	private void addtokens(ParseContext parseContext, HereDocContext context) {
		ParseToken hereDocToken = new ParseToken();
		hereDocToken.start = context.hereDocTokenStart;
		hereDocToken.end = context.hereDocTokenEnd;
		hereDocToken.text = "<<" + context.getLiteral();

		parseContext.addToken(hereDocToken);

		ParseToken contentToken = new ParseToken();
		contentToken.start = context.contentTokenStart;
		contentToken.end = context.contentTokenEnd;
		contentToken.text=context.getContent();

		parseContext.addToken(contentToken);

		ParseToken closingLiteralToken = new ParseToken();
		closingLiteralToken.start = context.closingLiteralTokenStart;
		closingLiteralToken.end = context.closingLiteralTokenEnd;
		closingLiteralToken.text = context.partScan.toString();

		parseContext.addToken(closingLiteralToken);
	}

	private void setp2_scanForContent(HereDocContext context) {
		/* CHECKPOINT 3: <<literal now defined */
		context.endliteralFound = false;

		// scan for content
		context.partScan = new StringBuilder();
		context.content = new StringBuilder();
		context.contentTokenStart = context.hereDocPos;
		
		do {
			if (isEndLiteralFound(context.getLiteral(), context.partScan)) {
				context.endliteralFound = true;
				context.closingLiteralTokenEnd = context.hereDocPos;
				break;
			}
			Character contentChar = context.getCharacterAtPosOrNull(context.hereDocPos++);
			if (contentChar == null) {
				break;
			}
			if (Character.isWhitespace(contentChar.charValue())) {
				/* not found - so add part scan to content */
				context.content.append(context.partScan);
				if (context.content.length() > 0) {
					context.content.append(contentChar
							.charValue()); /*
											 * add current whitespace too, when
											 * not at start
											 */
				}
				context.contentTokenEnd = context.hereDocPos - 1;

				/* reset part scan */
				context.closingLiteralTokenStart = context.hereDocPos;
				context.partScan = new StringBuilder();
			} else {
				context.partScan.append(contentChar.charValue());
			}

		} while (true);

	}

	private HereDocContext createContext(CodePosSupport codePosSupport) {
		
		HereDocContext context = new HereDocContext(codePosSupport);
		int hereDocTokenStart = context.getHereDocPos();
		
		Character init = context.getCharacterAtPosOrNull(hereDocTokenStart);
		if (init ==null){
			return context;
		}
		char c = init.charValue();
		if (c != '<') {
			return context;
		}
		/*
		 * CHECKPOINT 0: check if next is "<" as well. If so this is a
		 * here-doc...
		 */
		context.hereDocPos = hereDocTokenStart + 1;
		Character ca = context.getCharacterAtPosOrNull(context.hereDocPos++);
		if (ca == null) {
			return context;
		}
		if (ca.charValue() != '<') {
			return context;
		}
		/* CHECKPOINT 1:<< found */
		ca = context.getCharacterAtPosOrNull(context.hereDocPos++);
		if (ca == null) {
			return context;
		}
		// next line will mark also as initialized!
		context.hereDocTokenStart=hereDocTokenStart; 
		context.lastCharacter=ca;
		return context;
		
	}
	
	private void step1_scanForLiteral(HereDocContext context) {
		Character ca = context.lastCharacter;
		if (ca==null){
			return;
		}
		StringBuilder literal = new StringBuilder();
		if (Character.isWhitespace(ca.charValue())) {
			/* CHECKPOINT 2a:<<.. found so get literal */
			ca.charValue();
		} else {
			/* CHECKPOINT 2b:<< .. found so get literal */
			literal.append(ca.charValue());
		}
		do {
			Character literalChar = context.getCharacterAtPosOrNull(context.hereDocPos++);
			if (literalChar == null) {
				/* end reached but no literal - so ignore */
				return;
			}
			if (Character.isWhitespace(literalChar.charValue())) {
				break;
			}
			literal.append(literalChar.charValue());

		} while (true);
		context.literal=literal;
		context.hereDocTokenEnd = context.hereDocPos - 1;
		return;
	}

	private boolean isEndLiteralFound(String literalToFind, StringBuilder partScan) {
		if (partScan == null || partScan.length() == 0) {
			return false;
		}
		String partScanString = partScan.toString();
		if (partScanString.equals(literalToFind)) {
			return true;
		}
		/* handle tabs suppressed */
		if (literalToFind.startsWith("-")) {
			return isLiteralWhenFirstCharRemoved(literalToFind, partScanString);
		}

		/* handle Parameter substitution turned off */
		if (partScanString.length() < 3) {
			/* no possibility for 'a' or "a" ... */
			return false;
		}
		if (literalToFind.indexOf("'") == 0) {
			if (!literalToFind.endsWith("'")) {
				return false;
			}
			return isLiteralWhenFirstAndLastCharsRemoved(literalToFind, partScanString);
		}
		if (literalToFind.indexOf("\"") == 0) {
			if (!literalToFind.endsWith("\"")) {
				return false;
			}
			return isLiteralWhenFirstAndLastCharsRemoved(literalToFind, partScanString);
		}
		return false;

	}

	private boolean isLiteralWhenFirstAndLastCharsRemoved(String literalToFind, String partScanString) {
		String literalShrinked = literalToFind.substring(1, literalToFind.length() - 1);
		boolean isLiteral = partScanString.equals(literalShrinked);
		return isLiteral;
	}

	private boolean isLiteralWhenFirstCharRemoved(String literalToFind, String partScanString) {
		String literalShrinked = literalToFind.substring(1, literalToFind.length());
		boolean isLiteral = partScanString.equals(literalShrinked);
		return isLiteral;
	}

}
