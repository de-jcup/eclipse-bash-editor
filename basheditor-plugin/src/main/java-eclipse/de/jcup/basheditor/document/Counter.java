package de.jcup.basheditor.document;

import org.eclipse.jface.text.rules.ICharacterScanner;

class Counter{
	int count;
	
	boolean cleanupAndReturn(ICharacterScanner scanner, boolean result){
		if (result){
			return true; // do not clean up - pos is as wanted
		}
		if (count==0){
			return result;
		}
		if (count>0){
			while(count!=0){
				scanner.unread();
				count--;
			}
		}else{
			while(count!=0){
				scanner.read();
				count++;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "count:"+count;
	}
	
}