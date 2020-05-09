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
package de.jcup.basheditor;

import org.eclipse.core.resources.IFile;

import de.jcup.eclipse.commons.tasktags.AbstractConfigurableTaskTagsSupportProvider;

public class BashTaskTagsSupportProvider extends AbstractConfigurableTaskTagsSupportProvider{
	BashFileWithoutExtensionPropertyTester tester;
			
	public BashTaskTagsSupportProvider(BashEditorActivator plugin) {
		super(plugin);
		tester= new BashFileWithoutExtensionPropertyTester();
	}

	@Override
	public boolean isLineCheckforTodoTaskNessary(String line, int lineNumber, String[] lines) {
		if (line==null){
			return false;
		}
		return line.startsWith("#");
	}

	@Override
	public String getTodoTaskMarkerId() {
		return "de.jcup.basheditor.script.task";
	}

	@Override
	public boolean isFileHandled(IFile file) {
		if (file==null){
			return false;
		}
		String fileExtension = file.getFileExtension();
		if (fileExtension==null || fileExtension.isEmpty()){
			return tester.isFirstLineBashSheBang(file);
		}
		if (fileExtension.contentEquals("sh")) {
			return true;
		}
		return false;
	}


}
