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

import static de.jcup.basheditor.preferences.BashEditorSyntaxColorPreferenceConstants.COLOR_BASH_COMMAND;
import static de.jcup.basheditor.preferences.BashEditorSyntaxColorPreferenceConstants.COLOR_BASH_KEYWORD;
import static de.jcup.basheditor.preferences.BashEditorSyntaxColorPreferenceConstants.COLOR_BSTRING;
import static de.jcup.basheditor.preferences.BashEditorSyntaxColorPreferenceConstants.COLOR_COMMENT;
import static de.jcup.basheditor.preferences.BashEditorSyntaxColorPreferenceConstants.COLOR_DOUBLE_STRINGS;
import static de.jcup.basheditor.preferences.BashEditorSyntaxColorPreferenceConstants.COLOR_HEREDOCS;
import static de.jcup.basheditor.preferences.BashEditorSyntaxColorPreferenceConstants.COLOR_HERESTRINGS;
import static de.jcup.basheditor.preferences.BashEditorSyntaxColorPreferenceConstants.COLOR_INCLUDE_KEYWORD;
import static de.jcup.basheditor.preferences.BashEditorSyntaxColorPreferenceConstants.COLOR_NORMAL_TEXT;
import static de.jcup.basheditor.preferences.BashEditorSyntaxColorPreferenceConstants.COLOR_PARAMETERS;
import static de.jcup.basheditor.preferences.BashEditorSyntaxColorPreferenceConstants.COLOR_SINGLE_STRINGS;
import static de.jcup.basheditor.preferences.BashEditorSyntaxColorPreferenceConstants.COLOR_VARIABLES;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.basheditor.BashEditorColorConstants;
import de.jcup.basheditor.BashEditorUtil;

public class BashEditorSyntaxColorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public BashEditorSyntaxColorPreferencePage() {
		setPreferenceStore(BashEditorUtil.getPreferences().getPreferenceStore());
	}
	
	@Override
	public void init(IWorkbench workbench) {
		
	}

	@Override
	protected void createFieldEditors() {
		Composite parent = getFieldEditorParent();
		Map<BashEditorSyntaxColorPreferenceConstants, ColorFieldEditor> editorMap = new HashMap<BashEditorSyntaxColorPreferenceConstants, ColorFieldEditor>();
		for (BashEditorSyntaxColorPreferenceConstants colorIdentifier: BashEditorSyntaxColorPreferenceConstants.values()){
			ColorFieldEditor editor = new ColorFieldEditor(colorIdentifier.getId(), colorIdentifier.getLabelText(), parent);
			editorMap.put(colorIdentifier, editor);
			addField(editor);
		}
		Button restoreDarkThemeColorsButton= new Button(parent,  SWT.PUSH);
		restoreDarkThemeColorsButton.setText("Restore Defaults for Dark Theme");
		restoreDarkThemeColorsButton.setToolTipText("Same as 'Restore Defaults' but for dark themes.\n Editor makes just a suggestion, you still have to apply or cancel the settings.");
		restoreDarkThemeColorsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				/* editor colors */
				changeColor(editorMap, COLOR_NORMAL_TEXT, BashEditorColorConstants.GRAY_JAVA);
				changeColor(editorMap, COLOR_BASH_KEYWORD, BashEditorColorConstants.MIDDLE_GREEN);
				
				changeColor(editorMap, COLOR_SINGLE_STRINGS, BashEditorColorConstants.MIDDLE_GRAY);
				changeColor(editorMap, COLOR_DOUBLE_STRINGS, BashEditorColorConstants.MIDDLE_ORANGE);
				changeColor(editorMap, COLOR_BSTRING, BashEditorColorConstants.DARK_PINK);
				
				changeColor(editorMap, COLOR_COMMENT, BashEditorColorConstants.GREEN_JAVA);
				changeColor(editorMap, COLOR_INCLUDE_KEYWORD, BashEditorColorConstants.MIDDLE_BROWN);
				changeColor(editorMap, COLOR_BASH_COMMAND, BashEditorColorConstants.TASK_CYAN);
				changeColor(editorMap, COLOR_VARIABLES, BashEditorColorConstants.DARK_MIDDLE_GREEN);
				changeColor(editorMap, COLOR_PARAMETERS, BashEditorColorConstants.BRIGHT_CYAN);
				changeColor(editorMap, COLOR_HEREDOCS, BashEditorColorConstants.DARK_THEME_HEREDOC);
				changeColor(editorMap, COLOR_HERESTRINGS, BashEditorColorConstants.DARK_THEME_HERESTRING);
				
			}

			private void changeColor(Map<BashEditorSyntaxColorPreferenceConstants, ColorFieldEditor> editorMap,
					BashEditorSyntaxColorPreferenceConstants colorId, RGB rgb) {
				editorMap.get(colorId).getColorSelector().setColorValue(rgb);
			}
			
		});
			
		
	}
	
}