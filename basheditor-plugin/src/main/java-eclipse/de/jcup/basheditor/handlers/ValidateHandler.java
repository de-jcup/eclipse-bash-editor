package de.jcup.basheditor.handlers;

import de.jcup.basheditor.BashEditor;

public class ValidateHandler extends AbstractBashEditorHandler{

	@Override
	protected void executeOnBashEditor(BashEditor bashEditor) {
		bashEditor.validate();
	}

}
