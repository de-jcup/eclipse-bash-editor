package de.jcup.basheditor.debug.launch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.jcup.basheditor.BashEditorActivator;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class TerminalLauncher implements Runnable{
	private List<String> commands;
	private File file;

	/* FIXME Albert, 28.02.2019: provide this for windows too and provide a customization field in preferences, so user can change */
	TerminalLauncher(File file, String params){
		this.file = file;
		commands = new ArrayList<String>();
		addFirstCommands();
		
		StringBuilder sb = new StringBuilder();
		sb.append(getTerminalWindowCommand());
		sb.append(" bash --login -c '");
		sb.append("cd ");
		sb.append(file.getParentFile().getAbsolutePath());
		sb.append(";");
		sb.append("./"+file.getName());
		sb.append(" "+params);
		
		if (Boolean.getBoolean("basheditor.debug.afterexecution.wait")) {
			sb.append(";");
			sb.append("echo \"Exit code=$?\"");
			sb.append(";");
			sb.append("read -p \"Press enter to continue\"");
		}
		sb.append("'");
		commands.add(sb.toString());
		
		addLastCommands();
	}


	private void addLastCommands() {
		commands.add("&");
	}


	private void addFirstCommands() {
		commands.add("bash");
		commands.add("-c");
	}


	private String getTerminalWindowCommand() {
		return "x-terminal-emulator -e";
	}
	
	
	public void execute() {
		Thread thread = new Thread(this);
		thread.setName("launch terminal");
		thread.start();
	}


	@Override
	public void run() {
		ProcessBuilder pb = new ProcessBuilder(commands);
		pb.directory(file.getParentFile());
		System.out.println(">>> launching:"+commands);
		Process p;
		try {
			pb.inheritIO();
			p = pb.start();
			int result = p.waitFor();
			if (result!=0) {
				System.err.println("result:"+result);
			}else {
				System.out.println("OK");
			}
			
		} catch (IOException e) {
			EclipseUtil.logError("Cannot start real runtime process, fall back to dummy", e, BashEditorActivator.getDefault());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
}