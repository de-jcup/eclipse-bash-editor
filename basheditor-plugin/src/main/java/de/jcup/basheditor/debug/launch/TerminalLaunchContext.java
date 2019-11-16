/*
 * Copyright 2019 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.basheditor.debug.launch;

import java.io.File;
import java.util.List;

public class TerminalLaunchContext {
    
    List<String> commands;
	File file;
	String params;
	String terminalCommand;
	boolean waitAlways;
	boolean waitOnErrors;
	boolean switchToWorkingDirNecessary;
    String launchTerminalCommand;
    String terminalExecutionCommand;
    Exception exception;
    String startTemplate;
    
    public String getLaunchTerminalCommand() {
        return launchTerminalCommand;
    }
    
    public String getTerminalExecutionCommand() {
        return terminalExecutionCommand;
    }
    
	public String getUnixStyledWorkingDir() {
		File workingDirFile = getWorkingDirFile();
		if (workingDirFile==null) {
		    return null;
		}
        return OSUtil.toUnixPath(workingDirFile.getAbsolutePath());
	}

	public File getWorkingDirFile() {
	    if (file==null) {
	        return null;
	    }
		return file.getParentFile();
	}

	public boolean isSwitchToWorkingDirNecessary() {
		return switchToWorkingDirNecessary;
	}
}