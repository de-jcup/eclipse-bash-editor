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

import static de.jcup.basheditor.BashEditorColorConstants.BLACK;
import static de.jcup.basheditor.BashEditorColorConstants.CADET_BLUE;
import static de.jcup.basheditor.BashEditorColorConstants.DARK_BLUE;
import static de.jcup.basheditor.BashEditorColorConstants.DARK_GRAY;
import static de.jcup.basheditor.BashEditorColorConstants.GRAY;
import static de.jcup.basheditor.BashEditorColorConstants.GRAY_JAVA;
import static de.jcup.basheditor.BashEditorColorConstants.GREEN_JAVA;
import static de.jcup.basheditor.BashEditorColorConstants.KEYWORD_DEFAULT_PURPLE;
import static de.jcup.basheditor.BashEditorColorConstants.LIGHT_THEME_HERESTRING;
import static de.jcup.basheditor.BashEditorColorConstants.LINK_DEFAULT_BLUE;
import static de.jcup.basheditor.BashEditorColorConstants.ROYALBLUE;
import static de.jcup.basheditor.BashEditorColorConstants.STRING_DEFAULT_BLUE;
import static de.jcup.basheditor.BashEditorColorConstants.TASK_DEFAULT_RED;
import static de.jcup.basheditor.BashEditorUtil.getPreferences;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_CODE_ASSIST_ADD_KEYWORDS;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_CODE_ASSIST_ADD_SIMPLEWORDS;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_EDITOR_AUTO_CREATE_END_BRACKETSY;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_EDITOR_ENCLOSING_BRACKETS;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_EDITOR_HIGHLIGHT_BRACKET_AT_CARET_LOCATION;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_EDITOR_MATCHING_BRACKETS_COLOR;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_EDITOR_MATCHING_BRACKETS_ENABLED;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_LINK_OUTLINE_WITH_EDITOR;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_SAVE_ACTION_EXTERNAL_TOOL_COMMAND;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_SAVE_ACTION_EXTERNAL_TOOL_ENABLED;
import static de.jcup.basheditor.preferences.BashEditorPreferenceConstants.P_TOOLTIPS_ENABLED;
import static de.jcup.basheditor.preferences.BashEditorSyntaxColorPreferenceConstants.COLOR_BASH_COMMAND;
import static de.jcup.basheditor.preferences.BashEditorSyntaxColorPreferenceConstants.COLOR_BASH_KEYWORD;
import static de.jcup.basheditor.preferences.BashEditorSyntaxColorPreferenceConstants.COLOR_BSTRING;
import static de.jcup.basheditor.preferences.BashEditorSyntaxColorPreferenceConstants.COLOR_COMMENT;
import static de.jcup.basheditor.preferences.BashEditorSyntaxColorPreferenceConstants.COLOR_GSTRING;
import static de.jcup.basheditor.preferences.BashEditorSyntaxColorPreferenceConstants.COLOR_HEREDOCS;
import static de.jcup.basheditor.preferences.BashEditorSyntaxColorPreferenceConstants.COLOR_HERESTRINGS;
import static de.jcup.basheditor.preferences.BashEditorSyntaxColorPreferenceConstants.COLOR_INCLUDE_KEYWORD;
import static de.jcup.basheditor.preferences.BashEditorSyntaxColorPreferenceConstants.COLOR_KNOWN_VARIABLES;
import static de.jcup.basheditor.preferences.BashEditorSyntaxColorPreferenceConstants.COLOR_NORMAL_STRING;
import static de.jcup.basheditor.preferences.BashEditorSyntaxColorPreferenceConstants.COLOR_NORMAL_TEXT;
import static de.jcup.basheditor.preferences.BashEditorSyntaxColorPreferenceConstants.COLOR_PARAMETERS;
import static de.jcup.basheditor.preferences.BashEditorValidationPreferenceConstants.VALIDATE_BLOCK_STATEMENTS;
import static de.jcup.basheditor.preferences.BashEditorValidationPreferenceConstants.VALIDATE_DO_STATEMENTS;
import static de.jcup.basheditor.preferences.BashEditorValidationPreferenceConstants.VALIDATE_ERROR_LEVEL;
import static de.jcup.basheditor.preferences.BashEditorValidationPreferenceConstants.VALIDATE_FUNCTION_STATEMENTS;
import static de.jcup.basheditor.preferences.BashEditorValidationPreferenceConstants.VALIDATE_IF_STATEMENTS;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.jcup.basheditor.script.parser.validator.BashEditorValidationErrorLevel;

/**
 * Class used to initialize default preference values.
 */
public class BashEditorPreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		BashEditorPreferences preferences = getPreferences();
		IPreferenceStore store = preferences.getPreferenceStore();

		/* Outline */
		store.setDefault(P_LINK_OUTLINE_WITH_EDITOR.getId(), true);

		/* ++++++++++++ */
		/* + Brackets + */
		/* ++++++++++++ */
		/* bracket rendering configuration */
		store.setDefault(P_EDITOR_MATCHING_BRACKETS_ENABLED.getId(), true);
		store.setDefault(P_EDITOR_HIGHLIGHT_BRACKET_AT_CARET_LOCATION.getId(), false);
		store.setDefault(P_EDITOR_ENCLOSING_BRACKETS.getId(), false);
		store.setDefault(P_EDITOR_AUTO_CREATE_END_BRACKETSY.getId(), true);

		/* bracket color */
		preferences.setDefaultColor(P_EDITOR_MATCHING_BRACKETS_COLOR, GRAY_JAVA);

		store.setDefault(P_CODE_ASSIST_ADD_KEYWORDS.getId(), true);
		store.setDefault(P_CODE_ASSIST_ADD_SIMPLEWORDS.getId(), true);
		
		/* +++++++++++++++++++ */
		/* + Tooltips        + */
		/* +++++++++++++++++++ */
		store.setDefault(P_TOOLTIPS_ENABLED.getId(), true);

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
		preferences.setDefaultColor(COLOR_HEREDOCS, GRAY);
		preferences.setDefaultColor(COLOR_HERESTRINGS, LIGHT_THEME_HERESTRING);

		/* ++++++++++++++ */
		/* + Validation + */
		/* ++++++++++++++ */
		store.setDefault(VALIDATE_BLOCK_STATEMENTS.getId(), true);
		store.setDefault(VALIDATE_DO_STATEMENTS.getId(), true);
		store.setDefault(VALIDATE_IF_STATEMENTS.getId(), true);
		store.setDefault(VALIDATE_FUNCTION_STATEMENTS.getId(), true);

		store.setDefault(VALIDATE_ERROR_LEVEL.getId(), BashEditorValidationErrorLevel.ERROR.getId());
		
		/* ++++++++++++++ */
		/* + Save action + */
		/* ++++++++++++++ */
		store.setDefault(P_SAVE_ACTION_EXTERNAL_TOOL_ENABLED.getId(),false);
		// we use "beautysh -f $filename" as default - see https://github.com/bemeurer/beautysh for installation
		store.setDefault(P_SAVE_ACTION_EXTERNAL_TOOL_COMMAND.getId(),"beautysh -f $filename"); 

	}

}
