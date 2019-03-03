/*
 * Copyright 2019 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
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
