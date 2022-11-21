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
package de.jcup.basheditor.handlers;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import de.jcup.basheditor.BashEditorActivator;
import de.jcup.basheditor.debug.launch.TerminalLaunchContext;
import de.jcup.basheditor.debug.launch.TerminalLaunchContext.RunMode;
import de.jcup.basheditor.debug.launch.TerminalLaunchContextBuilder;
import de.jcup.basheditor.debug.launch.TerminalLauncher;
import de.jcup.basheditor.preferences.BashEditorPreferences;
import de.jcup.eclipse.commons.EclipseResourceHelper;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class OpenPathInTerminalHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IResource resource = getSelectedResource();
        if (resource == null) {
            return null;
        }
        if (!resource.exists()) {
            return null;
        }
        File file;
        try {
            file = EclipseResourceHelper.DEFAULT.toFile(resource);
        } catch (CoreException e) {
            EclipseUtil.logError("Was not able to fetch as file:" + resource, e, BashEditorActivator.getDefault());
            return null;
        }
        if (!file.isDirectory()) {
            file = file.getParentFile();
        }
        if (file == null || !file.exists()) {
            return null;
        }
        TerminalLauncher launcher = new TerminalLauncher();
        BashEditorPreferences preferences = BashEditorPreferences.getInstance();

        String terminalCommand = preferences.getTerminalCommand();
        String starterCommand = preferences.getStarterCommand();
        String openInTerminalCommand = preferences.getJustOpenTerminalCommand();

        /* @formatter:off */
        TerminalLaunchContext context = TerminalLaunchContextBuilder.builder().
                terminalCommand(terminalCommand).
                starterCommand(starterCommand).
                file(file).
                workingDir(file).
                openTerminalCommand(openInTerminalCommand).
                runMode(RunMode.JUST_OPEN_TERMINAL).
                build();
        /* @formatter:on */
        launcher.execute(context);

        return null;
    }

    protected IResource getSelectedResource() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return null;
        }

        ISelection selection = window.getSelectionService().getSelection();
        if (!(selection instanceof IStructuredSelection)) {
            return null;
        }
        IStructuredSelection structuredSelection = (IStructuredSelection) selection;

        Object firstElement = structuredSelection.getFirstElement();
        if (!(firstElement instanceof IAdaptable)) {
            return null;
        }

        IResource file = (IResource) ((IAdaptable) firstElement).getAdapter(IResource.class);
        return file;
    }

}
