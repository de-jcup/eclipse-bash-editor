package de.jcup.basheditor.debug;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.LineBreakpoint;

public class BashLineBreakpoint extends LineBreakpoint {

	/* Necessary for eclipse breakpoint manager (recreation) */
	public BashLineBreakpoint() {
	}

	public BashLineBreakpoint(final IResource resource, final int lineNumber) throws CoreException {
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = resource.createMarker("basheditor.debug.breakpoint.marker");
				setMarker(marker);

				marker.setAttribute(IBreakpoint.ENABLED, Boolean.TRUE);
				marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
				marker.setAttribute(IBreakpoint.ID, getModelIdentifier());
				marker.setAttribute(IMarker.MESSAGE, "Bash breakpoint: " + resource.getName() + " [line: " + lineNumber + "]");

			}
		};
		run(getMarkerRule(resource), runnable);
	}

	public String getModelIdentifier() {
		return BashDebugConstants.BASH_DEBUG_MODEL_ID;
	}
}
