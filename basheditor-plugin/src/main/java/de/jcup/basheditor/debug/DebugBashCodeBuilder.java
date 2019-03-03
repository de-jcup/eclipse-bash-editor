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

public class DebugBashCodeBuilder {
	private static final String DEFAULT_FUNCTION_NAME_TRAP = "_________DEBUG_TRAP";
	private static final String DEFAULT_DEBUG_COMMAND = "_________DEBUG_COMMAND";
	
	final String nameOfTrapFunction = DEFAULT_FUNCTION_NAME_TRAP;
	final String nameOfDebugCommand = DEFAULT_DEBUG_COMMAND;

	private int fileDescriptor = 33;

	public DebugBashCodeBuilder() {
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
		sb.append("exec " + fileDescriptor + "<>/dev/tcp/$1/$2 #params: 1=hostname(e.g localhost),2=port\n");
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
