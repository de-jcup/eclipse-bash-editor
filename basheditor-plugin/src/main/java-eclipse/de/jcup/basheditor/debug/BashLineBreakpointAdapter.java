package de.jcup.basheditor.debug;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTargetExtension;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * See https://www.eclipse.org/articles/Article-Debugger/how-to.html
 *
 */
public class BashLineBreakpointAdapter implements IToggleBreakpointsTargetExtension {// IToggleBreakpointsTarget
	public void toggleLineBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
		ITextEditor textEditor = getEditor(part);
		if (textEditor == null) {
			return;
		}
		IResource resource = (IResource) textEditor.getEditorInput().getAdapter(IResource.class);
		ITextSelection textSelection = (ITextSelection) selection;
		int lineNumber = textSelection.getStartLine();
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(BashDebugConstants.BASH_DEBUG_MODEL_ID);
		for (int i = 0; i < breakpoints.length; i++) {
			IBreakpoint breakpoint = breakpoints[i];
			if (resource.equals(breakpoint.getMarker().getResource())) {
				if (((ILineBreakpoint) breakpoint).getLineNumber() == (lineNumber + 1)) {
					// remove
					breakpoint.delete();
					return;
				}
			}
		}
		// create line breakpoint (doc line numbers start at 0)
		BashLineBreakpoint lineBreakpoint = new BashLineBreakpoint(resource, lineNumber + 1);
		DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(lineBreakpoint);
	}

	static int count = 0;

	public boolean canToggleLineBreakpoints(IWorkbenchPart part, ISelection selection) {
		return getEditor(part) != null;
	}

	private ITextEditor getEditor(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			ITextEditor editorPart = (ITextEditor) part;
			IResource resource = (IResource) editorPart.getEditorInput().getAdapter(IResource.class);
			if (resource != null) {
				return editorPart;
			}
		}
		return null;
	}

	public void toggleMethodBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
	}

	public boolean canToggleMethodBreakpoints(IWorkbenchPart part, ISelection selection) {
		return false;
	}

	public void toggleWatchpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
	}

	public boolean canToggleWatchpoints(IWorkbenchPart part, ISelection selection) {
		return false;
	}

	public boolean canToggleBreakpoints(IWorkbenchPart part, ISelection selection) {
		return canToggleLineBreakpoints(part, selection) || canToggleWatchpoints(part, selection);
	}

	public void toggleBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
		if (canToggleWatchpoints(part, selection)) {
			toggleWatchpoints(part, selection);
		} else {
			toggleLineBreakpoints(part, selection);
		}
	}

}
