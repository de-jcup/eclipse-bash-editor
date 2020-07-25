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

import de.jcup.basheditor.debug.BashDebugCodeBuilder;

public class InternalTerminalCommandStringBuilder {
    
    private BashDebugCodeBuilder bashCodeBuilder = new BashDebugCodeBuilder();

    public String build(TerminalLaunchContext context) {
        if (context==null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(bashCodeBuilder.buildWritePIDToPortSpecificTmpFileSnippet(""+context.getPort()));
        if (context.isSwitchToWorkingDirNecessary()) {
            sb.append("cd ");
            sb.append(context.getUnixStyledWorkingDir());
            sb.append(";");
        }
        String fileName = null;
        if (context.file!=null) {
            fileName=context.file.getName();
        }
        sb.append("./" + fileName);
        if (context.params!=null) {
            sb.append(" ");
            sb.append(context.params);
        }
        sb.append(";");
        sb.append("_exit_status=$?;");
        sb.append("echo \"Exit code=$_exit_status\"");
        sb.append(";");
        if (context.waitAlways) {
            sb.append("read -p \"Press enter to continue...\"");
        } else if (context.waitOnErrors) {
            sb.append("if [ $_exit_status -ne 0 ]; then read -p \"Unexpected exit code:$_exit_status , press enter to continue\";fi");
        }
//        sb.append("; exit $_exit_status");
        return sb.toString();
    }
}
