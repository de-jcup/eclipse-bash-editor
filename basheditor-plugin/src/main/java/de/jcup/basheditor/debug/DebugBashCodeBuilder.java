package de.jcup.basheditor.debug;

public class DebugBashCodeBuilder {
	private static final String DEFAULT_FUNCTION_NAME_TRAP = "_________DEBUG_TRAP";
	private static final String DEFAULT_DEBUG_COMMAND = "_________DEBUG_COMMAND";

	final String nameOfTrapFunction = DEFAULT_FUNCTION_NAME_TRAP;
	final String nameOfDebugCommand = DEFAULT_DEBUG_COMMAND;

	private int fileDescriptor = 33;
	private int port = 33333;
	private String hostname = "localhost";

	public DebugBashCodeBuilder() {
	}

	public String getNameOfDebugCommand() {
		return nameOfDebugCommand;
	}
	
	public String getNameOfTrapFunction() {
		return nameOfTrapFunction;
	}
	
	public void setPort(int port) {
		this.port = port;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
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
		sb.append("exec " + fileDescriptor + "<>/dev/tcp/" + hostname + "/" + port + "\n");
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
	
}
