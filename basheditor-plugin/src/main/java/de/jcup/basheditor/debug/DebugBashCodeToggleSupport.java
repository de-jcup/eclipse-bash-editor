package de.jcup.basheditor.debug;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class is responsible to change existin source code on a file to have DEBUGGER_script in first line. It shall also be able
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
		sb.append(INCLUDE_PREFIX).append(" ").append(hostname).append(" ").append(port).append(DEBUG_POSTFIX).append(sourceCode);
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
