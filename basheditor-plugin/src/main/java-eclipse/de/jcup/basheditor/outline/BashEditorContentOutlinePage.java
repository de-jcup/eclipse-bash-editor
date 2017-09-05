/*
 * Copyright 2017 Albert Tregnaghi
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
 package de.jcup.basheditor.outline;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import de.jcup.basheditor.BashEditor;
import de.jcup.basheditor.scriptmodel.BashScriptModel;

public class BashEditorContentOutlinePage extends ContentOutlinePage implements IDoubleClickListener {

	private BashEditorTreeContentProvider contentProvider;
	private Object input;
	private BashEditor editor;
	private BashEditorOutlineLabelProvider labelProvider;
	
	public BashEditorContentOutlinePage(BashEditor editor) {
		this.editor=editor;
		this.contentProvider=new BashEditorTreeContentProvider();
	}
	
	public void createControl(Composite parent) {
		super.createControl(parent);

		labelProvider = new BashEditorOutlineLabelProvider();

		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(contentProvider);
		viewer.addDoubleClickListener(this);
		viewer.setLabelProvider(new DelegatingStyledCellLabelProvider(labelProvider));
		viewer.addSelectionChangedListener(this);
		
		/* it can happen that input is already updated before control created*/
		if (input!=null){
			viewer.setInput(input);
		}

	}

	@Override
	public void doubleClick(DoubleClickEvent event) {
		if (editor==null){
			return;
		}
		ISelection selection = event.getSelection();
		editor.openSelectedTreeItemInEditor(selection,true);
	}
	
	public void rebuild(BashScriptModel model) {
		if (model==null){
			return;
		}
		contentProvider.rebuildTree(model);

		TreeViewer treeViewer= getTreeViewer();
		if (treeViewer!=null){
			treeViewer.setInput(model);
		}
	}
	
}
