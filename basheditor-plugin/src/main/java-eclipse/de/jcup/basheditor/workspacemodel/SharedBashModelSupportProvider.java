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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.eclipse.core.resources.IFile;

import de.jcup.basheditor.BashFileExtensionMatcher;
import de.jcup.basheditor.EclipseDeveloperSettings;
import de.jcup.basheditor.LineIsBashSheBangValidator;
import de.jcup.basheditor.preferences.BashEditorPreferences;
import de.jcup.eclipse.commons.PluginContextProvider;
import de.jcup.eclipse.commons.ui.EclipseUtil;
import de.jcup.eclipse.commons.workspacemodel.AbstractConfigurableModelBuilderSupportProvider;
import de.jcup.eclipse.commons.workspacemodel.ModelBuilder;

public class SharedBashModelSupportProvider extends AbstractConfigurableModelBuilderSupportProvider<SharedBashModel> {

    private LineIsBashSheBangValidator sheBangValidator;
    private BashFileExtensionMatcher matcher = new BashFileExtensionMatcher();

    public SharedBashModelSupportProvider(PluginContextProvider provider) {
        super(provider);
        sheBangValidator = new LineIsBashSheBangValidator();
    }

    @Override
    public boolean isFileHandled(IFile file) {
        URI location = file.getRawLocationURI();
        if (location!=null) {
            String asciiLocation = location.toASCIIString();
            boolean skip=false;
            if (asciiLocation.indexOf("/.git/")!=-1) {
                /* shortcut - we do not handle any GIT content */
                skip=true;
            }
            if (skip) {
                if (EclipseDeveloperSettings.SHOW_SHAREDMODEL_TRACEMODE ) {
                    System.out.println("Skipping location:"+asciiLocation);
                }
                return false;
            }
            
        }
        String fileExtension = file.getFileExtension();
        if (fileExtension == null || fileExtension.isEmpty()) {
            if (! file.exists()) {
                /* we cannot detect any longer if the file was binary or not, but 
                 * being not existing any longer we can assume that this was a delete.
                 * So we just accept this
                 */
                return true;
            }
            /*
             * hmm.. could be a binary as well. So we just read the first two bytes. If it is
             * valid script it will start with "#!" This is always a convention to identify
             * scripts
             */
            try (InputStream stream = file.getContents()) {
                boolean withScriptSignature= checkStreamStartsWithScriptSignature(stream);
                if (!withScriptSignature && EclipseDeveloperSettings.SHOW_SHAREDMODEL_TRACEMODE ) {
                    System.out.println("Not with script signature:"+file);
                }
                return withScriptSignature;
            } catch (Exception e) {
                EclipseUtil.logError("Was not able to read file " + file, e, getPluginContextProvider());
                return false;
            }
        } else if (matcher.isMatching(fileExtension,false)) {
            /* always accepted */
            return true;
        }
        return false;
    }

    private boolean checkStreamStartsWithScriptSignature(InputStream stream) throws IOException {
        int c1 = stream.read();
        if (c1 != '#') {
            return false;
        }
        int c2 = stream.read();
        if (c2 != '!') {
            return false;
        }
        return true;
    }

    @Override
    public String getModelName() {
        return "Shared bash model";
    }

    @Override
    public boolean isLineCheckforModelNessary(String line, int lineNumber, String[] lines) {
        if (lineNumber > 1) {
            return false;
        }
        /* only when the file is a valid bash script we add it to shared model */
        return sheBangValidator.isValid(line);
    }

    @Override
    public int getAmountOfLinesToCheck() {
        return 1;// we check only the first line
    }

    @Override
    public ModelBuilder<SharedBashModel> createBuilder() {
        return new SharedBashModelBuilder(getPluginContextProvider());
    }

    @Override
    public boolean isModelBuilderSupportEnabled() {
        return BashEditorPreferences.getInstance().isSharedModelBuildEnabled();
    }

}
