package de.jcup.basheditor.debug.element;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;

public class DummyProcess implements IProcess{

	private String label;
	private boolean terminated;
	private ILaunch launch;
	private int exitCode;
	public DummyProcess(String label, ILaunch launch) {
		this(label,launch,-1);
	}
	public DummyProcess(String label, ILaunch launch, int exitCode) {
		this.label=label;
		this.launch=launch;
		this.exitCode=exitCode;
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
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public ILaunch getLaunch() {
		return launch;
	}

	@Override
	public IStreamsProxy getStreamsProxy() {
		return null;
	}

	@Override
	public void setAttribute(String key, String value) {
		
	}

	@Override
	public String getAttribute(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getExitValue() throws DebugException {
		if (exitCode!=-1) {
			return exitCode;
		}
		return 0;
	}
	
	public void setExitCode(int exitCode) {
		this.exitCode = exitCode;
	}

}
