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

import static org.eclipse.core.runtime.Assert.isNotNull;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import de.jcup.eclipse.commons.ui.EclipseUtil;

public class BashExternalFileHyperlink implements IHyperlink {

    private IFile file;
	private IRegion region;

    public BashExternalFileHyperlink(IRegion region, IFile file) {
        isNotNull(file, "file may not be null!");
        this.file = file;
        this.region = region;
    }

    @Override
    public IRegion getHyperlinkRegion() {
        return region;
    }

    @Override
    public String getTypeLabel() {
        return "Open file";
    }

    @Override
    public String getHyperlinkText() {
        return "Opens external file:"+file.getName();
    }

    @Override
    public void open() {
        try {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            IDE.openEditor(page, file);
        } catch (PartInitException e) {
            EclipseUtil.logError("Cannot open editor", e, BashEditorActivator.getDefault());
        }
    }

}
