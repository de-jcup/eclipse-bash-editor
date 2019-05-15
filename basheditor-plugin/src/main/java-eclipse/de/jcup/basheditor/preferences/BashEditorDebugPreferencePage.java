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

import static de.jcup.basheditor.NeonCompatiblity.widgetSelectedAdapter;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_KEEP_TERMINAL_OPEN_ALWAYS;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_KEEP_TERMINAL_OPEN_ON_ERRORS;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_LAUNCH_IN_TERMINAL_ENABLED;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_LAUNCH_XTERMINAL_SNIPPET;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_SHOW_META_INFO_IN_DEBUG_CONSOLE;

import java.io.IOException;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.basheditor.BashEditorUtil;
import de.jcup.basheditor.EclipseUtil;
import de.jcup.basheditor.debug.launch.OSUtil;
import de.jcup.basheditor.debug.launch.TerminalLaucherTestExecution;

public class BashEditorDebugPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	private BooleanFieldEditor launchInExternalTerminalEnabled;
	private BooleanFieldEditor showMetaInfoInDebugConsoleEnabled;
	private BooleanFieldEditor keepExternalTerminalOpenOnErrors;
	private StringFieldEditor launchXterminalSnippet;
	private Button testTerminalButton;
	private BooleanFieldEditor keepExternalTerminalOpenAlways;

	public BashEditorDebugPreferencePage() {
		super(GRID);
		setPreferenceStore(BashEditorUtil.getPreferences().getPreferenceStore());
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected void createFieldEditors() {

		GridData debugGroupLayoutData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		debugGroupLayoutData.horizontalSpan = 2;
		debugGroupLayoutData.widthHint = 400;

		/* ----------------- */
		/* -- TERMINAL -- */
		/* ----------------- */
		Group terminalGroup = new Group(getFieldEditorParent(), SWT.NONE);
		terminalGroup.setText("Terminal");
		terminalGroup.setLayout(new GridLayout(2, false));
		terminalGroup.setLayoutData(debugGroupLayoutData);

		launchInExternalTerminalEnabled = new BooleanFieldEditor(P_LAUNCH_IN_TERMINAL_ENABLED.getId(), "Automatically launch in terminal", terminalGroup);
		launchInExternalTerminalEnabled.getDescriptionControl(terminalGroup)
				.setToolTipText("When enabled bash launches are done automatically in external terminal. If not enabled you have to execute by your own.");
		addField(launchInExternalTerminalEnabled);

		keepExternalTerminalOpenOnErrors = new BooleanFieldEditor(P_KEEP_TERMINAL_OPEN_ON_ERRORS.getId(), "Keep terminal open on errors.", terminalGroup);
		keepExternalTerminalOpenOnErrors.getDescriptionControl(terminalGroup).setToolTipText("Keep external terminal open when exit code !=0. So its possible to see output when script failed.");
		addField(keepExternalTerminalOpenOnErrors);

		keepExternalTerminalOpenAlways = new BooleanFieldEditor(P_KEEP_TERMINAL_OPEN_ALWAYS.getId(), "Keep terminal always open.", terminalGroup);
		keepExternalTerminalOpenAlways.getDescriptionControl(terminalGroup).setToolTipText("Keep external terminal  always open, even when exit code =0.");
		addField(keepExternalTerminalOpenAlways);

		launchXterminalSnippet = new StringFieldEditor(P_LAUNCH_XTERMINAL_SNIPPET.getId(), "XTerminal command", terminalGroup);
		launchXterminalSnippet.getTextControl(terminalGroup)
				.setToolTipText("Define your XTerminal command with valid option to execute a bash snippet.\nDefault value is suitable for alternative x-terminal set to gnome or mate-terminal");
		addField(launchXterminalSnippet);

		if ( OSUtil.isWindows()) {
			/* we disable this option at Linux */
			launchXterminalSnippet.setEnabled(false, terminalGroup);
		}

		testTerminalButton = new Button(terminalGroup, SWT.PUSH);
		testTerminalButton.setText("Test Terminal");
		testTerminalButton.setToolTipText("Will execute a test bash script which will fail with exit code 1.\n\nSo you are able to test out the behaviours of \n'Keep terminal open on errors/always'");
		testTerminalButton.addSelectionListener(widgetSelectedAdapter(e -> doValidateExternalTool()));

		GridData testTerminalButtonGridData = new GridData();
		testTerminalButtonGridData.horizontalAlignment = GridData.END;
		testTerminalButtonGridData.horizontalSpan = 2;
		testTerminalButton.setLayoutData(testTerminalButtonGridData);

		/* just add an empty label as divider */
		new Label(getFieldEditorParent(), SWT.NONE);

		/* ---------------- */
		/* -- CONSOLE -- */
		/* ---------------- */
		Group consoleGroup = new Group(getFieldEditorParent(), SWT.NONE);
		consoleGroup.setText("Console");
		consoleGroup.setLayout(new GridLayout());
		consoleGroup.setLayoutData(debugGroupLayoutData);

		showMetaInfoInDebugConsoleEnabled = new BooleanFieldEditor(P_SHOW_META_INFO_IN_DEBUG_CONSOLE.getId(), "Show meta information in debug console", consoleGroup);
		showMetaInfoInDebugConsoleEnabled.getDescriptionControl(consoleGroup).setToolTipText("When enabled some meta information used for debugging is shown in debug console");
		addField(showMetaInfoInDebugConsoleEnabled);

	}

	private Object doValidateExternalTool() {
		try {
			TerminalLaucherTestExecution.tryToExecuteTemporaryTestBashScript();
		} catch (IOException e) {
			EclipseUtil.logError("Was not able execute test", e);
		}
		return null;
	}
	

}