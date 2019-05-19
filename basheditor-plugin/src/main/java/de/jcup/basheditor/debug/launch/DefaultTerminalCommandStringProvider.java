package de.jcup.basheditor.debug.launch;

public interface DefaultTerminalCommandStringProvider {

    /**
     * The command which will prepare a bash to execute the terminal command
     * @return
     */
    public String getStarterCommandString();

    /**
     * The command which will open a graphical terminal, is a bash operation
     * @return
     */
    public String getTerminalCommandString();
    
}
