package de.jcup.basheditor.document;

import org.eclipse.jface.text.rules.ICharacterScanner;

import de.jcup.basheditor.script.parser.CodePosSupport;

public class ICharacterScannerCodePosSupport implements CodePosSupport {

	private ICharacterScanner scanner;
	private int startPos;
	private Counter counter;
	private int lastReadValue=ICharacterScanner.EOF;
	private int pos;

	public ICharacterScannerCodePosSupport(ICharacterScanner scanner){
		this.scanner=scanner;
		this.counter=new Counter();
		// startPos start will always be 0. this is okay, here, because no tokens will be created so the information can start from 0 and
		// is okay. also an ICharacterScanner does not support real startPos handling but only forward and backward
		this.startPos=0;
		this.pos=-1;
	}
	
	@Override
	public void moveToPos(int newPos) {
		if (newPos==pos){
			return;
		}
		
		while(newPos<pos){
			moveBack();
		}
		
		while(newPos>pos){
			moveForward();
		}
		
	}

	private void moveForward() {
		pos++;
		lastReadValue = scanner.read();
		counter.count++;
		
	}

	private void moveBack() {
		pos--;
		
		scanner.unread();
		scanner.unread();
		lastReadValue = scanner.read();
		counter.count--;
	}

	@Override
	public int getInitialStartPos() {
		return startPos;
	}

	@Override
	public Character getCharacterAtPosOrNull(int pos) {
		moveToPos(pos);
		if (lastReadValue == ICharacterScanner.EOF){
			return null;
		}
		char lastCharacter = (char)lastReadValue;
		return Character.valueOf(lastCharacter);
	}

	/**
	 * Reset cursor movements
	 */
	public void resetToStartPos() {
		counter.cleanupAndReturn(scanner, false);
	}

}
