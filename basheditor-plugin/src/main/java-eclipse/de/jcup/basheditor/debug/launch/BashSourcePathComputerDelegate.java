package de.jcup.basheditor.debug.launch;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.debug.core.sourcelookup.containers.FolderSourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.ProjectSourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.WorkspaceSourceContainer;

import de.jcup.basheditor.debug.BashDebugConstants;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class BashSourcePathComputerDelegate implements ISourcePathComputerDelegate {

	public ISourceContainer[] computeSourceContainers(ILaunchConfiguration configuration, IProgressMonitor monitor) throws CoreException {
		String path = configuration.getAttribute(BashDebugConstants.LAUNCH_ATTR_BASH_PROGRAM, (String) null);
		ISourceContainer sourceContainer = findSourceContainer(path);
		if (sourceContainer == null) {
			/* fallback */
			sourceContainer = new WorkspaceSourceContainer();
		}
		return new ISourceContainer[] { sourceContainer };
	}

	private ISourceContainer findSourceContainer(String path) {
		if (path == null) {
			return null;
		}
		IWorkspace workspace = EclipseUtil.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();

		IResource resource = root.findMember(new Path(path));
		if (resource != null) {
			IContainer container = resource.getParent();
			int containerType = container.getType();
			
			if (containerType == IResource.PROJECT) {
				return new ProjectSourceContainer((IProject) container, false);
			} else if (containerType == IResource.FOLDER) {
				return new FolderSourceContainer(container, false);
			}
		}
		return null;
	}
}
