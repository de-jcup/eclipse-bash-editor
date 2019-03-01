package de.jcup.basheditor.debug.launch;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;

public class DummyBashProcess implements IProcess {
	public boolean terminated = false;

	public synchronized boolean isTerminated() {
		return terminated;
	}

	public synchronized boolean canTerminate() {
		return true;
	}

	public <T> T getAdapter(Class<T> adapter) {
		return null;
	}

	public void terminate() throws DebugException {
		terminated = true;

	}

	public String getLabel() {
		return "Bash script process";
	}

	public ILaunch getLaunch() {
		return null;
	}

	public IStreamsProxy getStreamsProxy() {
		return null;
	}

	public void setAttribute(String key, String value) {

	}

	public String getAttribute(String key) {
		return null;
	}

	public int getExitValue() throws DebugException {
		return 0;
	}

}
