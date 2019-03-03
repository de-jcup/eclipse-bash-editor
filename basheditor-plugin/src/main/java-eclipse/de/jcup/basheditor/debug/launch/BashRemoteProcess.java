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
package de.jcup.basheditor.debug.launch;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;

public class BashRemoteProcess implements IProcess {
	public boolean terminated = false;
    private ILaunch launch;
    private int exitValue = -1;
	
	public BashRemoteProcess(ILaunch launch) {
	    this.launch=launch;
	}

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
		return "Bash remote debug process";
	}

	public ILaunch getLaunch() {
		return launch;
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
		return exitValue;
	}

}
