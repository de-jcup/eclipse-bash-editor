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
	private static final File BASH_CODE_DEBUGGER_FILE = new File(System.getProperty("user.home"),".basheditor/_debug_v1.sh");
	private static final String INCLUDE = "source "+BASH_CODE_DEBUGGER_FILE.getAbsolutePath();
	private DebugBashCodeBuilder codeBuilder;

	public DebugBashCodeToggleSupport() {
		this.codeBuilder=new DebugBashCodeBuilder();
	}

	public String enableDebugging(String sourceCode) throws IOException {
		ensureDebugFileExists();
		if (sourceCode.startsWith(INCLUDE)) {
			return sourceCode;
		}
		return INCLUDE+" "+sourceCode;
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
		if (!sourceCode.startsWith(INCLUDE)) {
			return sourceCode;
		}
		String data = sourceCode.substring(INCLUDE.length());
		if (data.startsWith("\n")) {
			/* new command line found and not a # ...*/
			data=data.substring(1);
		}
		return data;
	}
	
}
