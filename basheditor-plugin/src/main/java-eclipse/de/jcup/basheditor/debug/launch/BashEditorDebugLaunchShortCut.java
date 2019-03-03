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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ILaunchShortcut2;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import de.jcup.basheditor.debug.BashDebugConstants;
import de.jcup.eclipse.commons.ui.EclipseUtil;

/**
 * Short cut launcher for debugger
 * 
 */
public class BashEditorDebugLaunchShortCut implements ILaunchShortcut2 {

	public void launch(ISelection selection, String mode) {
		if (selection instanceof IStructuredSelection) {
			String typeSelectionTitle = getTypeSelectionTitle();
			String selectionEmptyMessage = getSelectionEmptyMessage();
			Object[] array = ((IStructuredSelection) selection).toArray();
			searchAndLaunch(array, null, mode, typeSelectionTitle, selectionEmptyMessage);
		}
	}

	public void launch(IEditorPart editor, String mode) {
		IEditorInput input = editor.getEditorInput();
		Object additionalScope = resolveEditorAdditonalScope(editor);
		IResource resource = (IResource) input.getAdapter(IResource.class);
		if (resource == null) {
			return;
		}
		searchAndLaunch(new Object[] { resource }, additionalScope, mode, getTypeSelectionTitle(), getEditorEmptyMessage());
	}
	
	/**
     * Resolves a type that can be launched from the given scope and launches in the
     * specified mode.
     * 
     * @param resources       the java children to consider for a type that can be
     *                        launched
     * @param mode            launch mode
     * @param selectTitle     prompting title for choosing a type to launch
     * @param additionalScope additional scope for launch
     * @param emptyMessage    error message when no types are resolved for launching
     */
    protected void searchAndLaunch(Object[] resources, Object additionalScope, String mode, String selectTitle, String emptyMessage) {
        IResource resource = null;
        Object object = resources[0];
        if (object instanceof IResource) {
            resource = (IResource) object;
        } else if (object instanceof IAdaptable) {
            IAdaptable adaptable = (IAdaptable) object;
            resource = getLaunchableResource(adaptable);
        } else {
            throw new IllegalArgumentException("Bash debug launch shortcut cannot handle object type:" + object);
        }
        if (resource != null) {
            launch(resource, additionalScope, mode);
        }
    }

	public IResource getLaunchableResource(IEditorPart editorpart) {
		return getLaunchableResource(editorpart.getEditorInput());
	}

	private IResource getLaunchableResource(IEditorInput editorInput) {
		return editorInput.getAdapter(IResource.class);
	}

