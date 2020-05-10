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
package de.jcup.basheditor.debug.launch.config;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

import de.jcup.basheditor.BashEditorActivator;
import de.jcup.basheditor.BashEditorUtil;
import de.jcup.basheditor.MultipleInputDialog;
import de.jcup.eclipse.commons.ui.EclipseUtil;
import de.jcup.eclipse.commons.ui.SWTFactory;

public class BashLaunchConfigurationPropertiesTab extends AbstractLaunchConfigurationTab {

	protected TableViewer propertiesTable;
	protected String[] propertyTableColumnHeaders = { "Key", "Value", };
	private static final String NAME_LABEL = "Name";
	private static final String VALUE_LABEL = "Value";
	protected static final String P_VARIABLE = "variable";
	protected static final String P_VALUE = "value";
	protected Button propertyAddButton;
	protected Button propertyEditButton;
	protected Button propertyRemoveButton;
	private String title;
	private String launchConfigurationPropertyMapAttributeName;
	private String imagePath;
	private String tabId;

	public BashLaunchConfigurationPropertiesTab(String title, String tabId, String imagePath,
			String launchConfigurationPropertyMapAttributeName) {
		this.title = title;
		this.imagePath = imagePath;
		this.launchConfigurationPropertyMapAttributeName = launchConfigurationPropertyMapAttributeName;
		this.tabId = tabId;
	}

