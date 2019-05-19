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

import static de.jcup.basheditor.NeonCompatiblity.*;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.*;

import java.io.IOException;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.basheditor.BashEditorActivator;
import de.jcup.basheditor.BashEditorUtil;
import de.jcup.basheditor.EclipseUtil;
import de.jcup.basheditor.debug.launch.TerminalCommandVariable;
import de.jcup.basheditor.debug.launch.TerminalLaucherTestExecution;

public class BashEditorDebugPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
    private BooleanFieldEditor showMetaInfoInDebugConsoleEnabled;
    private BooleanFieldEditor keepExternalTerminalOpenOnErrors;
    private StringFieldEditor launchTerminalCommand;
    private Button testTerminalButton;
    private BooleanFieldEditor keepExternalTerminalOpenAlways;
    private Text text;
    private StringFieldEditor launchStarterCommand;
    private StringFieldEditor customUserHomePath;

    public BashEditorDebugPreferencePage() {
        super(GRID);
        setPreferenceStore(BashEditorUtil.getPreferences().getPreferenceStore());
    }

    @Override
    public void init(IWorkbench workbench) {

    }

    @Override
    protected void createFieldEditors() {
        createTerminalParts();
        createConsoleParts();
        createAdvancedSetupParts();
    }

    private void createAdvancedSetupParts() {
        GridData debugGroupLayoutData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.BEGINNING);
        debugGroupLayoutData.horizontalSpan = 2;
        debugGroupLayoutData.widthHint = 400;

        Group advancedSetupGroup = new Group(getFieldEditorParent(), SWT.NONE);
        advancedSetupGroup.setText("Advanced setup");
        advancedSetupGroup.setLayout(new GridLayout(3, false));
        advancedSetupGroup.setLayoutData(debugGroupLayoutData);

        String customUserHomeTooltipText= "Normally only interesting for windows, at Linux, keep this field empty.\n"
                + "When using\n"
                + "- MinGW : it will work without changes here.\n"
                + "- WSL   : you need a to map used user home to e.g.\n"
                + "          /mnt/c/user/albert\n\n"
                + "Be aware: If you configure this wrong your debug session will not close!\n"
                + "In this case try out until its working correctly and restart eclipse to get\n"
                + "rid of the unclosed debug sessions...";
        customUserHomePath = new StringFieldEditor(P_USER_HOME_CUSTOMPATH.getId(), "Mapped user home", advancedSetupGroup);
        customUserHomePath.getLabelControl(advancedSetupGroup).setToolTipText(
                customUserHomeTooltipText);
        customUserHomePath.getTextControl(advancedSetupGroup).setToolTipText(
                "Shows either defined custom user home mapping, or when not defined, a default mapping");
        customUserHomePath.setEmptyStringAllowed(true);
        /* when not defined or empty, we use the default lookup and show it as grey text inside :*/
        customUserHomePath.getTextControl(advancedSetupGroup).setMessage(BashEditorActivator.getDefault().getDefaultScriptPathToUserHome());
        addField(customUserHomePath);
    }

    private void createConsoleParts() {
        GridData debugGroupLayoutData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.BEGINNING);
        debugGroupLayoutData.horizontalSpan = 2;
        debugGroupLayoutData.widthHint = 400;
        Group consoleGroup = new Group(getFieldEditorParent(), SWT.NONE);
        consoleGroup.setText("Console");
        consoleGroup.setLayout(new GridLayout());
        consoleGroup.setLayoutData(debugGroupLayoutData);

        showMetaInfoInDebugConsoleEnabled = new BooleanFieldEditor(P_SHOW_META_INFO_IN_DEBUG_CONSOLE.getId(), "Show meta information in debug console", consoleGroup);
        showMetaInfoInDebugConsoleEnabled.getDescriptionControl(consoleGroup).setToolTipText("When enabled some meta information used for debugging is shown in debug console");
        addField(showMetaInfoInDebugConsoleEnabled);
    }

    private void createTerminalParts() {
        GridData debugGroupLayoutData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.BEGINNING);
        debugGroupLayoutData.horizontalSpan = 2;
        debugGroupLayoutData.widthHint = 400;

        Group terminalGroup = new Group(getFieldEditorParent(), SWT.NONE);
        terminalGroup.setText("Terminal");
        terminalGroup.setLayout(new GridLayout(2, false));
        terminalGroup.setLayoutData(debugGroupLayoutData);

        keepExternalTerminalOpenOnErrors = new BooleanFieldEditor(P_KEEP_TERMINAL_OPEN_ON_ERRORS.getId(), "Keep terminal open on errors.", terminalGroup);
        keepExternalTerminalOpenOnErrors.getDescriptionControl(terminalGroup).setToolTipText("Keep external terminal open when exit code !=0. So its possible to see output when script failed.");
        addField(keepExternalTerminalOpenOnErrors);

        keepExternalTerminalOpenAlways = new BooleanFieldEditor(P_KEEP_TERMINAL_OPEN_ALWAYS.getId(), "Keep terminal always open.", terminalGroup);
        keepExternalTerminalOpenAlways.getDescriptionControl(terminalGroup).setToolTipText("Keep external terminal  always open, even when exit code =0.");
        addField(keepExternalTerminalOpenAlways);

        launchStarterCommand = new StringFieldEditor(P_LAUNCH_STARTER_COMMAND.getId(), "Start command", terminalGroup);
        launchStarterCommand.getTextControl(terminalGroup).setToolTipText(
                "Defines how to start a command environment where terminal command can be executed.\n"
                + "This command must be run in background, so in Linux ensure you end this command with an &\n\n"
                + "You must use "+TerminalCommandVariable.CMD_TERMINAL.getVariableRepresentation()+" as parameter here.");
        addField(launchStarterCommand);

        launchTerminalCommand = new StringFieldEditor(P_LAUNCH_TERMINAL_COMMAND.getId(), "Terminal command", terminalGroup);
        launchTerminalCommand.getTextControl(terminalGroup).setToolTipText(
                "This represents "+TerminalCommandVariable.CMD_TERMINAL.getVariableRepresentation()+" in start command before.\n"
                + "Defines the command to provide a login shell which can execute script\n"
                + "and keeps terminal open for debug output.\n\n"
                + "Here you can use "+TerminalCommandVariable.CMD_CALL.getVariableRepresentation()+" as generated bash call script\n"
                + "and "+TerminalCommandVariable.CMD_TITLE.getVariableRepresentation()+" for a title information in your terminal if you want it.\n\n"
                + "Press `Show result cmd` button to show calculated variant.");
        addField(launchTerminalCommand);

        Button showTestTerminalCommandButton = new Button(terminalGroup, SWT.PUSH);
        showTestTerminalCommandButton.setText("Show result cmd");
        showTestTerminalCommandButton.setToolTipText("Will show resulting cmd call for your given command\n\n"
                + "So you are able to test if the terminal command works.");
        showTestTerminalCommandButton.addSelectionListener(widgetSelectedAdapter(e -> doShowCommandString()));

        GridData showTestTerminalCommandButtonGridData = new GridData();
        showTestTerminalCommandButtonGridData.horizontalAlignment = GridData.END;
        showTestTerminalCommandButtonGridData.horizontalSpan = 1;
        showTestTerminalCommandButton.setLayoutData(showTestTerminalCommandButtonGridData);

        testTerminalButton = new Button(terminalGroup, SWT.PUSH);
        testTerminalButton.setText("Test Terminal");
        testTerminalButton.setToolTipText("Will execute a test bash script which will fail with exit code 1.\n\nSo you are able to test out the behaviours of \n'Keep terminal open on errors/always'");
        testTerminalButton.addSelectionListener(widgetSelectedAdapter(e -> doExecuteTestScript()));

        GridData testTerminalButtonGridData = new GridData();
        testTerminalButtonGridData.horizontalAlignment = GridData.END;
        testTerminalButtonGridData.horizontalSpan = 1;
        testTerminalButton.setLayoutData(testTerminalButtonGridData);
