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
package de.jcup.basheditor.debug;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.sourcelookup.CommonSourceNotFoundEditor;
import org.eclipse.debug.ui.sourcelookup.CommonSourceNotFoundEditorInput;
import org.eclipse.debug.ui.sourcelookup.ISourceLookupResult;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import de.jcup.basheditor.BashEditor;
import de.jcup.basheditor.debug.element.BashStackFrame;
import de.jcup.basheditor.debug.launch.BashSourceLookupParticipant;
import de.jcup.basheditor.debug.launch.config.BashSelectionDialog;

public class BashSourceNotFoundEditor extends CommonSourceNotFoundEditor {

	protected void selectBashSource() {
		BashSelectionDialog dialog = new BashSelectionDialog(getEditorSite().getWorkbenchWindow().getShell(), ResourcesPlugin.getWorkspace().getRoot(), IResource.FILE);
		CommonSourceNotFoundEditorInput editorInput = (CommonSourceNotFoundEditorInput) getEditorInput();
		BashStackFrame frame = (BashStackFrame) editorInput.getArtifact();
		dialog.setTitle("Unknown source for: " + frame.getSourceFileName());
		if (dialog.open() == Window.OK) {
			Object[] files = dialog.getResult();
			IFile file = (IFile) files[0];
			BashSourceLookupParticipant.putLookupSourceItem(frame.getSourceFileName(), file);
			SourceLookupResult result = new SourceLookupResult(frame, file, BashEditor.EDITOR_ID, new FileEditorInput(file));
			DebugUITools.displaySource(result, getSite().getPage());
		}
	}

	protected void createButtons(Composite parent) {
		GridData data;
		Button button = new Button(parent, SWT.PUSH);
		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		button.setLayoutData(data);
		button.setText("Select Bash script");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				selectBashSource();
			}
		});
	}

	public static class SourceLookupResult implements ISourceLookupResult {
		private Object fArtifact;
		private Object fSourceElement;
		private String fEditorId;
		private IEditorInput fEditorInput;

		public SourceLookupResult(Object artifact, Object sourceElement, String editorId, IEditorInput editorInput) {
			fArtifact = artifact;
			fSourceElement = sourceElement;
			fEditorId = editorId;
			fEditorInput = editorInput;
		}

		public Object getArtifact() {
			return fArtifact;
		}

		public Object getSourceElement() {
			return fSourceElement;
		}

		public String getEditorId() {
			return fEditorId;
		}

		public IEditorInput getEditorInput() {
			return fEditorInput;
		}
	}

}
