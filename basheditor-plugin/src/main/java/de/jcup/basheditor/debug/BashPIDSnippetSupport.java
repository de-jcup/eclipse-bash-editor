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
package de.jcup.basheditor.debug;

import java.io.File;
import java.io.IOException;

import de.jcup.basheditor.debug.launch.SnippetUtil;

public class BashPIDSnippetSupport {
	public static final String FILENAME_STORE_TERMINAL_PIDS_SCRIPT = "store-terminal-pid-v2.sh";
	public static final String DEBUGGER_TERMINAL_PID_FILENAME = "PID_debug-terminal_port_";
	
	
	private static final String PATH_FROM_USER_HOME_TO_KILL_OLD_TERMINALS_SCRIPT = ".basheditor/kill-old-terminals-v2.sh";
    private static final String PATH_FROM_USER_HOME_TO_STORE_TERMINAL_PIDS_SCRIPT = ".basheditor/"+FILENAME_STORE_TERMINAL_PIDS_SCRIPT;
    
	BashCallPIDStoreSnippetBuilder bashPIDfileSupport;
	private BashDebugInfoProvider infoProvider;

	public BashPIDSnippetSupport(BashDebugInfoProvider infoProvider) {
		bashPIDfileSupport = new BashCallPIDStoreSnippetBuilder();
		this.infoProvider=infoProvider;
	}

	/**
	 * Creates kill script
	 * 
	 * @param port - can be also a string - e.g. $2 for second parameter..
	 * @return
	 */
	public String buildKillOldTerminalsSnippet() {
		/* @formatter:off */
		String code=
				"cd ~/.basheditor\n" + 
				"KILL_TEXTFILE=\"./PID_debug-terminal_port_$1.txt\"\n" + 
				"if [ -f \"$KILL_TEXTFILE\" ]; then\n" + 
				"  while IFS='' read -r LINE || [ -n \"${LINE}\" ]; do\n" + 
				"        kill -9 ${LINE}\n" + 
				"  done < $KILL_TEXTFILE;\n" + 
				"  \n" + 
				"  rm \"$KILL_TEXTFILE\"\n" + 
				"else \n" + 
				"  echo \"No file found :$KILL_TEXTFILE inside pwd=$PWD\"\n" + 
				"fi\n" + 
				"";
		/* @formatter:on */
		return code;

	}

	public String buildStoreTerminalPIDSnippet() {
		/* @formatter:off */
		String code=
				"#!/bin/bash \n" + 
				"# usage: "+FILENAME_STORE_TERMINAL_PIDS_SCRIPT+" [PORT] [PID]\n" + 
				"echo \"$2\" >> "+DEBUGGER_TERMINAL_PID_FILENAME+"$1.txt";
		/* @formatter:on */
		return code;
	}
	
	private File resolveKillOldTerminalFiles(String base) {
        return new File(base, PATH_FROM_USER_HOME_TO_KILL_OLD_TERMINALS_SCRIPT);
    }
    private File resolveStoreTerminalPIDFiles(String base) {
    	return new File(base, PATH_FROM_USER_HOME_TO_STORE_TERMINAL_PIDS_SCRIPT);
    }
    
    public String getAbsolutePathToEnsuredKillOldTerminalScript() throws IOException {
        return ensureKillOldTerminalFileExistsInSystemUserHome().toPath().toAbsolutePath().toString();
    }
    
    public String getAbsolutePathToEnsuredStoreTerminalPIDsScript() throws IOException {
        return ensureStoreTerminalPIDFileExistsInSystemUserHome().toPath().toAbsolutePath().toString();
    }
   
    public File ensureKillOldTerminalFileExistsInSystemUserHome() throws IOException {
    	File file = resolveKillOldTerminalFiles(infoProvider.getSystemUserHomePath());
    	return SnippetUtil.ensureExecutableFile(file, ()-> buildKillOldTerminalsSnippet());
    }
    
    public File ensureStoreTerminalPIDFileExistsInSystemUserHome() throws IOException {
    	File file = resolveStoreTerminalPIDFiles(infoProvider.getSystemUserHomePath());
    	return SnippetUtil.ensureExecutableFile(file, ()-> buildStoreTerminalPIDSnippet());
    }
}
