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

import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_SAVE_ACTION_EXTERNAL_TOOL_ENABLED;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_SAVE_ACTION_EXTERNAL_TOOL_COMMAND;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.basheditor.BashEditorUtil;

public class BashEditorSaveActionsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	private BooleanFieldEditor saveActionExternalSourceFormatterEnabled;
	private StringFieldEditor saveActionFieldEditor;
	
	public BashEditorSaveActionsPreferencePage() {
		super(GRID);
		setPreferenceStore(BashEditorUtil.getPreferences().getPreferenceStore());
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected void createFieldEditors() {
		
		Composite appearanceComposite = new Composite(getFieldEditorParent(), SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		appearanceComposite.setLayout(layout);
		
		/* --------------------- */
		/* --   Save action   -- */
		/* --------------------- */

		GridData externalToolsGroupLayoutData = new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.GRAB_HORIZONTAL);
		externalToolsGroupLayoutData.horizontalSpan = 2;
		externalToolsGroupLayoutData.widthHint = 400;

		Group externalToolGroup = new Group(appearanceComposite, SWT.NONE);
		externalToolGroup.setText("External tool");
		externalToolGroup.setLayout(new GridLayout());
		externalToolGroup.setLayoutData(externalToolsGroupLayoutData);
		
		saveActionExternalSourceFormatterEnabled = new BooleanFieldEditor(P_SAVE_ACTION_EXTERNAL_TOOL_ENABLED.getId(),
				"Execute command on save", externalToolGroup);
		saveActionExternalSourceFormatterEnabled.getDescriptionControl(externalToolGroup).setToolTipText("External program to run every time a script file is saved; its output will replace current document.");
		addField(saveActionExternalSourceFormatterEnabled);

		saveActionFieldEditor = new StringFieldEditor(P_SAVE_ACTION_EXTERNAL_TOOL_COMMAND.getId(), "Command:", 
				StringFieldEditor.UNLIMITED, StringFieldEditor.VALIDATE_ON_FOCUS_LOST, externalToolGroup);
		addField(saveActionFieldEditor);

		Composite labelGroup = new Composite(externalToolGroup, SWT.NONE);
		labelGroup.setLayout(new GridLayout());
		GridData labelGroupLayoutData = new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.GRAB_HORIZONTAL);
		labelGroupLayoutData.horizontalSpan = 2;
		labelGroup.setLayoutData(labelGroupLayoutData);
		createNoteComposite(labelGroup.getFont(), labelGroup, 
			"Note:", "Special $filename placeholder can be used to indicate currently\n"
					 + "opened file. External tool output will replace current document.");
	}

}