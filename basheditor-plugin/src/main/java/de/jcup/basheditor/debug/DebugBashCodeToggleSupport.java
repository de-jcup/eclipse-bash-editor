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
import java.util.Objects;

/**
 * Class is responsible to change existing source code on a file to have
 * DEBUGGER_script in first line. It shall also be able to remove this line from
 * a source file.
 * 
 * @author albert
 *
 */
public class DebugBashCodeToggleSupport {
    private static final String PATH_FROM_USER_HOME_TO_DEBUGGER_SCRIPT = ".basheditor/remote-debugging-v1.sh";
    private static final String DEBUG_POSTFIX = "#BASHEDITOR-TMP-REMOTE-DEBUGGING-END|Origin line:";
    private DebugBashCodeBuilder codeBuilder;
    private BashDebugInfoProvider infoProvider;

    public DebugBashCodeToggleSupport(BashDebugInfoProvider infoProvider) {
        Objects.requireNonNull(infoProvider);
        
        this.infoProvider=infoProvider;
        this.codeBuilder = new DebugBashCodeBuilder();
        
    }

    public String enableDebugging(String sourceCode, String hostname, int port) throws IOException {
        ensureDebugFileExistsInSystemUserHome();
        String nSourceCode= disableDebugging(sourceCode); // if we got some call before with maybe another port or host etc.
        StringBuilder sb = new StringBuilder();
        sb.append(createSourceToInclude(infoProvider.getResultingScriptPathToUserHome())).append(" ").append(hostname).append(" ").append(port).append(" ");
        sb.append(DEBUG_POSTFIX);
        if (!nSourceCode.startsWith("#!")) {
            // this means its not something like #!/bin/bash etc. means: this line could be important and we do not want to just override it
            // so add a new line here:
            sb.append("\n");
        }
        sb.append(nSourceCode);
        return sb.toString();
    }

    private File resolveDebuggerFile(String base) {
        return new File(base, PATH_FROM_USER_HOME_TO_DEBUGGER_SCRIPT);
    }

    private String createSourceToInclude(String base) { 
        StringBuilder sb = new StringBuilder();
        sb.append("source ");
        if (base!=null) {
            sb.append(base);
            if (!base.endsWith("/")) {
                sb.append("/");
            }
        }else {
            sb.append("/tmp/missing-base/");
        }
        sb.append(PATH_FROM_USER_HOME_TO_DEBUGGER_SCRIPT);
        return sb.toString();
    }
    

   
    private File ensureDebugFileExistsInSystemUserHome() throws IOException {
        /* ensure debug script file does really exist on user.home */
        File debuggerFile = resolveDebuggerFile(infoProvider.getSystemUserHomePath());
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
        if (data.startsWith("\n")) { 
            /* in this case this means that first line was not a sheebang - e.g. "#! /bin bash" or so and so there was a newline added
             * and we must remove it as well!
             */
            data=data.substring(1);
        }
        return data;
    }

}
