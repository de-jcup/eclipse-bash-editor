package de.jcup.basheditor.debug.launch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.jcup.basheditor.BashEditorActivator;
import de.jcup.basheditor.debug.BashDebugConsole;
import de.jcup.basheditor.preferences.BashEditorPreferences;
import de.jcup.eclipse.commons.ui.EclipseUtil;

/* 
 * Working example output:
 * @formatter:off
 * 
 * win: cmd.exe /C "bash --login -c 'echo -e "color are \033[0;31mRED\033[0m - or not...";read -p "Press enter to continue"'"
 * lin: bash -c x-terminal-emulator -e bash --login -c 'cd /tmp;./terminallaunch3919760667320099616.sh -a 1 -b 2;_exit_status=$?;echo "Exit code=$_exit_status";if [ $_exit_status -ne 0 ]; then read -p "Unexpected exit code:$_exit_status , press enter to continue";fi;read -p "Press enter to continue"' &
 * 
 * diff linux: win:
 * win: cmd.exe /C "$callerSnippet"
 * lin: bash -c x-terminal-emulator -e $callerSnippet
 * 
 * 
 * @formatter:on
 */
public class TerminalLauncher {

	public final static String DEFAULT_XTERMINAL_SNIPPET = "x-terminal-emulator -e";

	public void execute(File file, String params, String linuxTerminalSnippet) {
		if (linuxTerminalSnippet == null) {
			linuxTerminalSnippet = DEFAULT_XTERMINAL_SNIPPET;
		}
		/* setup context */
		LaunchContext context = new LaunchContext();
		context.file = file;
		context.params = params;
		context.xTerminalSnippet = linuxTerminalSnippet;
		context.waitAlways = isWaitingAlways();
		context.waitOnErrors = isWaitingOnErrors();

		/* build command list */
		TerminalCommandListBuilder commandListBuilder = createOSSpecificCommandListBuilder();
		List<String> commands = commandListBuilder.buildCommands(context);

		/* execute in own thread */
		LaunchRunnable launchRunnable = new LaunchRunnable(context.getWoringDirFile(), commands);
		Thread thread = new Thread(launchRunnable);
		thread.setName("Launch in terminal:" + file.getName());
		thread.start();
	}

	protected boolean isWaitingOnErrors() {
		return BashEditorPreferences.getInstance().isLaunchedTerminalWaitingOnErrors();
	}

	protected boolean isWaitingAlways() {
		return BashEditorPreferences.getInstance().isLaunchedTerminalAlwaysWaiting();
	}

	protected void logExectionError(IOException e) {
		EclipseUtil.logError("Cannot start real runtime process, fall back to dummy", e, BashEditorActivator.getDefault());
	}

	protected void logExecutedCommand(LaunchRunnable runnable) {
		if (BashEditorPreferences.getInstance().isDebugConsoleEnabled()) {
			BashDebugConsole.println(">>> Launch Terminal:");
			BashDebugConsole.println("    " + runnable.createCommandString());
		}
	}

	private TerminalCommandListBuilder createOSSpecificCommandListBuilder() {
		if (OSUtil.isWindows()) {
			return new WindowsTerminalCommandListBuilder();
		}
		return new LinuxTerminalCommandListBuilder();
	}

	private class LaunchContext {
		File file;
		String params;
		String xTerminalSnippet;
		boolean waitAlways;
		boolean waitOnErrors;
		boolean switchToWorkingDirNecessary;

		public String getUnixStyledWorkingDir() {
			return getWoringDirFile().getAbsolutePath();
		}

		public File getWoringDirFile() {
			return file.getParentFile();
		}

		public boolean isSwitchToWorkingDirNecessary() {
			return switchToWorkingDirNecessary;
		}
	}

	private abstract class TerminalCommandListBuilder {
		protected abstract List<String> buildCommands(LaunchContext context);

		protected String createBashCallerSnippet(LaunchContext context) {
			StringBuilder sb = new StringBuilder();
			sb.append("bash --login -c '");
			if (context.isSwitchToWorkingDirNecessary()) {
				sb.append("cd ");
				sb.append(context.getUnixStyledWorkingDir());
				sb.append(";");
			}
			sb.append("./" + context.file.getName());
			sb.append(" " + context.params);

			sb.append(";");
			sb.append("_exit_status=$?");
			sb.append(";");
			sb.append("echo \"Exit code=$_exit_status\"");
			sb.append(";");
			if (context.waitAlways) {
				sb.append("read -p \"Press enter to continue...\"");
			} else if (context.waitOnErrors) {
				sb.append("if [ $_exit_status -ne 0 ]; then read -p \"Unexpected exit code:$_exit_status , press enter to continue\";fi");
			}
			sb.append("'");
			return sb.toString();
		}
	}

	private class WindowsTerminalCommandListBuilder extends TerminalCommandListBuilder {

		@Override
		protected List<String> buildCommands(LaunchContext context) {
			context.switchToWorkingDirNecessary = false; // not necessary, because on windows the working is still same

			List<String> commands = new ArrayList<String>();
			commands.add("cmd.exe");

			commands.add("/C");

			commands.add("start");

			commands.add("\"Bash Editor DEBUG Session: " + context.file.getName() + "\"");

			commands.add("cmd.exe");

			commands.add("/C");

			StringBuilder fullSnippet = new StringBuilder();
			fullSnippet.append("\"");
			fullSnippet.append(createBashCallerSnippet(context));
			fullSnippet.append("\"");
			commands.add(fullSnippet.toString());

			return commands;
		}

	}

	private class LinuxTerminalCommandListBuilder extends TerminalCommandListBuilder {

		@Override
		protected List<String> buildCommands(LaunchContext context) {
			context.switchToWorkingDirNecessary = true; // bash login on linux systems will lead to user home dir

			List<String> commands = new ArrayList<String>();

			commands.add("bash");

			commands.add("-c");

			StringBuilder fullSnippet = new StringBuilder();
			fullSnippet.append(context.xTerminalSnippet);
			fullSnippet.append(" ");
			fullSnippet.append(createBashCallerSnippet(context));
			commands.add(fullSnippet.toString());            	 

			commands.add("&");

			return commands;
		}

	}

	protected class LaunchRunnable implements Runnable {

		private File workingDir;
		private List<String> commands;

		public LaunchRunnable(File workingDir, List<String> commands) {
			this.workingDir = workingDir;
			this.commands = commands;
		}

		@Override
		public void run() {
			ProcessBuilder pb = new ProcessBuilder(commands);
			pb.directory(workingDir);
			logExecutedCommand(this);
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
				logExectionError(e);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		/**
		 * Creates a string presenting the command. Interesting for showing user the
		 * expected command
		 * 
		 * @return
		 */
		String createCommandString() {
			StringBuilder sb = new StringBuilder();
			for (String command : commands) {
				sb.append(command);
				sb.append(" ");
			}
			return sb.toString().trim();
		}
	}

}