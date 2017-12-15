package de.jcup.basheditor.script.parser;

public class HereStringContext {

	CodePosSupport codePosSupport;
	StringBuilder content;
	StringBuilder partScan;

	private Character lastCharacter;
	int hereStringTokenStart=-1;
	int hereStringPos;
	int hereStringTokenEnd;
	Character stringIdentifier;
	private Character lastCharacterBefore;
	boolean firstCharExceptWhitespacesCheck;
	
	public HereStringContext(CodePosSupport codePosSupport){
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

	public int getHereStringPos() {
		return codePosSupport.getInitialStartPos();
	}

	public Character getCharacterAtPosOrNull(int pos) {
		if (codePosSupport==null){
			return null;
		}
		return codePosSupport.getCharacterAtPosOrNull(pos);
	}
	
	public String getContent() {
		if (content.length()==0){
			return "";
		}
		char lastContentChar = content.charAt(content.length() - 1);

		if (Character.isWhitespace(lastContentChar)) {
			/* remove last whitespace */
			int contentLength = content.length() - 1;
			return content.substring(0, contentLength);
		} else {
			return content.toString();
		}
	}

	public boolean isNoHereStringFound() {
		return hereStringTokenStart == -1;
	}

	public void setLastCharacter(Character ca) {
		this.lastCharacterBefore = this.lastCharacter;
		this.lastCharacter=ca;
	}
	
	public Character getLastCharacter() {
		return lastCharacter;
	}
	
	public Character getLastCharacterBefore() {
		return lastCharacterBefore;
	}

	public boolean isEscaped() {
		if (lastCharacterBefore==null){
			return false;
		}
		return lastCharacterBefore.charValue()=='\\';
	}
	
	@Override
	public String toString() {
		return "HereStringContext: last:"+lastCharacter+",before:"+lastCharacterBefore+", partScan:"+partScan+", content="+content;
	}

}
