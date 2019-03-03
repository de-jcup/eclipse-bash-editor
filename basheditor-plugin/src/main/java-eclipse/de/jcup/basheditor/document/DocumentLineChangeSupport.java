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
package de.jcup.basheditor.document;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;

import de.jcup.basheditor.BashEditor;
import de.jcup.basheditor.BashEditorActivator;
import de.jcup.basheditor.debug.launch.BashDocumentChangeRegistry;
import de.jcup.eclipse.commons.EclipseResourceHelper;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class DocumentLineChangeSupport implements IDocumentListener{

	public IFile file;
	private BashEditor editor;
	private BashDocumentChangeRegistry registry;
	
	public void register(BashEditor editor, BashDocumentChangeRegistry registry) throws CoreException{
		this.editor=editor;
		this.registry=registry;
		File javafile = EclipseResourceHelper.DEFAULT.getFileOfEditor(editor);
		if (javafile==null) {
			throw new CoreException(new Status(IStatus.ERROR,BashEditorActivator.getDefault().getPluginID(), "No access to file possible"));
		}
		file = EclipseResourceHelper.DEFAULT.toIFile(javafile);
		editor.getDocument().addDocumentListener(this);
	}
	
	public void unregister() {
		editor.getDocument().removeDocumentListener(this);
	}


	// IDocumentListener
	public void documentAboutToBeChanged(DocumentEvent event) {
		int line = -1;
		try {
			line = event.fDocument.getLineOfOffset(event.fOffset);
			int numLines = 0;
			if (event.fLength > 0) {
				numLines = countLines(event.fDocument.get(event.fOffset, event.fLength));
				if (numLines != 0) {
					registry.documentChanged(file, line, -1 * numLines);
				}
			}
			if (event.fText.length() > 0) {
				numLines = countLines(event.fText);
				if (numLines != 0) {
					registry.documentChanged(file, line, numLines);
				}
			}
		} catch (BadLocationException e) {
			EclipseUtil.logError("Offset problems", e, BashEditorActivator.getDefault());
		}

	}

	int countLines(String text) {
		int numLines = 0;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '\n') {
				numLines++;
			}
		}
		return numLines;
	}

	public void documentChanged(DocumentEvent event) {
	}
}
