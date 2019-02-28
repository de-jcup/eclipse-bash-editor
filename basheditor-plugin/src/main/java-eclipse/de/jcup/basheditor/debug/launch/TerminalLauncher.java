package de.jcup.basheditor.debug.launch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.jcup.basheditor.BashEditorActivator;
import de.jcup.basheditor.debug.BashDebugConsole;
import de.jcup.basheditor.preferences.BashEditorPreferences;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class TerminalLauncher {

	public void execute(File file, String params,TerminalLauncherConfig config) {
		
		Thread thread = new Thread(new LaunchRunnable(file.getParentFile(), buildCommands(file,params,config)));
		thread.setName("Launch in terminal:"+file.getName());
		thread.start();
	}
	
	private List<String> buildCommands(File file, String params, TerminalLauncherConfig config) {
		List<String> commands = new ArrayList<String>();
		
		/* handle prefix - e.g. on windows */
		safeAddArrayIfNotEmpty(commands, config.getCommandsToCallBashInitial());
		
		/* this is bash only - no matter if on windows or linux */
		commands.add("bash");
		commands.add("-c");

		StringBuilder sb = new StringBuilder();
		/* on some systems theres is a need to execute a special terminal window command to keep window open */
		sb.append(config.getTerminalWindowCommand());
		sb.append(" bash --login -c '");
		sb.append("cd ");
		sb.append(file.getParentFile().getAbsolutePath());
		sb.append(";");
		sb.append("./" + file.getName());
		sb.append(" " + params);

		sb.append(";");
		sb.append("_exit_status=$?");
		sb.append(";");
		sb.append("echo \"Exit code=$_exit_status\"");
		sb.append(";");
		sb.append("if [ $_exit_status -ne 0 ]; then read -p \"Unexpected exit code:$_exit_status , press enter to continue\";fi");
		
		if (Boolean.getBoolean("basheditor.debug.afterexecution.wait.always")) {
			sb.append(";");
			sb.append("read -p \"Press enter to continue\"");
		}
		sb.append("'");
		commands.add(sb.toString());

		safeAddArrayIfNotEmpty(commands, config.getCommandsAtEnd());
		
		return commands;
		
	}

	private void safeAddArrayIfNotEmpty(List<String> commands, String[] cmdToCallBash) {
		if (cmdToCallBash!=null) {
			for (String cmd: cmdToCallBash) {
				if (cmd==null || cmd.isEmpty()) {
					continue;
				}
				commands.add(cmd);
			}
		}
	}


	private class LaunchRunnable implements Runnable{
		
		private File workingDir;
		private List<String> commands;
		
		public LaunchRunnable(File workingDir, List<String> commands) {
			this.workingDir=workingDir;
			this.commands=commands;
		}
		@Override
		public void run() {
			ProcessBuilder pb = new ProcessBuilder(commands);
			pb.directory(workingDir);
			if (BashEditorPreferences.getInstance().isDebugConsoleEnabled()) {
				BashDebugConsole.println(">>> Launch Terminal:");
				BashDebugConsole.println("    " + createCommandString());
			}
			Process p;
			try {
				pb.inheritIO();
				p = pb.start();
				int result = p.waitFor();
				if (result != 0) {
					System.err.println("result:" + result);
				} else {
					System.out.println("OK");
				}
				
			} catch (IOException e) {
				EclipseUtil.logError("Cannot start real runtime process, fall back to dummy", e, BashEditorActivator.getDefault());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		
		/**
		 * Creates a string presenting the command. Interesting for showing user the expected command
		 * @return
		 */
		private String createCommandString() {
			StringBuilder sb = new StringBuilder();
			for (String command: commands) {
				sb.append(command);
				sb.append(" ");
			}
			return sb.toString().trim();
		}
	}

	public interface TerminalLauncherConfig {
		
		public String getTerminalWindowCommand();
		
		/**
		 * @return commands before first "bash" call, or <code>null</code>
		 */
		public String[] getCommandsToCallBashInitial();
		
		public String[] getCommandsAtEnd();
		
		public TerminalLauncherConfig createClone();
		
	}

	static class GnomeTerminalConfig extends LinuxTerminalConfig{
		public String getTerminalWindowCommand() {
			return "x-terminal-emulator -e";
		}

		@Override
		public TerminalLauncherConfig createClone() {
			return new GnomeTerminalConfig();
		}
	
	}

	static abstract class LinuxTerminalConfig implements TerminalLauncherConfig{
		public String getTerminalWindowCommand() {
			return "x-terminal-emulator -e";
		}
	
		@Override
		public String[] getCommandsToCallBashInitial() {
			return null;
		}
		@Override
		public String[] getCommandsAtEnd() {
			return new String[] {"&"};
		}
	}

	static class WindowsTerminalConfig implements TerminalLauncherConfig{
		public String getTerminalWindowCommand() {
			return null;
		}
	
		@Override
		public String[] getCommandsToCallBashInitial() {
			return new String[] {"cmd.exe","/C"};
		}
		
		@Override
		public String[] getCommandsAtEnd() {
			return null;
		}

		@Override
		public TerminalLauncherConfig createClone() {
			return new WindowsTerminalConfig();
		}
	}

	public enum DefaultLaunchConfig{
		LINUX_GNOME("Linux (Gnome/Mate)",new GnomeTerminalConfig()),
		
		WINDOWS("Windows",new WindowsTerminalConfig()),
		
		;
		private TerminalLauncherConfig prototype;
		private String label;
	
		private DefaultLaunchConfig(String label, TerminalLauncherConfig config) {
			this.prototype=config;
			this.label=label;
		}
		public String getLabel() {
			return label;
		}
		
		public TerminalLauncherConfig create() {
			return prototype.createClone();
		}
	}

	public String createExampleCommand(TerminalLauncherConfig terminalLauncherConfig) {
		String userHome=System.getProperty("user.home");
		File file = new File(userHome, "example.sh");
		LaunchRunnable runnable = new LaunchRunnable(file,buildCommands(file, "--some-example-param", terminalLauncherConfig));
		return runnable.createCommandString();
	}

}