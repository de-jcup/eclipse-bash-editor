package de.jcup.basheditor.script.parser;

/**
 * A context only for here-doc (see http://tldp.org/LDP/abs/html/here-docs.html)
 * @author Albert Tregnaghi
 *
 */
class HereDocContext{
	CodePosSupport codePosSupport;
	StringBuilder content;
	StringBuilder partScan;
	boolean endliteralFound;
	int closingLiteralTokenEnd=-1;
	int closingLiteralTokenStart=-1;
	int contentTokenEnd=-1;
	int contentTokenStart=-1;

	Character lastCharacter;
	StringBuilder literal;
	int hereDocTokenStart=-1;
	int hereDocPos;
	int hereDocTokenEnd;
	
	public HereDocContext(CodePosSupport codePosSupport){
		if (codePosSupport==null){
			throw new IllegalArgumentException("codePosSupport may not be null!");
		}
		this.codePosSupport=codePosSupport;
		this.partScan=new StringBuilder();
		this.content=new StringBuilder();
	}
	
	public void moveToNewEndPosition(int newPos) {
		codePosSupport.moveToPos(newPos);
	}

	public int getHereDocPos() {
		return codePosSupport.getInitialStartPos();
	}

	public Character getCharacterAtPosOrNull(int pos) {
		if (codePosSupport==null){
			return null;
		}
		return codePosSupport.getCharacterAtPosOrNull(pos);
	}
	
	public String getContent() {
		char lastContentChar = content.charAt(content.length() - 1);

		if (Character.isWhitespace(lastContentChar)) {
			/* remove last whitespace */
			int contentLength = content.length() - 1;
			return content.substring(0, contentLength);
		} else {
			return content.toString();
		}
	}

	public boolean isHereDocValid() {
		if (!endliteralFound) {
			return false;
		}
		if (content.length() == 0) {
			return false;
		}
		return true;
	}

	public boolean isNoHereDocFound() {
		return hereDocTokenStart == -1;
	}
	
	public String getLiteral() {
		return literal.toString();
	}

	public boolean hasNoLiteral() {
		return literal==null || literal.length()==0;
	}
	
	
}