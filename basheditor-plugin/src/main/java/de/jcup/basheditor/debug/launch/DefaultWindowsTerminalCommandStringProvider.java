package de.jcup.basheditor.debug.launch;

public class DefaultWindowsTerminalCommandStringProvider implements DefaultTerminalCommandStringProvider {
    
	
    @Override
    public String getStarterCommandString() {
        return "cmd.exe /C start \""+TerminalCommandVariable.CMD_TITLE+"\" cmd.exe /C \""+TerminalCommandVariable.CMD_TERMINAL+"\"";
    }

    @Override
	public String getTerminalCommandString() {
	    return TerminalCommandVariable.CMD_CALL.getVariableRepresentation();
	}


}