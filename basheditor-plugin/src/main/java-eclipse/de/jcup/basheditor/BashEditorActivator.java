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

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.jcup.basheditor.workspacemodel.SharedBashModel;
import de.jcup.basheditor.workspacemodel.SharedBashModelSupportProvider;
import de.jcup.eclipse.commons.PluginContextProvider;
import de.jcup.eclipse.commons.keyword.TooltipTextSupport;
import de.jcup.eclipse.commons.resource.EclipseResourceInputStreamProvider;

/**
 * The activator class controls the plug-in life cycle
 */
public class BashEditorActivator extends AbstractUIPlugin implements PluginContextProvider {

	// The plug-in COMMAND_ID
	public static final String PLUGIN_ID = "de.jcup.basheditor"; //$NON-NLS-1$

	// The shared instance
	private static BashEditorActivator plugin;
	private ColorManager colorManager;
	private SharedBashModelSupportProvider modelProvider;

	/**
	 * The constructor
	 */
	public BashEditorActivator() {
		colorManager = new ColorManager();
		TooltipTextSupport.setTooltipInputStreamProvider(new EclipseResourceInputStreamProvider(PLUGIN_ID));
		modelProvider= new SharedBashModelSupportProvider(this);
	}

	public ColorManager getColorManager() {
		return colorManager;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		modelProvider.getWorkspaceModelSupport().install();
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		colorManager.dispose();
		modelProvider.getWorkspaceModelSupport().uninstall();
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

}