	public IResource getLaunchableResource(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			if (ss.size() == 1) {
				Object element = ss.getFirstElement();
				if (element instanceof IAdaptable) {
					return getLaunchableResource((IAdaptable) element);
				}
			}
		}
		return null;
	}

	private IResource getLaunchableResource(IAdaptable element) {
		return element.getAdapter(IResource.class);
	}

	public ILaunchConfiguration[] getLaunchConfigurations(IEditorPart editorpart) {
		// let the framework resolve configurations based on resource mapping
		return null;
	}

	public ILaunchConfiguration[] getLaunchConfigurations(ISelection selection) {
		// let the framework resolve configurations based on resource mapping
		return null;
	}

	protected Object resolveEditorAdditonalScope(IEditorPart editor) {
		/* per default we do nothing here */
		return null;
	}

	/**
	 * Returns a configuration from the given collection of configurations that
	 * should be launched, or <code>null</code> to cancel. Default implementation
	 * opens a selection dialog that allows the user to choose one of the specified
	 * launch configurations. Returns the chosen configuration, or <code>null</code>
	 * if the user cancels.
	 * 
	 * @param configList list of configurations to choose from
	 * @return configuration to launch or <code>null</code> to cancel
	 */
	protected ILaunchConfiguration chooseConfiguration(List<ILaunchConfiguration> configList) {
		IDebugModelPresentation labelProvider = DebugUITools.newDebugModelPresentation();
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), labelProvider);
		dialog.setElements(configList.toArray());
		dialog.setTitle(getTypeSelectionTitle());
		dialog.setMessage(getChooseConfigurationTitle());
		dialog.setMultipleSelection(false);
		int result = dialog.open();
		labelProvider.dispose();
		if (result == Window.OK) {
			return (ILaunchConfiguration) dialog.getFirstResult();
		}
		return null;
	}

	protected String getChooseConfigurationTitle() {
		return "Choose Bash debug launch config";
	}

	/**
	 * Creates and returns a new configuration based on the specified type.
	 * 
	 * @param additionalScope additional scope which can be given
	 * @param type            type to create a launch configuration for
	 * 
	 * @return launch configuration configured to launch the specified type
	 */
	protected ILaunchConfiguration createConfiguration(IResource resource, Object additionalScope) {
		ILaunchConfiguration config = null;
		ILaunchConfigurationWorkingCopy wc = null;
		try {
			String proposal = resource.getName();

			ILaunchConfigurationType configType = getConfigurationType();
			wc = configType.newInstance(null, getLaunchManager().generateLaunchConfigurationName(proposal));
			createCustomConfiguration(resource, additionalScope, wc);

			config = wc.doSave();
		} catch (CoreException exception) {
			ErrorDialog.openError(EclipseUtil.getActiveWorkbenchShell(), "Bash debug configuration failure", "Cannot create a shortcut for dbeug launch!", exception.getStatus());
		}
		return config;
	}

	protected void createCustomConfiguration(IResource resource, Object additionalScope, ILaunchConfigurationWorkingCopy wc) {
		wc.setMappedResources(new IResource[] { getResourceToMap(resource) });
		wc.setAttribute(BashDebugConstants.LAUNCH_ATTR_BASH_PROGRAM, resource.getFullPath().toString());
		wc.setAttribute(BashDebugConstants.LAUNCH_ATTR_BASH_PARAMS, "");
		wc.setAttribute(BashDebugConstants.LAUNCH_ATTR_LAUNCH_MODE, "debug");
		wc.setAttribute(BashDebugConstants.LAUNCH_ATTR_SOCKET_PORT, BashDebugConstants.DEFAULT_DEBUG_PORT);
		wc.setAttribute(BashDebugConstants.LAUNCH_ATTR_STOP_ON_STARTUP, true);
	}

	protected IResource getResourceToMap(IResource resource) {
		if (resource instanceof IProject) {
			/* project itself is always correct */
			return resource;
		}
		if (resource instanceof IFile) {
			if (isResourceToMapFilesAllowed()) {
				return resource;
			}
			return findProject(resource);
		}
		if (resource instanceof IFolder) {
			if (isResourceToMapFoldersAllowed()) {
				return resource;
			}
			return findProject(resource);
		}
		return resource;
	}

	private IResource findProject(IResource resource) {
		IProject project = resource.getProject();
		return project;
	}

	/**
	 * Returns true, when the launch configuration resource mapping is allowed to
	 * directly map to an IFile.
	 * 
	 * @return <code>true</code> when allowed, <code>false</code> when not (so the
	 *         project of the file will be used instead)
	 */
	protected boolean isResourceToMapFilesAllowed() {
		return true;
	}

	/**
	 * Returns true, when the launch configuration resource mapping is allowed to
	 * directly map to an IFolder.
	 * 
	 * @return <code>true</code> when allowed, <code>false</code> when not (so the
	 *         project of the folder will be used instead)
	 */
	protected boolean isResourceToMapFoldersAllowed() {
		return false;
	}

	/**
	 * Collect the listing of {@link ILaunchConfiguration}s that apply to the given
	 * {@link IType} and {@link ILaunchConfigurationType}
	 * 
	 * @param resource       the type
	 * @param configType     the {@link ILaunchConfigurationType}
	 * @param additonalScope additional scope can be <code>null</code>
	 * @return the list of {@link ILaunchConfiguration}s or an empty list, never
	 *         <code>null</code>
	 * @since 3.8
	 */
	List<ILaunchConfiguration> getCandidates(IResource selectedResource, Object additionalScope, ILaunchConfigurationType configType) {
		IResource resource = getResourceToMap(selectedResource);
		List<ILaunchConfiguration> candidateConfigs = Collections.emptyList(); // empty list s fallback when next line fails
		try {
			ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(configType);
			candidateConfigs = new ArrayList<ILaunchConfiguration>(configs.length);
			for (int i = 0; i < configs.length; i++) {
				ILaunchConfiguration config = configs[i];
				if (isConfigACandidate(resource, additionalScope, config)) {
					candidateConfigs.add(config);
				}
			}
		} catch (CoreException e) {
			ErrorDialog.openError(EclipseUtil.getActiveWorkbenchShell(), "Bash debug short cut problem", " Not able to resolve candidate", e.getStatus());
		}
		return candidateConfigs;
	}

	protected boolean isConfigACandidate(IResource resource, Object additionalScope, ILaunchConfiguration config) throws CoreException {
		IResource[] mappedResources = config.getMappedResources();
		if (mappedResources == null || mappedResources.length == 0) {
			return false;
		}
		for (IResource mapped : mappedResources) {
			if (mapped == null) {
				continue;
			}
			if (mapped.equals(resource)) {
				return true;
			}

		}
		return false;
	}

	/**
	 * Returns the type of configuration this shortcut is applicable to.
	 * 
	 * @return the type of configuration this shortcut is applicable to
	 */
	protected ILaunchConfigurationType getConfigurationType() {
		return getLaunchManager().getLaunchConfigurationType("de.jcup.basheditor.debug.launchConfigurationType");
	}

	/**
	 * Returns an error message to use when the editor does not contain a type that
	 * can be launched.
	 * 
	 * @return error message when editor cannot be launched
	 */
	protected String getEditorEmptyMessage() {
		return "Nothing available to launch from Bash Editor Debugger";
	}

	/**
	 * Returns the singleton launch manager.
	 * 
	 * @return launch manager
	 */
	protected ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

	/**
	 * Returns an error message to use when the selection does not contain a type
	 * that can be launched.
	 * 
	 * @return error message when selection cannot be launched
	 */
	protected String getSelectionEmptyMessage() {
		return "Selection does not contain something launchable by Bash editor debugger";
	}

	/**
	 * Convenience method to return the active workbench window shell.
	 * 
	 * @return active workbench window shell
	 */
	protected Shell getShell() {
		return EclipseUtil.getActiveWorkbenchShell();
	}

	/**
	 * Returns a title for a type selection dialog used to prompt the user when
	 * there is more than one type that can be launched.
	 * 
	 * @return type selection dialog title
	 */
	protected String getTypeSelectionTitle() {
		return "More than one can be launched:";
	}

	protected void launch(IResource type, Object additionalScope, String mode) {
		List<ILaunchConfiguration> configs = getCandidates(type, additionalScope, getConfigurationType());
		ILaunchConfiguration config = null;
		int count = configs.size();
		if (count == 1) {
			config = configs.get(0);
		} else if (count > 1) {
			config = chooseConfiguration(configs);
			if (config == null) {
				return;
			}
		}
		if (config == null) {
			config = createConfiguration(type, additionalScope);
		}
		if (config != null) {
			DebugUITools.launch(config, mode);
		}
	}

	
}
