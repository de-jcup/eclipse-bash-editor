/*
 * Copyright 2018 Albert Tregnaghi
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
package de.jcup.basheditor.handlers;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import de.jcup.basheditor.BashEditor;
import de.jcup.basheditor.BashEditorUtil;
import de.jcup.basheditor.callhierarchy.BashCallHierarchyView;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class ShowInCallHiararchyHandler extends AbstractBashEditorHandler{

	@Override
	protected void executeOnBashEditor(BashEditor bashEditor) {
		ISelection selection = bashEditor.getSelectionProvider().getSelection();
		IWorkbenchPage page = EclipseUtil.getActivePage();
		if (page==null){
		    return;
		}
		if (selection instanceof ITextSelection) {
		    ITextSelection textSelection = (ITextSelection) selection;
		    String text = textSelection.getText();
		    int offset = textSelection.getOffset();
		    try {
                IViewPart viewpart = page.showView(BashCallHierarchyView.VIEW_ID);
                if (!(viewpart instanceof BashCallHierarchyView)) {
                    BashCallHierarchyView bchv = (BashCallHierarchyView) viewpart;
                    bchv.showTextSelectionHierarchy(text,offset);
                }
            } catch (PartInitException e) {
               BashEditorUtil.logError("Not able to show hierarchy view", e);
            }
		}
	}

}
