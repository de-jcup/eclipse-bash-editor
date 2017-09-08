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
 

import static de.jcup.basheditor.BashEditorUtil.*;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.*;
import static de.jcup.basheditor.preferences.BashEditorSyntaxColorPreferenceConstants.*;
import static de.jcup.basheditor.preferences.BashEditorValidationPreferenceConstants.*;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import static de.jcup.basheditor.BashEditorColorConstants.*;

/**
 * Class used to initialize default preference values.
 */
public class BashEditorPreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		BashEditorPreferences preferences = getPreferences();
		IPreferenceStore store = preferences.getPreferenceStore();
		
		/* ++++++++++++ */
		/* + Brackets + */
		/* ++++++++++++ */
		/* bracket rendering configuration */
		store.setDefault(P_EDITOR_MATCHING_BRACKETS_ENABLED.getId(), true); // per default matching is enabled, but without the two other special parts
		store.setDefault(P_EDITOR_HIGHLIGHT_BRACKET_AT_CARET_LOCATION.getId(), false);
		store.setDefault(P_EDITOR_ENCLOSING_BRACKETS.getId(), false);
		store.setDefault(P_EDITOR_AUTO_CREATE_END_BRACKETSY.getId(), true);
		
		/* bracket color */
		preferences.setDefaultColor(P_EDITOR_MATCHING_BRACKETS_COLOR, GRAY_JAVA);
		
		/* +++++++++++++++++ */
		/* + Editor Colors + */
		/* +++++++++++++++++ */
		preferences.setDefaultColor(COLOR_NORMAL_TEXT, BLACK);

		preferences.setDefaultColor(COLOR_BASH_KEYWORD, KEYWORD_DEFAULT_PURPLE);
		preferences.setDefaultColor(COLOR_NORMAL_STRING, STRING_DEFAULT_BLUE);
		
		
		preferences.setDefaultColor(COLOR_GSTRING, ROYALBLUE);
		preferences.setDefaultColor(COLOR_BSTRING, CADET_BLUE);
		preferences.setDefaultColor(COLOR_COMMENT, GREEN_JAVA);
		
		preferences.setDefaultColor(COLOR_INCLUDE_KEYWORD, LINK_DEFAULT_BLUE);
		
		preferences.setDefaultColor(COLOR_BASH_COMMAND, TASK_DEFAULT_RED);
		preferences.setDefaultColor(COLOR_KNOWN_VARIABLES, DARK_GRAY);
		preferences.setDefaultColor(COLOR_PARAMETERS, DARK_BLUE);
		
		/* ++++++++++++++ */
		/* + Validation + */
		/* ++++++++++++++ */
		store.setDefault(VALIDATE_BLOCK_STATEMENTS.getId(),true);
		store.setDefault(VALIDATE_DO_STATEMENTS.getId(),true);
		store.setDefault(VALIDATE_IF_STATEMENTS.getId(),true);
		store.setDefault(VALIDATE_FUNCTION_STATEMENTS.getId(),true);
	}
	
	
	
}
