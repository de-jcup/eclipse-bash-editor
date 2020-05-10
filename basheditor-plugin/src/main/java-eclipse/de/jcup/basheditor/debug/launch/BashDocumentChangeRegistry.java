/*
 * Copyright 2019 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.basheditor.debug.launch;

import java.util.Vector;

import org.eclipse.core.resources.IContainer;
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

	public DocumentChanges getDocumentChanges(String source, IContainer container) {
		int p = source.indexOf("@");
		if (p != -1) {
			source = source.substring(p + 1);
		}
		IFile file = BashSourceLookupParticipant.getLookupSourceItem(source,container);
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
