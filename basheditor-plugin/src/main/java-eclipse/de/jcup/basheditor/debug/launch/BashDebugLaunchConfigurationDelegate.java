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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.debug.core.model.RuntimeProcess;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;

import de.jcup.basheditor.BashEditorActivator;
import de.jcup.basheditor.InfoPopup;
import de.jcup.basheditor.debug.BashDebugConstants;
import de.jcup.basheditor.debug.element.BashDebugTarget;
import de.jcup.basheditor.debug.element.FallbackBashDebugTarget;
import de.jcup.basheditor.debug.element.RunOnlyBashDebugTarget;
import de.jcup.basheditor.preferences.BashEditorPreferences;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class BashDebugLaunchConfigurationDelegate extends LaunchConfigurationDelegate {

	private BashDebugTarget debugTarget; // at the moment, we remember the debug target and allow only ONE debug session
											// - maybe we should change this in future...
	private TerminalLauncher terminalLauncher = new TerminalLauncher();

	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		boolean debug = mode.equals(ILaunchManager.DEBUG_MODE);
		boolean runOnly = mode.equals(ILaunchManager.RUN_MODE);
		if (!debug && !runOnly) {
			throw new IllegalStateException("Ony run and debug mode supported, not :" + mode);
		}

		String program = configuration.getAttribute(BashDebugConstants.LAUNCH_ATTR_BASH_PROGRAM, "");
		String params = configuration.getAttribute(BashDebugConstants.LAUNCH_ATTR_BASH_PARAMS, "");

		IWorkspaceRoot root = EclipseUtil.getWorkspace().getRoot();
		IFile programFileResource = (IFile) root.findMember(program);
		if (!canAccessBashScript(launch, program, programFileResource)) {
			return;
		}
		File programFile = programFileResource.getLocation().toFile();

		/* only for debugging we support "stopOnStartup" ... */
		boolean stopOnStartup = debug
				&& configuration.getAttribute(BashDebugConstants.LAUNCH_ATTR_STOP_ON_STARTUP, false);
		launch.setAttribute(BashDebugConstants.LAUNCH_ATTR_STOP_ON_STARTUP, Boolean.toString(stopOnStartup));
		launch.setAttribute(BashDebugConstants.LAUNCH_ATTR_LAUNCH_MODE, mode);

		int port = debug
				? configuration.getAttribute(BashDebugConstants.LAUNCH_ATTR_SOCKET_PORT,
						BashDebugConstants.DEFAULT_DEBUG_PORT)
				: -1;

		boolean canDoAutoRun = runOnly || getPreferences().isAutomaticLaunchInExternalTerminalEnabled();

		/* debug process is started, so launch terminal or inform */
		if (canDoAutoRun) {
			Process process = terminalLauncher.execute(programFile, params, getPreferences().getTerminalCommand(),
					getPreferences().getStarterCommand());

			IDebugTarget target = createDebugTargetOrNull(launch, process, debug, programFileResource, port);
			if (target != null) {
				launch.addDebugTarget(target);
//				IProcess p = target.getProcess();
//				if (p != null) {
//					launch.addProcess(p);
//				}
			}
			Map<String, String> attributes = new HashMap<String, String>();
			RuntimeProcess runtimeProcess = new RuntimeProcess(launch, process, programFile.getName(), attributes);
			launch.addProcess(runtimeProcess);
		} else {
			EclipseUtil.safeAsyncExec(new Runnable() {

				@Override
				public void run() {
					Shell shell = EclipseUtil.getSafeDisplay().getActiveShell();

					String titleText = "Bash launch necessary";
					String infoText = "You have only started the debug remote connection.\nThe bash program is currently not started.";
					String subMessage = "Either you start your bash program from commandline\nor you change your preferences to launch in terminal";

					InfoPopup popup = new InfoPopup(shell, titleText, infoText, null);
					popup.setSubMessage(subMessage);
					popup.setLinkToPreferencesId(
							"basheditor.eclipse.gradleeditor.preferences.BashEditorDebugPreferencePage");
					popup.setLinkToPreferencesText(
							"Change behaviour in <a href=\"https://github.com/de-jcup/eclipse-bash-editor\">preferences</a>");
					popup.open();
				}

			});
		}
	}

	/**
	 * # Creates debug/run target - if not possible (e.g. debug session cannot be
	 * started) the returned target is <code>null</code>
	 * 
	 * @param launch
	 * @param debug
	 * @param programFileResource
	 * @param port
	 * @return
	 * @throws CoreException
	 * @throws DebugException
	 */
	private IDebugTarget createDebugTargetOrNull(ILaunch launch, Process process, boolean debug,
			IFile programFileResource, int port) throws CoreException, DebugException {
		if (! debug) {
			return null;
		}
		IDebugTarget target = null;
		if (debug) {
			IProcess remoteProcess = new BashRemoteProcess(launch);
			terminateFormerDebugTarget();
			debugTarget = new BashDebugTarget(launch, remoteProcess, port, programFileResource);
			if (!debugTarget.startDebugSession()) {
				debugTarget.disconnect();
				FallbackBashDebugTarget fallbackTarget = new FallbackBashDebugTarget(launch,
						"Not able to start debug session");
				launch.addDebugTarget(fallbackTarget);
				fallbackTarget.terminate();
				return null;
			}
			target = debugTarget;
		} else {
			target = new RunOnlyBashDebugTarget(launch, process, programFileResource.getName(),
					new HashMap<String, String>());
		}
		return target;
	}

	/**
	 * Check if bash script can be accessed. If this is not possible, launch is
	 * terminated, a message shown and <code>false</code> is returned
	 * 
	 * @param launch
	 * @param program
	 * @param programFileResource
	 * @return <code>false</code> when bash script cannot be accessed, otherwise
	 *         <code>true</code>
	 * @throws DebugException
	 */
	private boolean canAccessBashScript(ILaunch launch, String program, IFile programFileResource)
			throws DebugException {
		if (programFileResource != null) {
			return true;
		}
		String message = "Was not able to find bash script '" + program + "' in workspace.";
		Status status = new Status(Status.ERROR, BashEditorActivator.getDefault().getPluginID(), message);
		EclipseUtil.safeAsyncExec(() -> ErrorDialog.openError(EclipseUtil.getActiveWorkbenchShell(), "Launch failed",
				"Bash script to launch not found.", status));

		FallbackBashDebugTarget fallbackTarget = new FallbackBashDebugTarget(launch, "Bash script not found");
		launch.addDebugTarget(fallbackTarget);
		fallbackTarget.terminate();
		return false;
	}

	private void terminateFormerDebugTarget() {
		if (debugTarget != null) {
			try {
				debugTarget.disconnect();
			} catch (Exception e) {
				EclipseUtil.logError("Debug target disconnect failed!", e, BashEditorActivator.getDefault());
			}
		}
	}

	private BashEditorPreferences getPreferences() {
		return BashEditorPreferences.getInstance();
	}

}
