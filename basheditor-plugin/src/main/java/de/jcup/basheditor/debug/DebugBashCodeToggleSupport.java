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

import de.jcup.basheditor.debug.launch.OSUtil;

/**
 * Class is responsible to change existing source code on a file to have
 * DEBUGGER_script in first line. It shall also be able to remove this line from
 * a source file.
 * 
 * @author albert
 *
 */
public class DebugBashCodeToggleSupport {
    private static final String DEBUG_POSTFIX = "#BASHEDITOR-TMP-REMOTE-DEBUGGING-END\n";
    private DebugBashCodeBuilder codeBuilder;

    public DebugBashCodeToggleSupport() {
        this.codeBuilder = new DebugBashCodeBuilder();
    }

    public String enableDebugging(String sourceCode, String hostname, int port) throws IOException {
        File debuggerFile = ensureDebugFileExists();
        disableDebugging(sourceCode); // if we got some call before with maybe another port or host etc.
        StringBuilder sb = new StringBuilder();
        sb.append(createSourceToInclude(debuggerFile)).append(" ").append(hostname).append(" ").append(port).append(" ").append(DEBUG_POSTFIX).append(sourceCode);
        return sb.toString();
    }

    private File resolveDebuggerFile() {
        return new File(System.getProperty("user.home"), ".basheditor/remote-debugging-v1.sh");
    }

    private String createSourceToInclude(File debuggerFile) {
        if (debuggerFile == null) {
            throw new IllegalStateException("file may not be null");
        }
        return "source " + convertToUnixStylePath(debuggerFile.getAbsolutePath());
    }

    

    String convertToUnixStylePath(String absolutePath) {
        return OSUtil.toUnixPath(absolutePath);
    }

    private File ensureDebugFileExists() throws IOException {
        File debuggerFile = resolveDebuggerFile();
        if (debuggerFile.exists()) {
            return debuggerFile;
        }
        debuggerFile.getParentFile().mkdirs();
        debuggerFile.createNewFile();
        debuggerFile.setExecutable(true, true);

        String snippet = codeBuilder.buildDebugBashCodeSnippet();
        try (FileWriter fw = new FileWriter(debuggerFile); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(snippet);
        }
        return debuggerFile;

    }

    public String disableDebugging(String sourceCode) throws IOException {
        int index = sourceCode.indexOf(DEBUG_POSTFIX);
        if (index == -1) {
            return sourceCode;
        }
        int pos = index + DEBUG_POSTFIX.length();
        String data = sourceCode.substring(pos);
        return data;
    }

}
