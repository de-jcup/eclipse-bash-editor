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
import java.util.Map;

public class TerminalLaunchContext {

    public enum RunMode {
        DEBUG,

        RUN,

        JUST_OPEN_TERMINAL,

    }

    RunMode runMode = RunMode.DEBUG;
    List<String> commands;
    File file;
    String params;
    String terminalCommand;
    boolean waitAlways;
    boolean waitOnErrors;
    String launchTerminalCommand;
    String terminalExecutionCommand;
    Exception exception;
    String startTemplate;
    Map<String, String> environment;
    int port;
    File workingDir;
    String openInTerminalCommand;

    public int getPort() {
        return port;
    }

    public File getWorkingDir() {
        return workingDir;
    }

    public RunMode getRunMode() {
        return runMode;
    }
    
    public String getOpenInTerminalCommand() {
        return openInTerminalCommand;
    }

    public String getLaunchTerminalCommand() {
        return launchTerminalCommand;
    }

    public Map<String, String> getEnvironment() {
        return environment;
    }

    public String getTerminalExecutionCommand() {
        return terminalExecutionCommand;
    }

    public String getUnixStyledWorkingDir() {
        File workingDirFile = getWorkingDirFile();
        if (workingDirFile == null) {
            return null;
        }
        return OSUtil.toUnixPath(workingDirFile.getAbsolutePath());
    }

    /**
     * Resolves working directory. If directly set in context, the defined working
     * directory will be used. If not set, but file to execute is defined, the
     * working directory will be the files parent directory. If no file defined
     * working directory result will be <code>null</code>
     * 
     * @return working dir or <code>null</code>
     */
    public File getWorkingDirFile() {
        if (workingDir != null) {
            return workingDir;
        }
        if (file == null) {
            return null;
        }
        return file.getParentFile();
    }

}