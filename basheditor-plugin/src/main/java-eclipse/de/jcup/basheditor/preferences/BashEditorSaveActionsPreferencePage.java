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

import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_SAVE_ACTION_EXTERNAL_TOOL_COMMAND;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_SAVE_ACTION_EXTERNAL_TOOL_ENABLED;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_SAVE_ACTION_EXTERNAL_TOOL_ARGUMENTS;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
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

public class BashEditorSaveActionsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	private BooleanFieldEditor saveActionExternalSourceFormatterEnabled;
	private MyFileEditor saveActionExternalSourceFormatter;
	private StringFieldEditor saveActionExternalSourceFormatterArguments;
	
	// UI
	private Composite fileComposite;
	private Group externalToolGroup;
	
	// dummy class just to override the FileFieldEditor::getChangeControl() visibility:
	class MyFileEditor extends FileFieldEditor {
		public MyFileEditor(String name, String labelText, Composite parent) {
			super(name, labelText, parent);
		}

		@Override
		public Button getChangeControl(Composite parent) {
			return super.getChangeControl(parent);
		}
	}

	
	
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

		// NOTE: we create a Composite in which we place the FileFieldEditor() and set an horizontalSpan = 2, so will take the entire second row of grid 
		fileComposite = new Composite(externalToolGroup, SWT.NONE);
		fileComposite.setLayout(new GridLayout(1, false));
		GridData fileLayoutData = new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.GRAB_HORIZONTAL);
		fileLayoutData.horizontalSpan = 2;
		fileComposite.setLayoutData(fileLayoutData);
		saveActionExternalSourceFormatter = 
				new MyFileEditor(P_SAVE_ACTION_EXTERNAL_TOOL_COMMAND.getId(), "External tool", fileComposite);
		addField(saveActionExternalSourceFormatter);
		
		// NOTE: the StringFieldEditor() has an horizontal span = 2, so will take the entire third row of grid
		saveActionExternalSourceFormatterArguments = 
				new StringFieldEditor(P_SAVE_ACTION_EXTERNAL_TOOL_ARGUMENTS.getId(), "External tool command-line arguments:", 
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

		enableSaveActionFieldEditors(isSaveActionEnabled());
	}

	public boolean isSaveActionEnabled() {
		return saveActionExternalSourceFormatterEnabled.getBooleanValue();
	}
	
	public void enableSaveActionFieldEditors(boolean enable) {
		saveActionExternalSourceFormatter.getTextControl(fileComposite).setEnabled(enable);
		saveActionExternalSourceFormatter.getChangeControl(fileComposite).setEnabled(enable);
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