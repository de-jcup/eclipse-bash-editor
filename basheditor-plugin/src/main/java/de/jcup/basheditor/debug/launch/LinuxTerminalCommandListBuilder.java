package de.jcup.basheditor.debug.launch;

import java.util.ArrayList;
import java.util.List;

class LinuxTerminalCommandListBuilder extends TerminalCommandListBuilder {

	@Override
	protected List<String> buildCommands(TerminalLaunchContext context) {
		context.switchToWorkingDirNecessary = true; // bash login on linux systems will lead to user home dir

		List<String> commands = new ArrayList<String>();

		commands.add("bash");

		commands.add("-c");

		StringBuilder fullSnippet = new StringBuilder();
		fullSnippet.append(context.terminalCommand);
		fullSnippet.append(" ");
		fullSnippet.append(createBashCallerSnippet(context));
		commands.add(fullSnippet.toString());            	 

		commands.add("&");

		return commands;
	}

}