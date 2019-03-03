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
