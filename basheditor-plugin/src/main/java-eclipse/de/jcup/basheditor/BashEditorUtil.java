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
package de.jcup.basheditor;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

import de.jcup.basheditor.preferences.BashEditorPreferences;
import de.jcup.basheditor.scriptmodel.BashError;
import de.jcup.basheditor.scriptmodel.ValidationResult;

public class BashEditorUtil {

	private static UnpersistedMarkerHelper scriptProblemMarkerHelper = new UnpersistedMarkerHelper(
			"de.jcup.basheditor.script.problem");

	public static BashEditorPreferences getPreferences() {
		return BashEditorPreferences.getInstance();
	}

	public static void logInfo(String info) {
		getLog().log(new Status(IStatus.INFO, BashEditorActivator.PLUGIN_ID, info));
	}

	public static void logWarning(String warning) {
		getLog().log(new Status(IStatus.WARNING, BashEditorActivator.PLUGIN_ID, warning));
	}

	public static void logError(String error, Throwable t) {
		getLog().log(new Status(IStatus.ERROR, BashEditorActivator.PLUGIN_ID, error, t));
	}

	public static void removeScriptErrors(IEditorPart editor) {
		if (editor == null) {
			return;
		}
		IEditorInput input = editor.getEditorInput();
		if (input == null) {
			return;
		}
		IResource editorResource = input.getAdapter(IResource.class);
		if (editorResource == null) {
			return;
		}
		scriptProblemMarkerHelper.removeMarkers(editorResource);
	}

	public static void addScriptError(IEditorPart editor, int line, BashError error) {
		if (editor == null) {
			return;
		}
		if (error == null) {
			return;
		}

		IEditorInput input = editor.getEditorInput();
		if (input == null) {
			return;
		}
		IResource editorResource = input.getAdapter(IResource.class);
		if (editorResource == null) {
			return;
		}
		try {
			scriptProblemMarkerHelper.createErrorMarker(editorResource, error.getMessage(), line, error.getStart(),
					+ error.getEnd());
		} catch (CoreException e) {
			logError("Was not able to add error markers", e);
		}

	}

	private static ILog getLog() {
		ILog log = BashEditorActivator.getDefault().getLog();
		return log;
	}

}
