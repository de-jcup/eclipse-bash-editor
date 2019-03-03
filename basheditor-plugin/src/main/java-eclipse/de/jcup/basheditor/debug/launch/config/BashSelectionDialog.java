package de.jcup.basheditor.debug.launch.config;

import org.eclipse.core.resources.IContainer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;

public class BashSelectionDialog extends ResourceListSelectionDialog {
	public BashSelectionDialog(Shell parentShell, IContainer container, int typeMask) {
		super(parentShell, container, typeMask);
	}

	protected Control createDialogArea(Composite parent) {
		Control ret = super.createDialogArea(parent);
		refresh(true);
		return ret;
	}

	protected String adjustPattern() {
		String s = super.adjustPattern();
		if (!s.equals("")) {
			return s;
		}
		return "*.sh";
	}

}