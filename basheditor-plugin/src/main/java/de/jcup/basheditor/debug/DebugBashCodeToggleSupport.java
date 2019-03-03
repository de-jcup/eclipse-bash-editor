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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class is responsible to change existing source code on a file to have DEBUGGER_script in first line. It shall also be able
 * to remove this line from a source file.
 * @author albert
 *
 */
public class DebugBashCodeToggleSupport {
	private static final File BASH_CODE_DEBUGGER_FILE = new File(System.getProperty("user.home"),".basheditor/remote-debugging-v1.sh");
	private static final String INCLUDE_PREFIX = "source "+BASH_CODE_DEBUGGER_FILE.getAbsolutePath();
	private static final String DEBUG_POSTFIX="#BASHEDITOR-TMP-REMOTE-DEBUGGING-END\n";
	private DebugBashCodeBuilder codeBuilder;

	
	public DebugBashCodeToggleSupport() {
		this.codeBuilder=new DebugBashCodeBuilder();
	}

	public String enableDebugging(String sourceCode, String hostname, int port) throws IOException {
		ensureDebugFileExists();
		disableDebugging(sourceCode); // if we got some call before with maybe another port or host etc.
		StringBuilder sb = new StringBuilder();
		sb.append(INCLUDE_PREFIX).append(" ").append(hostname).append(" ").append(port).append(" ").append(DEBUG_POSTFIX).append(sourceCode);
		return sb.toString();
	}

	private void ensureDebugFileExists() throws IOException{
		if (BASH_CODE_DEBUGGER_FILE.exists()) {
			return;
		}
		BASH_CODE_DEBUGGER_FILE.getParentFile().mkdirs();
		BASH_CODE_DEBUGGER_FILE.createNewFile();
		BASH_CODE_DEBUGGER_FILE.setExecutable(true,true);
		
		String snippet = codeBuilder.buildDebugBashCodeSnippet();
		try (FileWriter fw = new FileWriter(BASH_CODE_DEBUGGER_FILE); BufferedWriter bw = new BufferedWriter(fw)) {
			bw.write(snippet);
		}
		
	}

	public String disableDebugging(String sourceCode) throws IOException {
		int index = sourceCode.indexOf(DEBUG_POSTFIX);
		if (index==-1) {
			return sourceCode;
		}
		int pos = index+ DEBUG_POSTFIX.length();
		String data = sourceCode.substring(pos);
		return data;
	}
	
}
