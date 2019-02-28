package de.jcup.basheditor.preferences;
/*
 * Copyright 2017 Albert Tregnaghi
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

import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.*;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.basheditor.BashEditorUtil;

public class BashEditorDebugPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	private BooleanFieldEditor launchInExternalTerminalEnabled;
	private BooleanFieldEditor showMetaInfoInDebugConsoleEnabled;
	private BooleanFieldEditor keepExternalTerminalOpenOnErrors;
	
	public BashEditorDebugPreferencePage() {
		super(GRID);
		setPreferenceStore(BashEditorUtil.getPreferences().getPreferenceStore());
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected void createFieldEditors() {
		
		/* --------------------- */
		/* --   Save action   -- */
		/* --------------------- */

		GridData debugGroupLayoutData = new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.GRAB_HORIZONTAL);
		debugGroupLayoutData.horizontalSpan=2;
		debugGroupLayoutData.widthHint=400;
		
		Group terminalGroup = new Group(getFieldEditorParent(),SWT.NONE);
		terminalGroup.setText("Terminal");
		terminalGroup.setLayout(new GridLayout());
		terminalGroup.setLayoutData(debugGroupLayoutData);
		
//		TerminalLauncher launer = new TerminalLauncher();
//		String exampleCommand = launer.createExampleCommand(BashEditorPreferences.getInstance().getTerminalLauncherConfig());
		
		launchInExternalTerminalEnabled = new BooleanFieldEditor(P_LAUNCH_IN_TERMINAL_ENABLED.getId(),
				"Automatically launch in terminal", terminalGroup);
		launchInExternalTerminalEnabled.getDescriptionControl(terminalGroup)
		.setToolTipText("When enabled bash launches are done automatically in external terminal. If not enabled you have to execute by your own.");
		addField(launchInExternalTerminalEnabled);
		
		keepExternalTerminalOpenOnErrors = new BooleanFieldEditor(P_KEEP_TERMINAL_OPEN_ON_ERRORS.getId(),
				"Keep terminal open on errors.", terminalGroup);
		keepExternalTerminalOpenOnErrors.getDescriptionControl(terminalGroup)
		.setToolTipText("Keep external terminal open when exit code !=0. So its possible to see output when script failed.");
		addField(keepExternalTerminalOpenOnErrors);

		/* just add an empty label as divider */
	    new Label(getFieldEditorParent(),SWT.NONE);
		
		Group consoleGroup = new Group(getFieldEditorParent(),SWT.NONE);
		consoleGroup.setText("Console");
		consoleGroup.setLayout(new GridLayout());
		consoleGroup.setLayoutData(debugGroupLayoutData);
		showMetaInfoInDebugConsoleEnabled = new BooleanFieldEditor(P_SHOW_META_INFO_IN_DEBUG_CONSOLE.getId(),
				"Show meta information in debug console", consoleGroup);
		showMetaInfoInDebugConsoleEnabled.getDescriptionControl(consoleGroup)
		.setToolTipText("When enabled some meta information used for debugging is shown in debug console");
		addField(showMetaInfoInDebugConsoleEnabled);
		
	}

}