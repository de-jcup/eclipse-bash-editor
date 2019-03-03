package de.jcup.basheditor.debug;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleManager;

public class BashDebugConsoleFactory implements IConsoleFactory {
	static BashDebugConsoleFactory INSTANCE = new BashDebugConsoleFactory();

	@Override
	public void openConsole() {
		IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
		BashDebugConsole console = getConsole();
		consoleManager.showConsoleView(console);
	}

	public BashDebugConsole getConsole() {
		return findConsole("Bash Debug Console");
	}

	private BashDebugConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++) {
			if (name.equals(existing[i].getName())) {
				return (BashDebugConsole) existing[i];
			}

		}
		// no console found, so create a new one
		BashDebugConsole myConsole = createConsole(name, conMan);
		return myConsole;
	}

	private BashDebugConsole createConsole(String name, IConsoleManager conMan) {
		BashDebugConsole myConsole = new BashDebugConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

}
