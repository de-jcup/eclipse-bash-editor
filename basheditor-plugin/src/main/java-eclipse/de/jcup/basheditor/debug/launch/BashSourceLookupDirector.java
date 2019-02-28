package de.jcup.basheditor.debug.launch;

import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;

public class BashSourceLookupDirector extends AbstractSourceLookupDirector {
	
	public void initializeParticipants() {
		addParticipants(new ISourceLookupParticipant[] { new BashSourceLookupParticipant() });
	}

}
