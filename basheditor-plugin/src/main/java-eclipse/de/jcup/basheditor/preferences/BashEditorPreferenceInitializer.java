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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.jcup.basheditor.BashEditorColorConstants;

/**
 * Class used to initialize default preference values.
 */
public class BashEditorPreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IPreferenceStore store = getPreferences().getPreferenceStore();
		
		/* bracket rendering configuration */
		store.setDefault(P_EDITOR_MATCHING_BRACKETS_ENABLED.getId(), true); // per default matching is enabled, but without the two other special parts
		store.setDefault(P_EDITOR_HIGHLIGHT_BRACKET_AT_CARET_LOCATION.getId(), false);
		store.setDefault(P_EDITOR_ENCLOSING_BRACKETS.getId(), false);
		store.setDefault(P_EDITOR_AUTO_CREATE_END_BRACKETSY.getId(), true);
		
		/* bracket color */
		getPreferences().setDefaultColor(P_EDITOR_MATCHING_BRACKETS_COLOR, BashEditorColorConstants.GRAY_JAVA);
		
		/* editor colors */
		getPreferences().setDefaultColor(COLOR_NORMAL_TEXT, BashEditorColorConstants.BLACK);

		getPreferences().setDefaultColor(COLOR_BASH_KEYWORD, BashEditorColorConstants.KEYWORD_DEFAULT_PURPLE);
		getPreferences().setDefaultColor(COLOR_NORMAL_STRING, BashEditorColorConstants.STRING_DEFAULT_BLUE);
		
		
		getPreferences().setDefaultColor(COLOR_GSTRING, BashEditorColorConstants.ROYALBLUE);
		getPreferences().setDefaultColor(COLOR_BSTRING, BashEditorColorConstants.CADET_BLUE);
		getPreferences().setDefaultColor(COLOR_COMMENT, BashEditorColorConstants.GREEN_JAVA);
		
		getPreferences().setDefaultColor(COLOR_INCLUDE_KEYWORD, BashEditorColorConstants.LINK_DEFAULT_BLUE);
		
		getPreferences().setDefaultColor(COLOR_BASH_COMMAND, BashEditorColorConstants.TASK_DEFAULT_RED);
		getPreferences().setDefaultColor(COLOR_KNOWN_VARIABLES, BashEditorColorConstants.DARK_GRAY);
		getPreferences().setDefaultColor(COLOR_LITERALS, BashEditorColorConstants.KEYWORD_DEFAULT_PURPLE);
	}
	
	
	
}
