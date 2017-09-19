package de.jcup.basheditor.handlers;

import de.jcup.basheditor.BashEditor;

public class OpenQuickOutlineHandler extends AbstractBashEditorHandler {

	public static final String COMMAND_ID = "egradle.editor.commands.quickoutline";

	@Override
	protected void executeOnBashEditor(BashEditor bashEditor) {
		bashEditor.openQuickOutline();
		
	}
	

}