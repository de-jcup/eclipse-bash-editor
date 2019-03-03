package de.jcup.basheditor.debug;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import de.jcup.basheditor.BashEditor;
import de.jcup.basheditor.BashEditorActivator;
import de.jcup.basheditor.EclipseUtil;
import de.jcup.basheditor.debug.element.AbstractBashDebugElement;
import de.jcup.basheditor.debug.element.BashVariable;

public class BashDebugModelLabelProvider extends LabelProvider implements IDebugModelPresentation {
	private static final String FALLBACK_DETAIL_VALUE = "";

	public void setAttribute(String attribute, Object value) {
	}

	public Image getImage(Object element) {
		if (element instanceof BashVariable) {
			return EclipseUtil.getImage("icons/bash-editor.png", BashEditorActivator.getDefault().getPluginID());
		}
		if (element instanceof AbstractBashDebugElement) {
			return null;
		}
		/* return null will use defaults */
		return null;
	}

	public String getText(Object element) {
		/* return null will use defaults */
		return null;
	}

	public void computeDetail(IValue value, IValueDetailListener listener) {
		String detail = FALLBACK_DETAIL_VALUE;
		try {
			detail = value.getValueString();
		} catch (DebugException e) { 
			EclipseUtil.logError("Cannot get value as string:"+value,e);
		}
		listener.detailComputed(value, detail);
	}

	public IEditorInput getEditorInput(Object element) {
		if (element instanceof IFile) {
			return new FileEditorInput((IFile) element);
		}
		if (element instanceof ILineBreakpoint) {
			return new FileEditorInput((IFile) ((ILineBreakpoint) element).getMarker().getResource());
		}
		return null;
	}

	public String getEditorId(IEditorInput input, Object element) {
		if (element instanceof IFile || element instanceof ILineBreakpoint) {
			return BashEditor.EDITOR_ID;
		}
		return null;
	}
}