//		/* just add an empty label as divider */
//		new Label(getFieldEditorParent(), SWT.NONE);

        GridData testCommandOutputGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
//		testCommandOutputGridData.horizontalAlignment = GridData.BEGINNING;
        testCommandOutputGridData.horizontalSpan = 2;

        text = new Text(terminalGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        text.setLayoutData(testCommandOutputGridData);
        text.setText("");
        text.setToolTipText("Here you will find the last output created by `Show result cmd`.\n"
                + "You can paste this command inside an opened bash and test if the command works\n"
                + "(means you will see a new terminal appearing which executes a testscript)");
        text.setEditable(false);
    }

    private Object doShowCommandString() {
        try {
            String result = TerminalLaucherTestExecution.simulateCallCommandForTestBashScript(launchTerminalCommand.getStringValue(), launchStarterCommand.getStringValue());
            text.setText(result);
        } catch (IOException e) {
            EclipseUtil.logError("Was not able execute test", e);
        }
        return null;
    }

    private Object doExecuteTestScript() {
        try {
            TerminalLaucherTestExecution.tryToExecuteTemporaryTestBashScript(launchTerminalCommand.getStringValue(), launchStarterCommand.getStringValue());
        } catch (IOException e) {
            EclipseUtil.logError("Was not able execute test", e);
        }
        return null;
    }

}