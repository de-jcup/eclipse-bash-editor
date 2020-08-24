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
