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

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import de.jcup.basheditor.script.BashFunction;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class BashExternalFunctionHyperlink implements IHyperlink {

    private IRegion region;
    private BashFunction function;
    private IFile file;

    public BashExternalFunctionHyperlink(IRegion region, BashFunction function, IFile file) {
        isNotNull(region, "Gradle hyperlink region may not be null!");
        isNotNull(function, "function may not be null!");
        isNotNull(file, "file may not be null!");
        this.region = region;
        this.function = function;
        this.file = file;
    }

    @Override
    public IRegion getHyperlinkRegion() {
        return region;
    }

    @Override
    public String getTypeLabel() {
        return "Open function";
    }

    @Override
    public String getHyperlinkText() {
        return "Opens external declaration of " + function.getName() + " in "+file.getName();
    }

    @Override
    public void open() {
        IEditorPart editor;
        try {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            editor = IDE.openEditor(page, file,BashEditor.EDITOR_ID);
            if (editor instanceof BashEditor) {
                BashEditor be = (BashEditor) editor;
                be.selectAndReveal(function.getPosition(), function.getLengthToNameEnd());

            }
        } catch (PartInitException e) {
            EclipseUtil.logError("Cannot open editor", e, BashEditorActivator.getDefault());
        }
    }

}
