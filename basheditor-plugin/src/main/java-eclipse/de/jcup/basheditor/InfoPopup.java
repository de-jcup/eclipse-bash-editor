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
package de.jcup.basheditor;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.LayoutConstants;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class InfoPopup extends TitleAreaDialog {


	private Image wizban;

	private String title;

	private String titleMessage;
	
	private String linkToPreferencesText;
	private String linkToPreferencesId;
	private String subMessage;

	private ImageDescriptor wizbanDescriptor;

	public InfoPopup(Shell shell, String title, String titleMessage, ImageDescriptor wizbanDescriptor) {
		super(shell);
		this.title=title;
		this.titleMessage=titleMessage;
		this.wizbanDescriptor = wizbanDescriptor;
		setHelpAvailable(false);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK only
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}
	
	public void setSubMessage(String subMessage) {
		this.subMessage = subMessage;
	}
	
	public void setLinkToPreferencesId(String linkToPreferencesId) {
		this.linkToPreferencesId = linkToPreferencesId;
	}
	public void setLinkToPreferencesText(String linkToPreferencesText) {
		this.linkToPreferencesText = linkToPreferencesText;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(title);
	}

	@Override
	public Control createDialogArea(Composite parent) {
		setTitle(title);
		setMessage(titleMessage);
		if (wizbanDescriptor!=null) {
			wizban=wizbanDescriptor.createImage();
			setTitleImage(wizban);
		}

		Composite res = new Composite(parent, SWT.NONE);
		GridData resGridData = GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, SWT.DEFAULT).create();
		res.setLayoutData(resGridData);
		GridLayoutFactory.fillDefaults().margins(LayoutConstants.getMargins()).equalWidth(false).applyTo(res);

		GridData labelGridData =null;
		if (subMessage!=null) {
			Label label = new Label(res, SWT.WRAP);
			label.setText(subMessage);
			labelGridData = GridDataFactory.swtDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).create();
			label.setLayoutData(labelGridData);
		}

		if (linkToPreferencesId!=null) {
			Link linkToPreferences = new Link(res, SWT.NONE);
			
			GridDataFactory.swtDefaults()
			.align(SWT.FILL, SWT.TOP)
			.grab(true, true)
			.indent(0, LayoutConstants.getMargins().y)
			.applyTo(linkToPreferences);
			
			linkToPreferences.setText(linkToPreferencesText);
			linkToPreferences.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					PreferenceDialog pref = PreferencesUtil.createPreferenceDialogOn(getShell(),
							linkToPreferencesId, null, 
							null);
					pref.setBlockOnOpen(false);
					if (pref != null) {
						pref.open();
					}
				}
			});
		}

		Point hint = res.computeSize(SWT.NONE, SWT.DEFAULT);
		if (labelGridData!=null) {
			labelGridData.widthHint = hint.x + 20;
			labelGridData.heightHint = SWT.DEFAULT;
		}
		hint = res.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		resGridData.widthHint = hint.x;
		resGridData.heightHint = SWT.DEFAULT;

		return res;
	}


	@Override
	public boolean close() {
		if (super.close()) {
			if (wizban!=null) {
				wizban.dispose();
			}
		}
		return false;
	}


}