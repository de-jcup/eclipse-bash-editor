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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
import de.jcup.basheditor.debug.launch.TerminalLaunchContext;
import de.jcup.basheditor.debug.launch.TerminalLaunchContextBuilder;
import de.jcup.basheditor.debug.launch.TerminalLauncher;

public class BashEditorLaunchPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
    private static final String TOOLTIP_HEADER_TESTOUTPUT = "Here you will find the last output created by `Show result cmd`.\n"
            + "It is the resulting terminal command, which means you can paste this command inside your CLI and test if the command works\n"
            + "(means you will see a new terminal appearing which executes a testscript)";
    private BooleanFieldEditor showMetaInfoInDebugConsoleEnabled;
    private BooleanFieldEditor keepExternalTerminalOpenOnErrors;
    private StringFieldEditor launchTerminalCommand;
    private Button testTerminalButton;
    private BooleanFieldEditor keepExternalTerminalOpenAlways;
    private Text testCommandOutputText;
    private StringFieldEditor launchStarterCommand;
    private StringFieldEditor customUserHomePath;
    private StringFieldEditor justOpenTerminalCommand;
    private BooleanFieldEditor showOpenPathInTerminalCommandEnabled;
    private static final String params = "-a 1 -b 2";

    public BashEditorLaunchPreferencePage() {
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

        String customUserHomeTooltipText = "Normally only interesting for windows, at Linux, keep this field empty.\n" + "When using\n" + "- MinGW : it will work without changes here.\n"
                + "- WSL   : you need a to map used user home to e.g.\n" + "          /mnt/c/user/albert\n\n" + "Be aware: If you configure this wrong your debug session will not close!\n"
                + "In this case try out until its working correctly and restart eclipse to get\n" + "rid of the unclosed debug sessions...";
        customUserHomePath = new StringFieldEditor(P_USER_HOME_CUSTOMPATH.getId(), "Mapped user home", advancedSetupGroup);
        customUserHomePath.getLabelControl(advancedSetupGroup).setToolTipText(customUserHomeTooltipText);
        customUserHomePath.getTextControl(advancedSetupGroup).setToolTipText("Shows either defined custom user home mapping, or when not defined, a default mapping");
        customUserHomePath.setEmptyStringAllowed(true);
        /*
         * when not defined or empty, we use the default lookup and show it as grey
         * testCommandOutputText inside :
         */
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
        GridData debugGroupLayoutData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.BEGINNING | GridData.GRAB_HORIZONTAL);
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

        launchStarterCommand = new StringFieldEditor(P_LAUNCH_STARTER_COMMAND.getId(), "Start bash script command", terminalGroup);
        launchStarterCommand.getTextControl(terminalGroup)
                .setToolTipText("Defines how to start a command environment where terminal command can be executed.\n"
                        + "This command must be run in background, so in Linux ensure you end this command with an &\n\n" + "You must use "
                        + TerminalCommandVariable.BE_TERMINAL.getVariableRepresentation() + " as parameter here.");
        addField(launchStarterCommand);

        launchTerminalCommand = new StringFieldEditor(P_LAUNCH_TERMINAL_COMMAND.getId(), "Terminal command", terminalGroup);
        launchTerminalCommand.getTextControl(terminalGroup).setToolTipText("This represents " + TerminalCommandVariable.BE_TERMINAL.getVariableRepresentation() + " in start command before.\n"
                + "Defines the command to provide a login shell which can execute script\n" + "and keeps terminal open for debug output.\n\n" + "Here you can use "
                + TerminalCommandVariable.BE_CMD_CALL.getVariableRepresentation() + " as generated bash call script\n" + "and " + TerminalCommandVariable.BE_CMD_TITLE.getVariableRepresentation()
                + " for a title information in your terminal if you want it.\n\n" + "Press `Show result cmd` button to show calculated variant.");
        addField(launchTerminalCommand);

        Button showTestTerminalCommandButton = new Button(terminalGroup, SWT.PUSH);
        showTestTerminalCommandButton.setText("Show result cmd");
        showTestTerminalCommandButton.setToolTipText("Will show resulting cmd call for your given command\n\n" + "So you are able to test if the terminal command works.");
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
        testCommandOutputGridData.verticalSpan = 3;
        testCommandOutputGridData.grabExcessVerticalSpace = true;
        testCommandOutputGridData.heightHint = 100;

        testCommandOutputText = new Text(terminalGroup, SWT.MULTI | SWT.LEAD | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        testCommandOutputText.setLayoutData(testCommandOutputGridData);
        testCommandOutputText.setText("");
        testCommandOutputText.setToolTipText(TOOLTIP_HEADER_TESTOUTPUT);
        testCommandOutputText.setEditable(false);
        
        showOpenPathInTerminalCommandEnabled = new BooleanFieldEditor(P_OPEN_PATH_IN_TERMINAL_ENABLED.getId(), "Show 'Open path in terminal'", terminalGroup);
        showOpenPathInTerminalCommandEnabled.getDescriptionControl(terminalGroup).setToolTipText("When enabled the 'Open path in terminal' command will be shown inside project and package explorer.");
        addField(showOpenPathInTerminalCommandEnabled);
        
        justOpenTerminalCommand = new StringFieldEditor(P_JUST_OPEN_TERMINAL_COMMAND.getId(), "Command for 'Open path in terminal'", terminalGroup);
        justOpenTerminalCommand.getTextControl(terminalGroup).setToolTipText("Defines command used for 'Open path in terminal' action inside project or package explorer view.");
        addField(justOpenTerminalCommand);

    }

    private Object doShowCommandString() {
        try {
            TerminalLaunchContext context = createTestLaunchContext();
            testCommandOutputText.setText(context.getTerminalExecutionCommand());
            testCommandOutputText.setToolTipText(TOOLTIP_HEADER_TESTOUTPUT + "\n\nBash editor will launch this:\n" + context.getLaunchTerminalCommand());
        } catch (IOException e) {
            EclipseUtil.logError("Was not able execute test", e);
        }
        return null;
    }

    private TerminalLaunchContext createTestLaunchContext() throws IOException {
        boolean waitOnError = keepExternalTerminalOpenOnErrors.getBooleanValue();
        boolean alwaysWait = keepExternalTerminalOpenAlways.getBooleanValue();
        String terminalCommand = launchTerminalCommand.getStringValue();
        String starterCommand = launchStarterCommand.getStringValue();

        /* @formatter:off*/
        TerminalLaunchContext context = TerminalLaunchContextBuilder.builder().
                file(createTempFile()).
                params(params).
                waitingOnErrors(waitOnError).
                waitingAlways(alwaysWait).
                terminalCommand(terminalCommand).
                starterCommand(starterCommand).build();
        /* @formatter:on*/
        return context;

    }

    private static File createTempFile() throws IOException {
        // --------------------------------------------------------------------------------------------------------------------------------|123456789
        Path tempFile = Files.createTempFile("terminallaunch", ".sh");
        File temp = tempFile.toFile();
        temp.setExecutable(true, true);

        try (FileWriter fw = new FileWriter(temp); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("#! /bin/bash\n");
            bw.write("echo 'A simple test'\n");
            bw.write("echo $1 $2 $3\n");
            bw.write("exit 1");
        }
        return temp;
    }

    private Object doExecuteTestScript() {
        try {
            TerminalLauncher launcher = new TerminalLauncher();
            launcher.execute(createTestLaunchContext());

        } catch (IOException e) {
            EclipseUtil.logError("Was not able execute test", e);
        }
        return null;
    }

}