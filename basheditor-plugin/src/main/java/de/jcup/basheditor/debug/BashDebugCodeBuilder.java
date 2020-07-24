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

public class BashDebugCodeBuilder {

    public static final String DEBUGGER_TERMINAL_PID_FILENAME = "basheditor_terminal_pid4port_";
    public static final BashDebugCodeBuilder SHARED = new BashDebugCodeBuilder();
    private static final String DEFAULT_FUNCTION_NAME_TRAP = "_________DEBUG_TRAP";
    private static final String DEFAULT_DEBUG_COMMAND = "_________DEBUG_COMMAND";

    final String nameOfTrapFunction = DEFAULT_FUNCTION_NAME_TRAP;
    final String nameOfDebugCommand = DEFAULT_DEBUG_COMMAND;

    private int fileDescriptor = 33;
    private File tmpFolder;
    private String userName;

    private BashDebugCodeBuilder() {
        String tmpdir = System.getProperty("java.io.tmpdir");
        userName = System.getProperty("user.name");
        tmpFolder = new File(tmpdir);// ,"basheditor");
        
    }

    public String getNameOfDebugCommand() {
        return nameOfDebugCommand;
    }

    public String getNameOfTrapFunction() {
        return nameOfTrapFunction;
    }

    public String getNameOfBashLineNumberVariable() {
        return "BASH_LINENO";
    }

    public String getNameOfFunctionNameVariable() {
        return "FUNCNAME";
    }

    public String getNameOfBashSourceVariable() {
        return "BASH_SOURCE";
    }

    public void setFileDescriptor(int fileDescriptor) {
        this.fileDescriptor = fileDescriptor;
    }

    public String buildDebugBashCodeSnippet() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nexec " + fileDescriptor + "<>/dev/tcp/$1/$2 #params: 1=hostname(e.g localhost),2=port\n");
        sb.append("function " + nameOfTrapFunction + "()\n");
        sb.append("{\n");
        sb.append("	local " + nameOfDebugCommand + "\n");
        sb.append("	read -u " + fileDescriptor + " " + nameOfDebugCommand + "\n");
        sb.append("	eval $" + nameOfDebugCommand + "\n");
        sb.append("}\n");
        sb.append("set -o functrace\n");
        sb.append("trap " + nameOfTrapFunction + " DEBUG\n");

        return sb.toString();

    }


    public String buildPIDFileAbsolutePath(String port) {
        return buildPIDFile(port).toPath().toAbsolutePath().toString();
    }

    private File buildPIDFile(String port) {
        return new File(tmpFolder, DEBUGGER_TERMINAL_PID_FILENAME + port + "_"+userName+".txt");
    }

    public String buildWritePIDToPortSpecificTmpFileSnippet(String port) {
        StringBuilder sb = new StringBuilder();
        /* @formatter:on */
        sb.append("_debug_terminal_pid=$$;");
        String pidFileAbsolutePath = buildPIDFileAbsolutePath(port);
        sb.append("echo \"using PID file:" + pidFileAbsolutePath + "\";");
        sb.append("touch " + pidFileAbsolutePath + ";");
        sb.append("echo $_debug_terminal_pid >> ").append(pidFileAbsolutePath).append(";");
        /* @formatter:off */
        return sb.toString();
	}
	
    /**
     * Creates kill script
     * @param port - can be also a string - e.g. $2 for second parameter..
     * @return
     */
	public String buildKillOldTerminalsSnippet(String port) {
	    StringBuilder sb = new StringBuilder();
	    /* @formatter:off */
        String buildPIDFileAbsolutePath = buildPIDFileAbsolutePath(port);
        sb.append("while IFS=\'\' read -r LINE || [ -n \"${LINE}\" ]; do\n" + 
                  "        kill -9 ${LINE}\n" + 
                  " done < ").append(buildPIDFileAbsolutePath).append(";\n").
            append("rm ").append(buildPIDFileAbsolutePath).append(";");
        /* @formatter:on */
	    return sb.toString();

	}

	public String buildRemoteDebugCommand() {
		return "set >&" + fileDescriptor + " ; echo $'\\\\x0A\\\\x0A\\\\x0A\\\\x0A\\\\x0A\\\\x0A\\\\x0A\\\\x0A\\\\x0A\\\\x0A\\\\x0A\\\\x09' >&" + fileDescriptor + " ; read -u " + fileDescriptor + " "
				+ getNameOfDebugCommand() + "\n";
	}

	public String buildSafeArrayValue(String text) {
		if (text==null) {
			return "";
		}
		if (text.startsWith("\"")) {
			text=text.substring(1);
		}
		if (text.startsWith("(")) {
			text=text.substring(1);
		}
		if (text.endsWith(")")) {
			text= text.substring(0,text.length()-1);
		}
		if (text.endsWith("\"")) {
			text= text.substring(0,text.length()-1);
		}
		return text;
	}

	public int getLinesOfDebugCode() {
	    return 10;
	}
	
}
