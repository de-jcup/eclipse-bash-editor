package de.jcup.basheditor.debug.element;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;

import de.jcup.basheditor.debug.DebugEventSource;

public class BashThread extends AbstractBashDebugElement implements IThread, DebugEventSource {

	/**
	 * Breakpoints this thread is suspended at or <code>null</code> if none.
	 */
	private IBreakpoint[] fBreakpoints;

	/**
	 * Whether this thread is stepping
	 */
	private boolean stepping = false;

	/**
	 * Constructs a new thread for the given target
	 * 
	 * @param target VM
	 */
	public BashThread(BashDebugTarget target) {
		super(target);
	}

	public IStackFrame[] getStackFrames() throws DebugException {
		if (isSuspended()) {
			return ((BashDebugTarget) getDebugTarget()).getStackFrames();
		} else {
			return new IStackFrame[0];
		}
	}

	public boolean hasStackFrames() throws DebugException {
		return isSuspended();
	}

	public int getPriority() throws DebugException {
		return 0;
	}

	public IStackFrame getTopStackFrame() throws DebugException {
		IStackFrame[] frames = getStackFrames();
		if (frames.length > 0) {
			return frames[0];
		}
		return null;
	}

	public String getName() throws DebugException {
		return "Bash-Thread[1]";
	}

	public IBreakpoint[] getBreakpoints() {
		if (fBreakpoints == null) {
			return new IBreakpoint[0];
		}
		return fBreakpoints;
	}

	/**
	 * Sets the breakpoints this thread is suspended at, or <code>null</code> if
	 * none.
	 * 
	 * @param breakpoints the breakpoints this thread is suspended at, or
	 *                    <code>null</code> if none
	 */
	protected void setBreakpoints(IBreakpoint[] breakpoints) {
		fBreakpoints = breakpoints;
	}

	public boolean canResume() {
		return isSuspended();
	}

	public boolean canSuspend() {
		return !isSuspended();
	}

	public boolean isSuspended() {
		return getDebugTarget().isSuspended();
	}

	public void resume() throws DebugException {
		getDebugTarget().resume();
	}

	public void suspend() throws DebugException {
		getDebugTarget().suspend();
	}

	public boolean canStepInto() {
		return isSuspended();
	}

	public boolean canStepOver() {
		return isSuspended();
	}

	public boolean canStepReturn() {
		return isSuspended();
	}

	public boolean isStepping() {
		return stepping;
	}

	public void stepInto() throws DebugException {
		((BashDebugTarget) getDebugTarget()).step();
	}

	public void stepOver() throws DebugException {
		((BashDebugTarget) getDebugTarget()).stepOver();
	}

	public void stepReturn() throws DebugException {
		((BashDebugTarget) getDebugTarget()).stepReturn();
	}

	public boolean canTerminate() {
		return !isTerminated();
	}

	public boolean isTerminated() {
		return getDebugTarget().isTerminated();
	}

	public void terminate() throws DebugException {
		getDebugTarget().terminate();
	}

	public void setStepping(boolean stepping) {
		this.stepping = stepping;
	}
}
