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
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_SAVE_ACTION_EXTERNAL_TOOL_COMMAND;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_SAVE_ACTION_EXTERNAL_TOOL_ENABLED;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.basheditor.BashEditorUtil;
import de.jcup.basheditor.ExternalToolCommandArrayBuilder;
import de.jcup.basheditor.process.BashEditorFileProcessContext;
import de.jcup.basheditor.process.OutputHandler;
import de.jcup.basheditor.process.SimpleProcessExecutor;

public class BashEditorSaveActionsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	// UI
	private BooleanFieldEditor saveActionExternalSourceFormatterEnabled;
	private StringFieldEditor saveActionExternalSourceFormatterArguments;
	private Group externalToolGroup;
	private Button validateBtn;
	
	// non-UI
	private ExternalToolCommandArrayBuilder commandArrayBuilder = new ExternalToolCommandArrayBuilder();

	public BashEditorSaveActionsPreferencePage() {
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

		GridData externalToolsGroupLayoutData = new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.GRAB_HORIZONTAL);
		//externalToolsGroupLayoutData.horizontalSpan = 1;
		//externalToolsGroupLayoutData.widthHint = 400;

		externalToolGroup = new Group(getFieldEditorParent(), SWT.NONE);
		externalToolGroup.setText("External source reformatter");
		externalToolGroup.setLayout(new GridLayout(2, false));   // create a 2-column based GRID
		externalToolGroup.setLayoutData(externalToolsGroupLayoutData);

		// NOTE: the BooleanFieldEditor() has an horizontalSpan = 2, so will take the entire first row of grid
		saveActionExternalSourceFormatterEnabled = new BooleanFieldEditor(P_SAVE_ACTION_EXTERNAL_TOOL_ENABLED.getId(),
				"Execute command on save", externalToolGroup);
		saveActionExternalSourceFormatterEnabled.getDescriptionControl(externalToolGroup).
			setToolTipText("External program to run every time a script file is saved; its output will replace current document.");
		addField(saveActionExternalSourceFormatterEnabled);

		// NOTE: the StringFieldEditor() has an horizontal span = 2, so will take the entire second row of grid
		saveActionExternalSourceFormatterArguments = 
				new StringFieldEditor(P_SAVE_ACTION_EXTERNAL_TOOL_COMMAND.getId(), "External tool command-line:", 
						StringFieldEditor.UNLIMITED, StringFieldEditor.VALIDATE_ON_FOCUS_LOST, externalToolGroup);
		addField(saveActionExternalSourceFormatterArguments);
		
		// NOTE: we create a Composite in which we place the note fields and set an horizontalSpan = 2, so will take the entire last row of grid
		Composite labelComposite = new Composite(externalToolGroup, SWT.NONE);
		labelComposite.setLayout(new GridLayout(1, false));
		GridData labelGroupLayoutData = new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.GRAB_HORIZONTAL);
		labelGroupLayoutData.horizontalSpan = 2;
		labelComposite.setLayoutData(labelGroupLayoutData);
		createNoteComposite(labelComposite.getFont(), labelComposite, 
			"Note:", "Special $filename placeholder can be used to indicate currently\n"
					 + "opened file. External tool should overwrite the given filename\n"
					 + "with the reformatted document.");

		// NOTE: we set grid data for the Validate Button so that it has an horizontal span = 2 and thus takes entire last row
		validateBtn = new Button(externalToolGroup, SWT.PUSH);
		validateBtn.setText("Validate external tool");
		validateBtn.addSelectionListener(widgetSelectedAdapter(e -> doValidateExternalTool()));
		GridData validateBtnGridData = new GridData();
		validateBtnGridData.horizontalAlignment = GridData.CENTER;
		validateBtnGridData.horizontalSpan = 2;
		validateBtn.setLayoutData(validateBtnGridData);
		
		enableSaveActionFieldEditors(isSaveActionEnabled());
	}

	protected File writeDocumentInTempFile() throws IOException {
		File tempFile;
		tempFile = File.createTempFile("eclipse-basheditor", ".tmp");

		// put the current contents in the temp file
		FileWriter fw = new FileWriter(tempFile);
		fw.write("function test() {\necho dummy;\n}\n");
		fw.close();

		return tempFile;
	}
	
	protected void doValidateExternalTool() {
		String externalToolString = saveActionExternalSourceFormatterArguments.getStringValue();

		File dummyTmpFile;
		try {
			dummyTmpFile = writeDocumentInTempFile();
		} catch (IOException e) {
			MessageDialog.openError(getShell(), "Validation Results", "Failed creating a temporary file for validation purposes.");
			return;
		}
		
		BashEditorFileProcessContext processContext = new BashEditorFileProcessContext(dummyTmpFile);

		// substitute in the external tool cmd line the special placeholders:
		String[] cmd_args = commandArrayBuilder.build(externalToolString, dummyTmpFile);
		if (commandArrayBuilder.getNumKeywordsReplaced() == 0) {
			MessageDialog.openError(getShell(), "Validation Results", "The command line is not including the special placeholder $filename.");
			return;
		}

		OutputHandler.STRING_OUTPUT.clearOutput();
		SimpleProcessExecutor executor = new SimpleProcessExecutor(OutputHandler.STRING_OUTPUT, true, true, 10);

		int exitCode;
		try {
			exitCode = executor.execute(processContext, processContext, processContext, cmd_args);
		} catch (IOException e) {
			MessageDialog.openError(getShell(), "Validation Results", "Validation of external tool command line '"
					+ externalToolString + "': " + e.getMessage());
			return;
		}
		
		if (exitCode == 0) {
			MessageDialog.openInformation(getShell(), "Validation Results", "Successfully validated external tool command line.");
		} else {
			MessageDialog.openError(getShell(), "Validation Results", "Validation of external tool command line '"
					+ externalToolString + "' failed with exit code " + exitCode + ": \" "
					+ OutputHandler.STRING_OUTPUT.getFullOutput() + "\"");
		}
	}

	public boolean isSaveActionEnabled() {
		return saveActionExternalSourceFormatterEnabled.getBooleanValue();
	}
	
	public void enableSaveActionFieldEditors(boolean enable) {
		validateBtn.setEnabled(enable);
		saveActionExternalSourceFormatterArguments.getTextControl(externalToolGroup).setEnabled(enable);
	}
	
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// if saveAction checkbock is unchecked, disable other controls to make it clear they are logically connected together:
		enableSaveActionFieldEditors(isSaveActionEnabled());
	}

	@Override
	protected void initialize() {
		super.initialize();
		enableSaveActionFieldEditors(isSaveActionEnabled());
	}
	
	
}