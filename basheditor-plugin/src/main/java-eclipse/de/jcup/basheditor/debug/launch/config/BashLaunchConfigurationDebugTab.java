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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
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
import org.eclipse.swt.widgets.Spinner;

import de.jcup.basheditor.BashEditorActivator;
import de.jcup.basheditor.debug.BashDebugConstants;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class BashLaunchConfigurationDebugTab extends AbstractLaunchConfigurationTab {

	private Spinner portSpinner;
	private Label portLabel;
	private Button stopOnStartupButton;
	
	public void createControl(Composite parent) {
		Font font = parent.getFont();

		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		GridLayout topLayout = new GridLayout();
		topLayout.verticalSpacing = 0;
		topLayout.numColumns = 3;
		comp.setLayout(topLayout);
		comp.setFont(font);

		GridData gd = new GridData(GridData.BEGINNING);

		portLabel = new Label(comp, SWT.NONE);
		portLabel.setText("Debugger port:");
		gd = new GridData(GridData.BEGINNING);
		portLabel.setLayoutData(gd);
		portLabel.setFont(font);

		portSpinner = new Spinner(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		portSpinner.setLayoutData(gd);
		portSpinner.setFont(font);
		portSpinner.setMaximum(0xFFFF);
		portSpinner.setMinimum(0x01);
		
		portSpinner.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		new Label(comp, SWT.NONE);
		createSeparator(comp, 3);
		stopOnStartupButton = new Button(comp, SWT.CHECK);
		stopOnStartupButton.setText("Stop on startup");
		
		gd = new GridData(GridData.FILL);
		stopOnStartupButton.setLayoutData(gd);
		stopOnStartupButton.setFont(font);
		stopOnStartupButton.setSelection(false);
		stopOnStartupButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		new Label(comp, SWT.NONE);
		new Label(comp, SWT.NONE);
		getLaunchConfigurationDialog().setActiveTab(0);
	}
	
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			int port = configuration.getAttribute(BashDebugConstants.LAUNCH_ATTR_SOCKET_PORT, BashDebugConstants.DEFAULT_DEBUG_PORT);
			portSpinner.setSelection(port);
			
			stopOnStartupButton.setSelection(configuration.getAttribute(BashDebugConstants.LAUNCH_ATTR_STOP_ON_STARTUP, true));

			setMessage(null);
			setErrorMessage(null);
			updateLaunchConfigurationDialog();
		} catch (CoreException e) {
			setErrorMessage(e.getMessage());
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		int port = portSpinner.getSelection();
		configuration.setAttribute(BashDebugConstants.LAUNCH_ATTR_SOCKET_PORT, port);
		configuration.setAttribute(BashDebugConstants.LAUNCH_ATTR_STOP_ON_STARTUP, stopOnStartupButton.getSelection());
	}

	public String getName() {
		return "Debug";
	}
	
	public Image getImage() {
		return EclipseUtil.getImage("icons/debug_exc.gif",BashEditorActivator.getDefault());
	}


	public boolean isValid(ILaunchConfiguration launchConfig) {
		return super.isValid(launchConfig);
	}
}
