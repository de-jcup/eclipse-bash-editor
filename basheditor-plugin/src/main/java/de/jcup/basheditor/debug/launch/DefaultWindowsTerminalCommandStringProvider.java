package de.jcup.basheditor.debug.launch;

public class DefaultWindowsTerminalCommandStringProvider implements DefaultTerminalCommandStringProvider {
    
	
    @Override
    public String getDefaultStarterCommandString() {
        return "cmd.exe /C start \""+TerminalCommandVariable.CMD_TITLE+"\" cmd.exe /C \""+TerminalCommandVariable.CMD_TERMINAL+"\"";
    }

    @Override
	public String getDefaultTerminalCommandString() {
	    return TerminalCommandVariable.CMD_CALL.getVariableRepresentation();
	}


}