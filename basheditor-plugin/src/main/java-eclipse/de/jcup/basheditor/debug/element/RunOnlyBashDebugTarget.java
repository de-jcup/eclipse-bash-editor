/*
 * Copyright 2020 Albert Tregnaghi
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
package de.jcup.basheditor.debug.element;

import java.util.Map;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.RuntimeProcess;

import de.jcup.basheditor.debug.BashDebugConstants;
import de.jcup.basheditor.debug.DebugEventSource;
import de.jcup.basheditor.debug.DebugEventSupport;

/**
 * We use this for run only mode
 * @author albert
 *
 */
public class RunOnlyBashDebugTarget implements IDebugTarget, DebugEventSource {
    private DebugEventSupport support = new DebugEventSupport();
	private boolean disconnected;
	private boolean terminated;
    private ILaunch launch;
    private IProcess process;
    private IThread[] threads = new IThread[] {};
    private String name;

	public RunOnlyBashDebugTarget(ILaunch launch, Process process, String name, Map<String, String> attributes) {
	    this.launch=launch;
	    this.name=name;
	}
	
	@Override
	public String getModelIdentifier() {
		return BashDebugConstants.BASH_DEBUG_MODEL_ID;
	}

	@Override
	public IDebugTarget getDebugTarget() {
		return this;
	}

	@Override
	public ILaunch getLaunch() {
		return launch;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return null;
	}

	@Override
	public boolean canTerminate() {
		return true;
	}

	@Override
	public boolean isTerminated() {
		return terminated;
	}

	@Override
	public void terminate() throws DebugException {
	    this.terminated=true;
	    support.fireTerminateEvent(this);
	}

	@Override
	public boolean canResume() {
		return false;
	}

	@Override
	public boolean canSuspend() {
		return false;
	}

	@Override
	public boolean isSuspended() {
		return false;
	}

	@Override
	public void resume() throws DebugException {
	}

	@Override
	public void suspend() throws DebugException {
		
		
	}

	@Override
	public void breakpointAdded(IBreakpoint breakpoint) {
		
		
	}

	@Override
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		
		
	}

	@Override
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		
		
	}

	@Override
	public boolean canDisconnect() {
		return true;
	}

	@Override
	public void disconnect() throws DebugException {
		this.disconnected=true;
	}

	@Override
	public boolean isDisconnected() {
		return disconnected;
	}

	@Override
	public boolean supportsStorageRetrieval() {
		return false;
	}

	@Override
	public IMemoryBlock getMemoryBlock(long startAddress, long length) throws DebugException {
		return null;
	}

	@Override
	public IProcess getProcess() {
		return process;
	}

	@Override
	public IThread[] getThreads() throws DebugException {
		return threads;
	}

	@Override
	public boolean hasThreads() throws DebugException {
		return false;
	}

	@Override
	public String getName() throws DebugException {
		return name;
	}

	@Override
	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		return false;
	}

}
