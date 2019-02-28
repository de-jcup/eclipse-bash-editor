package de.jcup.basheditor.debug;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;

public class DebugEventSupport {
	
	protected void fireEvent(DebugEvent event) {
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { event });
	}

	public void fireCreationEvent(DebugEventSource source) {
		fireEvent(new DebugEvent(source, DebugEvent.CREATE));
	}

	public void fireResumeEvent(DebugEventSource source, int detail) {
		fireEvent(new DebugEvent(source, DebugEvent.RESUME, detail));
	}

	public void fireSuspendEvent(DebugEventSource source, int detail) {
		fireEvent(new DebugEvent(source, DebugEvent.SUSPEND, detail));
	}

	public void fireTerminateEvent(DebugEventSource source) {
		fireEvent(new DebugEvent(source, DebugEvent.TERMINATE));
	}
}
