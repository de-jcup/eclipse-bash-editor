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
package de.jcup.basheditor.debug.launch.config;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.jcup.basheditor.BashEditorActivator;
import de.jcup.basheditor.debug.BashDebugConstants;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class BashLaunchConfigurationMainTab extends AbstractLaunchConfigurationTab {

	private Text bashScriptText;
	private Text bashParameterText;
	private Button bashScriptSelectionButton;
	
	String configurationName = "";

	public void createControl(Composite parent) {
		Font font = parent.getFont();

		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		GridLayout topLayout = new GridLayout();
		topLayout.verticalSpacing = 0;
		topLayout.numColumns = 3;
		comp.setLayout(topLayout);
		comp.setFont(font);

		Label programLabel = new Label(comp, SWT.NONE);
		programLabel.setText("&Bash script:");
		GridData gd = new GridData(GridData.BEGINNING);
		programLabel.setLayoutData(gd);
		programLabel.setFont(font);

		bashScriptText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		bashScriptText.setLayoutData(gd);
		bashScriptText.setFont(font);
		bashScriptText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});

		bashScriptSelectionButton = createPushButton(comp, "&Browse...", null); 
		bashScriptSelectionButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				browseBashFiles();
			}
		});
		
		Label paramLabel = new Label(comp, SWT.NONE);
		paramLabel.setText("Parameters:");
		gd = new GridData(GridData.BEGINNING);
		paramLabel.setLayoutData(gd);
		paramLabel.setFont(font);
		
		bashParameterText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		bashParameterText.setLayoutData(gd);
		bashParameterText.setFont(font);
		bashParameterText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		getLaunchConfigurationDialog().setActiveTab(0);
	}
	
	protected void browseBashFiles() {
		BashSelectionDialog dialog = new BashSelectionDialog(getShell(), ResourcesPlugin.getWorkspace().getRoot(), IResource.FILE);
		dialog.setTitle("Bash script");
		dialog.setMessage("Select Bash script");
		if (dialog.open() == Window.OK) {
			Object[] files = dialog.getResult();
			IFile file = (IFile) files[0];
			bashScriptText.setText(file.getFullPath().toString());
		}
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			configurationName = configuration.getName();
			String program = configuration.getAttribute(BashDebugConstants.LAUNCH_ATTR_BASH_PROGRAM, (String) null);
			if (program == null) {
				program = "";
			}
			bashScriptText.setText(program);

			String params = configuration.getAttribute(BashDebugConstants.LAUNCH_ATTR_BASH_PARAMS, (String) null);
			if (params == null) {
				params = "";
			}
			bashParameterText.setText(params);
			
			setMessage(null);
			setErrorMessage(null);
			updateLaunchConfigurationDialog();
		} catch (CoreException e) {
			setErrorMessage(e.getMessage());
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		if (bashScriptText.getText() == null) {
			bashScriptText.setText("");
		}
		String program = bashScriptText.getText().trim();
		if (program.length() == 0) {
			program = null;
		}
		
		String params = bashParameterText.getText().trim();
		if (params.length() == 0) {
			params = null;
		}
		
		configuration.setAttribute(BashDebugConstants.LAUNCH_ATTR_BASH_PROGRAM, program);
		configuration.setAttribute(BashDebugConstants.LAUNCH_ATTR_BASH_PARAMS, params);
	}

	public String getName() {
		return "Main";
	}
	
	public Image getImage() {
		return EclipseUtil.getImage("icons/run_exc.gif",BashEditorActivator.getDefault());
	}


	public boolean isValid(ILaunchConfiguration launchConfig) {
		String text = bashScriptText.getText();
		if (text.length() > 0) {
			IPath path = new Path(text);
			if (EclipseUtil.getWorkspace().getRoot().findMember(path) == null) {
				setErrorMessage("Specified bash file does not exist");
				return false;
			}
		} else {
			setMessage("Specify a bash script");
			return false;
		}
		setMessage(null);
		setErrorMessage(null);
		return super.isValid(launchConfig);
	}
}