	/**
	 * Content provider for the environment table
	 */
	protected class PropertiesVariableContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			PropertyVariable[] elements = new PropertyVariable[0];
			ILaunchConfiguration config = (ILaunchConfiguration) inputElement;
			Map<String, String> m;
			try {
				m = config.getAttribute(launchConfigurationPropertyMapAttributeName, (Map<String, String>) null);
			} catch (CoreException e) {
				BashEditorUtil.logError("Error reading configuration", e);
				return elements;
			}
			if (m != null && !m.isEmpty()) {
				elements = new PropertyVariable[m.size()];
				String[] varNames = new String[m.size()];
				m.keySet().toArray(varNames);
				for (int i = 0; i < m.size(); i++) {
					elements[i] = new PropertyVariable(varNames[i], (String) m.get(varNames[i]));
				}
			}
			return elements;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (newInput == null) {
				return;
			}
			if (viewer instanceof TableViewer) {
				TableViewer tableViewer = (TableViewer) viewer;
				if (tableViewer.getTable().isDisposed()) {
					return;
				}
				tableViewer.setComparator(new ViewerComparator() {
					public int compare(Viewer iviewer, Object e1, Object e2) {
						if (e1 == null) {
							return -1;
						} else if (e2 == null) {
							return 1;
						} else {
							return ((PropertyVariable) e1).getName()
									.compareToIgnoreCase(((PropertyVariable) e2).getName());
						}
					}
				});
			}
		}
	}

	/**
	 * Label provider for the table
	 */
	public class PropertiesVariableLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object element, int columnIndex) {
			String result = null;
			if (element != null) {
				PropertyVariable var = (PropertyVariable) element;
				switch (columnIndex) {
				case 0: // variable
					result = var.getName();
					break;
				case 1: // value
					result = var.getValue();
					break;
				}
			}
			return result;
		}

		public Image getColumnImage(Object element, int columnIndex) {
			if (columnIndex == 0) {
				return EclipseUtil.getImage("/icons/launch-environment-key.png", BashEditorActivator.getDefault());
			}
			return null;
		}
	}

	public void createControl(Composite parent) {
		// Create main composite
		Composite mainComposite = SWTFactory.createComposite(parent, 2, 1, GridData.FILL_HORIZONTAL);
		setControl(mainComposite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), getHelpContextId());

		createEnvironmentTable(mainComposite);
		createTableButtons(mainComposite);

		Dialog.applyDialogFont(mainComposite);
	}

	/**
	 * Creates and configures the table that displayed the key/value pairs that
	 * comprise the environment.
	 * 
	 * @param parent
	 *            the composite in which the table should be created
	 */
	protected void createEnvironmentTable(Composite parent) {
		Font font = parent.getFont();
		SWTFactory.createLabel(parent, "Variables to &set:", 2);
		// Create table composite
		Composite tableComposite = SWTFactory.createComposite(parent, font, 1, 1, GridData.FILL_BOTH, 0, 0);
		// Create table
		propertiesTable = new TableViewer(tableComposite,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
		Table table = propertiesTable.getTable();
		table.setLayout(new GridLayout());
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setFont(font);
		propertiesTable.setContentProvider(new PropertiesVariableContentProvider());
		propertiesTable.setLabelProvider(new PropertiesVariableLabelProvider());
		propertiesTable.setColumnProperties(new String[] { P_VARIABLE, P_VALUE });
		propertiesTable.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				handleTableSelectionChanged(event);
			}
		});
		propertiesTable.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (!propertiesTable.getSelection().isEmpty()) {
					handlePropertiesEditButtonSelected();
				}
			}
		});
		// Create columns
		final TableColumn tc1 = new TableColumn(table, SWT.NONE, 0);
		tc1.setText(propertyTableColumnHeaders[0]);
		final TableColumn tc2 = new TableColumn(table, SWT.NONE, 1);
		tc2.setText(propertyTableColumnHeaders[1]);
		final Table tref = table;
		final Composite comp = tableComposite;
		tableComposite.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle area = comp.getClientArea();
				Point size = tref.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				ScrollBar vBar = tref.getVerticalBar();
				int width = area.width - tref.computeTrim(0, 0, 0, 0).width - 2;
				if (size.y > area.height + tref.getHeaderHeight()) {
					Point vBarSize = vBar.getSize();
					width -= vBarSize.x;
				}
				Point oldSize = tref.getSize();
				if (oldSize.x > area.width) {
					tc1.setWidth(width / 2 - 1);
					tc2.setWidth(width - tc1.getWidth());
					tref.setSize(area.width, area.height);
				} else {
					tref.setSize(area.width, area.height);
					tc1.setWidth(width / 2 - 1);
					tc2.setWidth(width - tc1.getWidth());
				}
			}
		});
	}

	/**
	 * Responds to a selection changed event in the table
	 * 
	 * @param event
	 *            the selection change event
	 */
	protected void handleTableSelectionChanged(SelectionChangedEvent event) {
		int size = ((IStructuredSelection) event.getSelection()).size();
		propertyEditButton.setEnabled(size == 1);
		propertyRemoveButton.setEnabled(size > 0);
	}

	/**
	 * Creates the add/edit/remove buttons for the table
	 * 
	 * @param parent
	 *            the composite in which the buttons should be created
	 */
	protected void createTableButtons(Composite parent) {
		// Create button composite
		Composite buttonComposite = SWTFactory.createComposite(parent, parent.getFont(), 1, 1,
				GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_END, 0, 0);

		// Create buttons
		propertyAddButton = createPushButton(buttonComposite, "N&ew...", null);
		propertyAddButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				handlePropertiesAddButtonSelected();
			}
		});
		propertyEditButton = createPushButton(buttonComposite, "E&dit...", null);
		propertyEditButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				handlePropertiesEditButtonSelected();
			}
		});
		propertyEditButton.setEnabled(false);
		propertyRemoveButton = createPushButton(buttonComposite, "Rem&ove", null);
		propertyRemoveButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				handlePropertiesRemoveButtonSelected();
			}
		});
		propertyRemoveButton.setEnabled(false);
	}

	/**
	 * Adds a new variable to the table.
	 */
	protected void handlePropertiesAddButtonSelected() {
		MultipleInputDialog dialog = new MultipleInputDialog(getShell(), "New Variable");
		dialog.addTextField(NAME_LABEL, null, false);
		dialog.addVariablesField(VALUE_LABEL, null, true);

		if (dialog.open() != Window.OK) {
			return;
		}

		String name = dialog.getStringValue(NAME_LABEL);
		String value = dialog.getStringValue(VALUE_LABEL);

		if (name != null && value != null && name.length() > 0 && value.length() > 0) {
			addVariable(new PropertyVariable(name.trim(), value.trim()));
		}
	}

	/**
	 * Attempts to add the given variable. Returns whether the variable was
	 * added or not (as when the user answers not to overwrite an existing
	 * variable).
	 * 
	 * @param variable
	 *            the variable to add
	 * @return <code>true</code> when variable was added
	 */
	protected boolean addVariable(PropertyVariable variable) {
		String name = variable.getName();
		TableItem[] items = propertiesTable.getTable().getItems();
		for (int i = 0; i < items.length; i++) {
			PropertyVariable existingVariable = (PropertyVariable) items[i].getData();
			if (existingVariable.getName().equals(name)) {
				boolean overWrite = MessageDialog.openQuestion(getShell(), "Overwrite variable?",
						MessageFormat.format("A variable named {0} already exists. Overwrite?", new Object[] { name })); //
				if (!overWrite) {
					return false;
				}
				propertiesTable.remove(existingVariable);
				break;
			}
		}
		propertiesTable.add(variable);
		updateLaunchConfigurationDialog();
		return true;
	}

	private void handlePropertiesEditButtonSelected() {
		IStructuredSelection sel = (IStructuredSelection) propertiesTable.getSelection();
		PropertyVariable var = (PropertyVariable) sel.getFirstElement();
		if (var == null) {
			return;
		}
		String originalName = var.getName();
		String value = var.getValue();
		MultipleInputDialog dialog = new MultipleInputDialog(getShell(), "Edit Property Variable");
		dialog.addTextField(NAME_LABEL, originalName, false);
		if (value != null && value.indexOf(System.getProperty("line.separator")) > -1) {
			dialog.addMultilinedVariablesField(VALUE_LABEL, value, true);
		} else {
			dialog.addVariablesField(VALUE_LABEL, value, true);
		}

		if (dialog.open() != Window.OK) {
			return;
		}
		String name = dialog.getStringValue(NAME_LABEL);
		value = dialog.getStringValue(VALUE_LABEL);
		if (!originalName.equals(name)) {
			if (addVariable(new PropertyVariable(name, value))) {
				propertiesTable.remove(var);
			}
		} else {
			var.setValue(value);
			propertiesTable.update(var, null);
			updateLaunchConfigurationDialog();
		}
	}

	private void handlePropertiesRemoveButtonSelected() {
		IStructuredSelection sel = (IStructuredSelection) propertiesTable.getSelection();
		propertiesTable.getControl().setRedraw(false);
		for (@SuppressWarnings("unchecked")
		Iterator<PropertyVariable> i = sel.iterator(); i.hasNext();) {
			PropertyVariable var = i.next();
			propertiesTable.remove(var);
		}
		propertiesTable.getControl().setRedraw(true);
		updateLaunchConfigurationDialog();
	}

	protected void updateProperties(ILaunchConfiguration configuration) {
		propertiesTable.setInput(configuration);
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		updateProperties(configuration);
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		// Convert the table's items into a Map so that this can be saved in the
		// configuration's attributes.
		TableItem[] items = propertiesTable.getTable().getItems();
		Map<String, String> map = new HashMap<>(items.length);
		for (int i = 0; i < items.length; i++) {
			PropertyVariable var = (PropertyVariable) items[i].getData();
			map.put(var.getName(), var.getValue());
		}
		if (map.size() == 0) {
			configuration.setAttribute(launchConfigurationPropertyMapAttributeName, (Map<String, String>) null);
		} else {
			configuration.setAttribute(launchConfigurationPropertyMapAttributeName, map);
		}

	}

	public String getName() {
		return title;
	}

	public String getId() {
		return "de.jcup.basheditor.launchtab." + tabId;
	}

	public Image getImage() {
		return EclipseUtil.getImage(imagePath,BashEditorActivator.getDefault());
	}

	public void activated(ILaunchConfigurationWorkingCopy workingCopy) {
		// do nothing when activated
	}

	public void deactivated(ILaunchConfigurationWorkingCopy workingCopy) {
		// do nothing when deactivated
	}

}