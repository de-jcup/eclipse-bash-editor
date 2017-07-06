/*
 * Copyright 2016 Albert Tregnaghi
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

import static org.eclipse.core.runtime.Assert.*;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class BashFileHyperlink implements IHyperlink {

	private IRegion region;
	private IFile gradleFile;
	private IFileStore fileStore;

	public BashFileHyperlink(IRegion region, IFile gradleFile) {
		isNotNull(region, "Gradle hyperlink region may not be null!");
		isNotNull(gradleFile, "Gradle file may not be null!");
		this.region = region;
		this.gradleFile = gradleFile;
	}

	public BashFileHyperlink(IRegion region, IFileStore fileStore) {
		isNotNull(region, "Gradle hyperlink region may not be null!");
		isNotNull(fileStore, "FileStore may not be null!");
		this.region = region;
		this.fileStore = fileStore;
	}

	@Override
	public IRegion getHyperlinkRegion() {
		return region;
	}

	@Override
	public String getTypeLabel() {
		return "gradle link";
	}

	@Override
	public String getHyperlinkText() {
		return null;
	}

	@Override
	public void open() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null) {
			return;
		}
		IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
		if (activeWorkbenchWindow == null) {
			return;
		}
		IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
		if (activePage == null) {
			return;
		}
		try {
			if (fileStore != null) {
				IDE.openEditorOnFileStore(activePage, fileStore);
				return;
			}
			if (gradleFile != null) {
				IDE.openEditor(activePage, gradleFile);
				return;
			}
		} catch (PartInitException e) {

		}
	}

}
