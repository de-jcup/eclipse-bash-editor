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

import static de.jcup.basheditor.debug.BashDebugConstants.LAUNCH_ENVIRONMENT_PROPERTIES;

import java.io.File;
import java.util.Collections;
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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import de.jcup.basheditor.BashEditorActivator;
import de.jcup.basheditor.BashEditorUtil;
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
		if (monitor.isCanceled()) {
			return;
		}
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

		boolean shellScriptIsExecutable = handleNotExecutable(programFile);

		/* only for debugging we support "stopOnStartup" ... */
		boolean stopOnStartup = debug
				&& configuration.getAttribute(BashDebugConstants.LAUNCH_ATTR_STOP_ON_STARTUP, false);
		launch.setAttribute(BashDebugConstants.LAUNCH_ATTR_STOP_ON_STARTUP, Boolean.toString(stopOnStartup));
		launch.setAttribute(BashDebugConstants.LAUNCH_ATTR_LAUNCH_MODE, mode);

		int port = debug
				? configuration.getAttribute(BashDebugConstants.LAUNCH_ATTR_SOCKET_PORT,
						BashDebugConstants.DEFAULT_DEBUG_PORT)
				: -1;

		boolean canDoAutoRun = shellScriptIsExecutable && (runOnly || getPreferences().isAutomaticLaunchInExternalTerminalEnabled());

		/* debug process is started, so launch terminal or inform */
		if (canDoAutoRun) {
			Map<String, String> environment = configuration.getAttribute(LAUNCH_ENVIRONMENT_PROPERTIES,
					Collections.emptyMap());

		
			terminalLauncher.removeOldTerminalsOfPort(port, monitor);
			
			if (monitor.isCanceled()) {
				return;
			}
			
			Process process = terminalLauncher.execute(programFile, params, getPreferences().getTerminalCommand(),
					getPreferences().getStarterCommand(), environment,port);

			IDebugTarget target = createDebugTargetOrNull(launch, process, debug, programFileResource, port,terminalLauncher);
			if (target != null) {
				launch.addDebugTarget(target);
			}
			if (!debug) {
				/* we need this process for run mode */
				Map<String, String> attributes = new HashMap<String, String>();
				RuntimeProcess runtimeProcess = new RuntimeProcess(launch, process, programFile.getName(), attributes);
				launch.addProcess(runtimeProcess);
			} else {
			    if (target!=null) {
			        /* we use created debug remote process */
			        IProcess process2 = target.getProcess();
			        launch.addProcess(process2);
			    }else {
			        FallbackProcess process3 = new FallbackProcess(launch);
			        process3.setLabel("No debug target available for: " + programFile.getName());
			        launch.addProcess(process3);
			    }
			}
		} else {
			
			if (shellScriptIsExecutable) {
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
			
			FallbackProcess process = new FallbackProcess(launch);
			process.setLabel("Not executable: " + programFile.getName());
			launch.addProcess(process);
			launch.addDebugTarget(new FallbackBashDebugTarget(launch,"Execution failed"));
			launch.terminate();
		}
	}

	private boolean handleNotExecutable(File programFile) {
		if (programFile == null) {
			BashEditorUtil.logError("Cannot execute, because file null", new IllegalStateException("file is null"));
			return false;
		}
		if (programFile.canExecute()) {
			return true;
		}
		EclipseUtil.safeAsyncExec(new Runnable() {

			@Override
			public void run() {
				Shell shell = EclipseUtil.getSafeDisplay().getActiveShell();

				String titleText = "Bashscript '" + programFile.getName() + "' cannot be executed";
				String infoText = "The file:\n\n" + programFile.getAbsolutePath()
						+ "\n\ncannot be executed by user who started eclipse / or any user at all.\n\n"
						+ "Please define correct permissions to the file and try again";
				MessageDialog.openError(shell, titleText, infoText);
			}

		});
		return false;
	}

	/**
	 * # Creates debug/run target - if not possible (e.g. debug session cannot be
	 * started) the returned target is <code>null</code>
	 * 
	 * @param launch
	 * @param debug
	 * @param programFileResource
	 * @param port
	 * @param terminalLauncher2 
	 * @return
	 * @throws CoreException
	 * @throws DebugException
	 */
	private IDebugTarget createDebugTargetOrNull(ILaunch launch, Process terminalProcess, boolean debug,
			IFile programFileResource, int port, TerminalLauncher terminalLauncher) throws CoreException, DebugException {
		if (!debug) {
			return null;
		}
		IDebugTarget target = null;
		if (debug) {
			IProcess remoteProcess = new BashRemoteProcess(launch, terminalProcess);
			terminateFormerDebugTarget();
			debugTarget = new BashDebugTarget(launch, remoteProcess, port, programFileResource,terminalLauncher);
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
			target = new RunOnlyBashDebugTarget(launch, terminalProcess, programFileResource.getName(),
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
