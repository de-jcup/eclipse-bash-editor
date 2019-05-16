package de.jcup.basheditor.debug.launch;

import java.util.ArrayList;
import java.util.List;

class WindowsTerminalCommandListBuilder extends TerminalCommandListBuilder {

	@Override
	protected List<String> buildCommands(TerminalLaunchContext context) {
		context.switchToWorkingDirNecessary = false; // not necessary, because on windows the working is still same

		List<String> commands = new ArrayList<String>();
		commands.add("cmd.exe");

		commands.add("/C");

		commands.add("start");

		commands.add("\"Bash Editor DEBUG Session: " + context.file.getName() + "\"");

		commands.add("cmd.exe");

		commands.add("/C");

		StringBuilder fullSnippet = new StringBuilder();
		fullSnippet.append("\"");
		fullSnippet.append(createBashCallerSnippet(context));
		fullSnippet.append("\"");
		commands.add(fullSnippet.toString());

		return commands;
	}

}