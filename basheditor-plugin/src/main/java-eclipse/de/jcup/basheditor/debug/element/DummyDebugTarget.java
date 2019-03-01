package de.jcup.basheditor.debug.element;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;

/**
 * Could be used for "run" mode.
 * @author albert
 *
 */
public class DummyDebugTarget implements IDebugTarget {

	private boolean disconnected;

	@Override
	public String getModelIdentifier() {
		
		return null;
	}

	@Override
	public IDebugTarget getDebugTarget() {
		
		return null;
	}

	@Override
	public ILaunch getLaunch() {
		
		return null;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		
		return null;
	}

	@Override
	public boolean canTerminate() {
		
		return false;
	}

	@Override
	public boolean isTerminated() {
		
		return false;
	}

	@Override
	public void terminate() throws DebugException {
		
		
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
		
		return null;
	}

	@Override
	public IThread[] getThreads() throws DebugException {
		return null;
	}

	@Override
	public boolean hasThreads() throws DebugException {
		return false;
	}

	@Override
	public String getName() throws DebugException {
		return null;
	}

	@Override
	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		return false;
	}

}
