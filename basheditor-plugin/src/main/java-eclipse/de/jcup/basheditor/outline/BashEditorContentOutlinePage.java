package de.jcup.basheditor.outline;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import de.jcup.basheditor.BashEditor;

public class BashEditorContentOutlinePage extends ContentOutlinePage implements IDoubleClickListener {

	private ITreeContentProvider contentProvider;
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
	
	public void rebuild(IDocument document) {
		if (document==null){
			return;
		}
		TreeViewer treeViewer= getTreeViewer();
		if (treeViewer==null){
			return;
		}
		treeViewer.setInput(document);
		
	}
}
