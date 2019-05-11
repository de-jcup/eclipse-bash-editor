/*
 * Copyright 2019 Albert Tregnaghi
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
package de.jcup.basheditor.workspacemodel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import de.jcup.basheditor.EclipseDeveloperSettings;
import de.jcup.basheditor.script.BashScriptModel;
import de.jcup.basheditor.script.BashScriptModelBuilder;
import de.jcup.basheditor.script.BashScriptModelException;
import de.jcup.eclipse.commons.EclipseResourceHelper;
import de.jcup.eclipse.commons.PluginContextProvider;
import de.jcup.eclipse.commons.ui.EclipseUtil;
import de.jcup.eclipse.commons.workspacemodel.AbstractModelBuilder;
import de.jcup.eclipse.commons.workspacemodel.ModelUpdateAction;

/**
 * This is an example for a project model builder. This builder collects in
 * every file with ".testcase" file extensions the lines where wellknown
 * keywords reside.<br>
 * <br>
 * Inside it's just a simple map .
 * 
 * @author albert
 *
 */
public class SharedBashModelBuilder extends AbstractModelBuilder<SharedBashModel> {

    BashScriptModelBuilder scriptModelBuilder;
    private PluginContextProvider provider;

    SharedBashModelBuilder(PluginContextProvider provider) {
        scriptModelBuilder = new BashScriptModelBuilder();

        this.provider = provider;
    }

    @Override
    public SharedBashModel create() {
        return new SharedBashModel();
    }

    @Override
    public void updateImpl(SharedBashModel model, ModelUpdateAction action) {
        IResource resource = action.getResource();
        if (! (resource instanceof IFile)) {
            return;
        }
        IFile file = (IFile) resource;
        switch (action.getType()) {
        case ADD:
            String loadedscript = loadScript(file);
            if (EclipseDeveloperSettings.SHOW_SHAREDMODEL_TRACEMODE) {
                System.out.println("added script:"+file.getName());
            }
            try {
                /*
                 * the builder support already has checked that this file is a bash file - means
                 * a bash shebang was found. So normally ... .there should be no exceptions.
                 */
                BashScriptModel scriptModel = scriptModelBuilder.build(loadedscript);
                model.update(file, scriptModel);
            } catch (BashScriptModelException e) {
                EclipseUtil.logError("Was not able build script", e, provider);
            }
            break;
        case DELETE:
            model.remove(file);
            if (EclipseDeveloperSettings.SHOW_SHAREDMODEL_TRACEMODE) {
                System.out.println("removed script:"+file.getName());
            }
            break;
        default:
            break;

        }

    }

    private String loadScript(IFile file) {
        if (file == null) {
            return "";
        }
        String name = file.getName();
        try {
            if (EclipseDeveloperSettings.SHOW_SHAREDMODEL_TRACEMODE) {
                System.out.println("Loading script:"+file);
            }
            return EclipseResourceHelper.DEFAULT.readAsText(file, provider, name);
        } catch (CoreException e) {
            EclipseUtil.logError("Was not able to load script:" + name, e, provider);
            return null;
        }
    }

}
