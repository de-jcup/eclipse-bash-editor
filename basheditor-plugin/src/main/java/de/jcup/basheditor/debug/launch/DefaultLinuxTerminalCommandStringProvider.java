package de.jcup.basheditor.debug.launch;

public class DefaultLinuxTerminalCommandStringProvider implements DefaultTerminalCommandStringProvider {

    @Override
    public String getDefaultStarterCommandString() {
        return "bash -c "+TerminalCommandVariable.CMD_TERMINAL.getVariableRepresentation()+" &";
    }
    
    @Override
    public String getDefaultTerminalCommandString() {
        return "x-terminal-emulator -e bash --login -c '"+TerminalCommandVariable.CMD_CALL.getVariableRepresentation()+"'";
	}


}