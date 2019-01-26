/*
 * Copyright 2018 Albert Tregnaghi
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
package de.jcup.basheditor.process;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

public class BashEditorFileProcessContext implements ProcessContext, ProcessConfiguration, EnvironmentProvider {

	private Map<String, String> envMap;
	private File editorFile;
	private CancelStateProvider cancelStateProvider;

	public BashEditorFileProcessContext(File editorFile) {
		envMap = new TreeMap<>();
		this.editorFile = editorFile;
	}

	@Override
	public Map<String, String> getEnvironment() {
		return envMap;
	}

	@Override
	public String getWorkingDirectory() {
		return editorFile.getParent();
	}

	@Override
	public CancelStateProvider getCancelStateProvider() {
		if (cancelStateProvider == null) {
			return CancelStateProvider.NEVER_CANCELED;
		}
		return cancelStateProvider;
	}

	public void setCancelStateProvider(CancelStateProvider cancelStateProvider) {
		this.cancelStateProvider = cancelStateProvider;
	}

}
