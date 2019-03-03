package de.jcup.basheditor.debug;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class BashDebugConsole extends MessageConsole {

	public BashDebugConsole(String name, ImageDescriptor imageDescriptor) {
		super(name, imageDescriptor);
	}

	@Override
	public MessageConsoleStream newMessageStream() {
		return super.newMessageStream();
	}
	
	private static BashDebugConsole get() {
		return BashDebugConsoleFactory.INSTANCE.getConsole();
	}
	
	public static void println(String message) {
		get().newMessageStream().println(message);
	}
	
	public static void clear() {
		get().clearConsole();
	}

}