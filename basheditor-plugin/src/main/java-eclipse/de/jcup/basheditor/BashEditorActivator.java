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

import org.eclipse.core.commands.Command;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.jcup.basheditor.debug.BashDebugInfoProvider;
import de.jcup.basheditor.debug.DebugBashCodeToggleSupport;
import de.jcup.basheditor.debug.launch.OSUtil;
import de.jcup.basheditor.handlers.ToggleMarkOccurencesResourcesHandler;
import de.jcup.basheditor.preferences.BashEditorPreferences;
import de.jcup.basheditor.templates.BashEditorTemplateSupportConfig;
import de.jcup.basheditor.workspacemodel.SharedBashModel;
import de.jcup.basheditor.workspacemodel.SharedBashModelSupportProvider;
import de.jcup.eclipse.commons.PluginContextProvider;
import de.jcup.eclipse.commons.keyword.TooltipTextSupport;
import de.jcup.eclipse.commons.resource.EclipseResourceInputStreamProvider;
import de.jcup.eclipse.commons.templates.TemplateSupportProvider;
import de.jcup.eclipse.commons.ui.EclipseUtil;

/**
 * The activator class controls the plug-in life cycle
 */
public class BashEditorActivator extends AbstractUIPlugin implements PluginContextProvider, BashDebugInfoProvider {

    // The plug-in COMMAND_ID
    public static final String PLUGIN_ID = "de.jcup.basheditor"; //$NON-NLS-1$

    // The shared instance
    private static BashEditorActivator plugin;
    private ColorManager colorManager;
    private SharedBashModelSupportProvider modelProvider;
    private TemplateSupportProvider templateSupportProvider;

    private BashTaskTagsSupportProvider taskSupportProvider;

    private DebugBashCodeToggleSupport toggleSupport;

    private boolean markOccurrences;

    /**
     * The constructor
     */
    public BashEditorActivator() {
        colorManager = new ColorManager();
        TooltipTextSupport.setTooltipInputStreamProvider(new EclipseResourceInputStreamProvider(PLUGIN_ID));
        modelProvider = new SharedBashModelSupportProvider(this);
        templateSupportProvider = new TemplateSupportProvider(new BashEditorTemplateSupportConfig(), this);
        taskSupportProvider = new BashTaskTagsSupportProvider(this);
    }

    public BashTaskTagsSupportProvider getTaskSupportProvider() {
        return taskSupportProvider;
    }

    public ColorManager getColorManager() {
        return colorManager;
    }

    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;

        
        ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);  
        Command command = commandService.getCommand("basheditor.editor.commands.togglemarkoccurrences");  
        
        /* initialize with persisted state */
        markOccurrences = ToggleMarkOccurencesResourcesHandler.getCurrentUIToggleMarkOccurrences(command);
        
        toggleSupport = new DebugBashCodeToggleSupport(BashEditorActivator.getDefault());

        InstallJob job = new InstallJob();
        job.schedule();
    }

    private class InstallJob extends Job {

        public InstallJob() {
            super("Install bash editor model and task support");
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            modelProvider.getWorkspaceModelSupport().install();
            taskSupportProvider.getTodoTaskSupport().install();
            return Status.OK_STATUS;
        }

    }

    public void stop(BundleContext context) throws Exception {
        plugin = null;
        colorManager.dispose();
        modelProvider.getWorkspaceModelSupport().uninstall();
        taskSupportProvider.getTodoTaskSupport().uninstall();
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static BashEditorActivator getDefault() {
        return plugin;
    }

    @Override
    public AbstractUIPlugin getActivator() {
        return this;
    }

    @Override
    public String getPluginID() {
        return PLUGIN_ID;
    }

    public SharedBashModel getModel() {
        return modelProvider.getWorkspaceModelSupport().getModel();
    }

    public void rebuildSharedBashModel() {
        modelProvider.getWorkspaceModelSupport().fullRebuild();
    }

    @Override
    public String getSystemUserHomePath() {
        String userHome = System.getProperty("user.home");
        return userHome;
    }

    @Override
    public String getDefaultScriptPathToUserHome() {
        return OSUtil.toUnixPath(getSystemUserHomePath());
    }

    @Override
    public String getResultingScriptPathToUserHome() {
        String customPath = BashEditorPreferences.getInstance().getCustomScriptPathToUserHome();
        if (customPath == null || customPath.trim().isEmpty()) {
            return getDefaultScriptPathToUserHome();
        }
        return customPath.trim();
    }

    public TemplateSupportProvider getTemplateSupportProvider() {
        return templateSupportProvider;
    }

    public DebugBashCodeToggleSupport getToggleSupport() {
        return toggleSupport;
    }

    /**
     * 
     * @return new state;
     */
    public boolean toggleMarkOccurrences() {
        if (markOccurrences) {
            /* inform all Bash editors about color changes */
            IWorkbenchPage activePage = EclipseUtil.getActivePage();
            if (activePage == null) {
                return markOccurrences; // keep as is
            }
            IEditorReference[] references = activePage.getEditorReferences();
            for (IEditorReference ref : references) {
                IEditorPart editor = ref.getEditor(false);
                if (editor == null) {
                    continue;
                }
                if (!(editor instanceof BashEditor)) {
                    continue;
                }
                BashEditor geditor = (BashEditor) editor;
                geditor.removeOccurrenceMarkers();
            }
        }
        this.markOccurrences = !markOccurrences;
        return markOccurrences;
    }
    
    public boolean isMarkOccurrencesActivated() {
        return markOccurrences;
    }

}
