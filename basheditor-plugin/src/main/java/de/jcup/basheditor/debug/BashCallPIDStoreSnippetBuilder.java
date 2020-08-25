/*
 * Copyright 2020 Albert Tregnaghi
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

import de.jcup.basheditor.debug.launch.OSUtil;

public class BashCallPIDStoreSnippetBuilder {
	
	private File tmpFolder;

	public BashCallPIDStoreSnippetBuilder() {
		tmpFolder = new File(System.getProperty("user.home"),".basheditor");
	}
	
	public String buildPIDFileAbsolutePath(String port) {
		String path = buildPIDFile(port).toPath().toAbsolutePath().toString();
		return path;
	}
	
	public String buildPIDParentFolderAbsolutePath() {
		String path =tmpFolder.toPath().toAbsolutePath().toString();
		return path;
	}
	
	private File buildPIDFile(String port) {
		return new File(tmpFolder, createFileName(port));
	}

	private String createFileName(String port) {
		return BashPIDSnippetSupport.DEBUGGER_TERMINAL_PID_FILENAME + port + ".txt";
	}

	public String buildWritePIDToPortSpecificTmpFileSnippet(int port) {
        StringBuilder sb = new StringBuilder();
        /* @formatter:on */
        sb.append("cd \"").append(OSUtil.toUnixPath(buildPIDParentFolderAbsolutePath())).append("\";");
        sb.append("./").append(BashPIDSnippetSupport.FILENAME_STORE_TERMINAL_PIDS_SCRIPT);
        sb.append(" ").append(port);
        sb.append(" $$");
        sb.append(";");
        /* @formatter:off */
        return sb.toString();
	}
}
