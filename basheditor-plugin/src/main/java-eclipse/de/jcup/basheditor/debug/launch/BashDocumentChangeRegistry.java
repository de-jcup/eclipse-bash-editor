package de.jcup.basheditor.debug.launch;

import java.util.Vector;

import org.eclipse.core.resources.IFile;

public class BashDocumentChangeRegistry {

	private Vector<DocumentChanges> bashDocumentsChanges = new Vector<DocumentChanges>();
	
	public static final BashDocumentChangeRegistry INSTANCE = new BashDocumentChangeRegistry();
	
	public class DocumentChange {
		public DocumentChange(int line, int numLines) {
			this.line = line;
			this.numLines = numLines;
		}

		public int line;
		public int numLines;
	}

	public class DocumentChanges {
		public IFile file;
		public Vector<DocumentChange> changes = new Vector<DocumentChange>();
	}
	
	private BashDocumentChangeRegistry() {
		
	}
	
	public void documentChanged(IFile file, int line, int numLines) {
		if (file == null) {
			return;
		}
		DocumentChanges docChanges = getDocumentChanges(file);
		if (docChanges == null) {
			docChanges = new DocumentChanges();
			docChanges.file = file;
			bashDocumentsChanges.add(docChanges);
		}
		docChanges.changes.add(new DocumentChange(line + 1, numLines));
	}

	public DocumentChanges getDocumentChanges(String source) {
		int p = source.indexOf("@");
		if (p != -1) {
			source = source.substring(p + 1);
		}
		IFile file = BashSourceLookupParticipant.getLookupSourceItem(source);
		if (file == null) {
			return null;
		}
		DocumentChanges docChanges = getDocumentChanges(file);
		return docChanges;
	}

	public DocumentChanges getDocumentChanges(IFile file) {
		DocumentChanges docChanges = null;
		for (int i = 0; i < bashDocumentsChanges.size(); i++) {
			docChanges = bashDocumentsChanges.get(i);
			if (docChanges.file.equals(file)) {
				break;
			}
		}
		return docChanges;
	}
}
