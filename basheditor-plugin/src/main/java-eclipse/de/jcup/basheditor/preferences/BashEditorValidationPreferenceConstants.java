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

/**
 * Constant definitions for plug-in preferences
 */
public enum BashEditorValidationPreferenceConstants implements PreferenceIdentifiable, PreferenceLabeled{
	VALIDATE_BLOCK_STATEMENTS("validateCodeBlocks","Code blocks"), 
	
	VALIDATE_DO_STATEMENTS("validateDo","Do statements"), 
	
	VALIDATE_IF_STATEMENTS("validateIf","If statements"),
	
	VALIDATE_FUNCTION_STATEMENTS("validateIf","Functions"),
	
	;

	private String id;
	private String labelText;

	private BashEditorValidationPreferenceConstants(String id, String labelText) {
		this.id = id;
		this.labelText=labelText;
	}

	public String getLabelText() {
		return labelText;
	}
	
	public String getId() {
		return id;
	}

}
