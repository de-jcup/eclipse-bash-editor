package de.jcup.basheditor.debug.launch;

import java.io.File;
import java.util.List;

class TerminalLaunchContext {
    List<String> commands;
	File file;
	String params;
	String terminalCommand;
	boolean waitAlways;
	boolean waitOnErrors;
	boolean switchToWorkingDirNecessary;
    String commandString;
    Exception exception;
    String startTemplate;

	public String getUnixStyledWorkingDir() {
		File workingDirFile = getWoringDirFile();
		if (workingDirFile==null) {
		    return null;
		}
        return workingDirFile.getAbsolutePath();
	}

	public File getWoringDirFile() {
	    if (file==null) {
	        return null;
	    }
		return file.getParentFile();
	}

	public boolean isSwitchToWorkingDirNecessary() {
		return switchToWorkingDirNecessary;
	}
}