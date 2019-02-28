package de.jcup.basheditor.debug;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.ui.texteditor.ITextEditor;

public class BashLineBreakpointAdapterFactory implements IAdapterFactory {
	@SuppressWarnings("unchecked")
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (adaptableObject instanceof ITextEditor) {
			ITextEditor editorPart = (ITextEditor) adaptableObject;
			IResource resource = (IResource) editorPart.getEditorInput().getAdapter(IResource.class);
			if (resource != null) {
				return (T) new BashLineBreakpointAdapter();
			}
		}
		return null;
	}

	public Class<?>[] getAdapterList(){
		return new Class[] { IToggleBreakpointsTarget.class };
	}
}
