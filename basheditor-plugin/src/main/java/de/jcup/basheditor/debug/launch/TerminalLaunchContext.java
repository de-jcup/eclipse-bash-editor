package de.jcup.basheditor.debug.launch;

import java.io.File;

class TerminalLaunchContext {
	File file;
	String params;
	String terminalCommand;
	boolean waitAlways;
	boolean waitOnErrors;
	boolean switchToWorkingDirNecessary;

	public String getUnixStyledWorkingDir() {
		return getWoringDirFile().getAbsolutePath();
	}

	public File getWoringDirFile() {
		return file.getParentFile();
	}

	public boolean isSwitchToWorkingDirNecessary() {
		return switchToWorkingDirNecessary;
	}
}