package de.jcup.basheditor.debug.launch;

import java.util.List;

abstract class TerminalCommandListBuilder {
	
    private TerminalCommandStringBuilder terminalCommandStringBuilder = new TerminalCommandStringBuilder();
    protected abstract List<String> buildCommands(TerminalLaunchContext context);

	protected String createBashCallerSnippet(TerminalLaunchContext context) {
		StringBuilder sb = new StringBuilder();
		sb.append("bash --login -c '");
		sb.append(terminalCommandStringBuilder.build(context));
		return sb.toString();
	}

    
}