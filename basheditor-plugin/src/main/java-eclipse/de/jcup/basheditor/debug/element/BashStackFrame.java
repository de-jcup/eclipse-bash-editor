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
package de.jcup.basheditor.debug.element;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDisconnect;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;

import de.jcup.basheditor.BashEditorActivator;
import de.jcup.basheditor.debug.BashDebugger;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class BashStackFrame extends AbstractBashDebugElement implements IStackFrame, IDisconnect {

	private BashThread thread;
	private String functionName;
	public int frameLineNumber;
	private String fileName;
	private int id;
	public IVariable[] variables;
	BashDebugger.StackElement stackElement;

	public BashStackFrame(BashThread thread, String fileName, String functionName, int currentLine, int id, BashDebugger.StackElement stackElement) {
		super((BashDebugTarget) thread.getDebugTarget());
		this.id = id;
		this.thread = thread;
		this.frameLineNumber = currentLine;
		int atSignIndex = fileName.indexOf("@");
		if (atSignIndex == -1) {
			this.fileName = fileName;
		} else {
			this.fileName = fileName.substring(atSignIndex + 1);
		}
		this.functionName = functionName;
		this.stackElement = stackElement;
	}

	public IThread getThread() {
		return thread;
	}

	public IVariable[] getVariables() throws DebugException {
		if (variables == null) {
			BashDebugTarget bashDebugTarget = getBashDebugTarget();
			bashDebugTarget.lock();
			try {
				variables = bashDebugTarget.createBashVariables(this);
			} catch (Exception e) {
				EclipseUtil.logError("Was not able to get variables!", e, BashEditorActivator.getDefault());
			}
			bashDebugTarget.unlock();
		}
		if (variables == null) {
			return new IVariable[0];
		}
		return variables;
	}

	public boolean hasVariables() throws DebugException {
		getVariables();
		if (variables == null) {
			return false;
		}
		return variables.length > 0;
	}

	public int getLineNumber() throws DebugException {
		return frameLineNumber;
	}

	public int getCharStart() throws DebugException {
		return -1;
	}

	public int getCharEnd() throws DebugException {
		return -1;
	}

	public String getName() throws DebugException {
		return functionName;
	}

	public IRegisterGroup[] getRegisterGroups() throws DebugException {
		return new IRegisterGroup[] {};
	}

	public boolean hasRegisterGroups() throws DebugException {
		return false;
	}

	public boolean canStepInto() {
		return getThread().canStepInto();
	}

	public boolean canStepOver() {
		return getThread().canStepOver();
	}

	public boolean canStepReturn() {
		return getThread().canStepReturn();
	}

	public boolean isStepping() {
		return getThread().isStepping();
	}

	public void stepInto() throws DebugException {
		getThread().stepInto();
	}

	public void stepOver() throws DebugException {
		getThread().stepOver();
	}

	public void stepReturn() throws DebugException {
		getThread().stepReturn();
	}

	public boolean canResume() {
		return getThread().canResume();
	}

	public boolean canSuspend() {
		return getThread().canSuspend();
	}

	public boolean isSuspended() {
		return getThread().isSuspended();
	}

	public void resume() throws DebugException {
		getThread().resume();
	}

	public void suspend() throws DebugException {
		getThread().suspend();
	}

	public boolean canTerminate() {
		return getThread().canTerminate();
	}

	public boolean isTerminated() {
		return getThread().isTerminated();
	}

	public void terminate() throws DebugException {
		getThread().terminate();
	}

	public String getSourceFileName() {
		return fileName;
	}

	public boolean equals(Object obj) {
		if (! (obj instanceof BashStackFrame)) {
			return false;
		}
		BashStackFrame stackFrame = (BashStackFrame) obj;
		try {
			
			String frameSourceFileName = stackFrame.getSourceFileName();
			if(frameSourceFileName==null) {
				return false;
			}
			boolean sameName = frameSourceFileName.equals(getSourceFileName());
			if (!sameName) {
				return false;
			}
			boolean sameLineNumber = stackFrame.getLineNumber() == getLineNumber();
			if (!sameLineNumber) {
				return false;
			}
			boolean sameFrameId = stackFrame.id == id;
			return  sameFrameId;
		} catch (DebugException e) {
			EclipseUtil.logError("Was not able to handle equals!", e, BashEditorActivator.getDefault());
			return false;
		}
	}

	public int hashCode() {
		String sourceFileName = getSourceFileName();
		if (sourceFileName == null) {
			return -1;
		}
		return sourceFileName.hashCode() + id;
	}

	protected int getIdentifier() {
		return id;
	}

	public boolean canDisconnect() {
		return false;
	}

	public void disconnect() throws DebugException {
		/* ignore */
	}

	public boolean isDisconnected() {
		return false;
	}
}
