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

import java.io.IOException;
import java.net.BindException;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.jface.dialogs.ErrorDialog;

import de.jcup.basheditor.BashEditorActivator;
import de.jcup.basheditor.debug.element.BashDebugTarget;
import de.jcup.basheditor.debug.element.BashStackFrame;
import de.jcup.basheditor.debug.element.BashThread;
import de.jcup.basheditor.debug.element.BashVariable;
import de.jcup.basheditor.debug.launch.BashDocumentChangeRegistry;
import de.jcup.basheditor.debug.launch.BashDocumentChangeRegistry.DocumentChange;
import de.jcup.basheditor.debug.launch.BashDocumentChangeRegistry.DocumentChanges;
import de.jcup.basheditor.debug.launch.BashSourceLookupParticipant;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class BashDebugger {
	private DebugEventSupport eventsupport = new DebugEventSupport();
	private final ReentrantLock lock = new ReentrantLock();

	private int stackLevelStop = -100;
	private int stackLevel = -1;
	private int currentline = -1;
	private int previousLine = -1;

	private boolean stepIn = false;
	private boolean resume = false;
	private boolean suspend = false;
	private boolean breakpointToggled = false;
	private boolean terminate = false;

	private boolean starting;

	BashNetworkConnector bashConnector;
	private IStackFrame[] stackFrames;
	private BashDebugTarget debugTarget;
	private BashThread bashThread;

	public static class StackElement {
		int currentline;
		String source;
		int level;
		String name;
	}

	public IStackFrame[] getStackFrames() {
		return stackFrames;
	}

	public boolean isStepIn() {
		return stepIn;
	}

	public boolean isBreakpointToggled() {
		return breakpointToggled;
	}

	public boolean isTerminated() {
		return terminate;
	}

	public enum DebugCommand {
		STEP,

		RESUME,

		SUSPEND,

		BREAKPOINT_TOGGLED,

		TERMINATE,

		STEP_OVER,

		STEP_RETURN
	}

	public BashDebugger(BashDebugTarget debugTarget, BashThread bashThread) {
		stackFrames = new IStackFrame[0];
		this.debugTarget = debugTarget;
		this.bashThread = bashThread;
	}

	public synchronized void sendCommand(DebugCommand cmd) {
		switch (cmd) {
		case STEP:
			stepIn = true;
			synchronized (this) {
				notify();
			}
			break;
		case STEP_OVER:
			stackLevelStop = stackLevel;
			resume = true;
			synchronized (this) {
				notify();
			}
			break;
		case STEP_RETURN:
			stackLevelStop = stackLevel - 1;
			resume = true;
			synchronized (this) {
				notify();
			}
			break;
		case RESUME:
			stackLevelStop = -1;
			resume = true;
			synchronized (this) {
				notify();
			}
			break;
		case SUSPEND:
			suspend = true;
			break;
		case BREAKPOINT_TOGGLED:
			breakpointToggled = true;
			break;
		case TERMINATE:
			terminate = true;
			try {
				if (starting) {
					bashConnector.cancel();
				}
			} catch (Exception e) {
				EclipseUtil.logError("Terminate problem: was not able to cancel", e, BashEditorActivator.getDefault());
			}
			synchronized (this) {
				notify();
			}
			break;
		default:
			break;
		}
	}

	public boolean startDebugServerSession(int port) throws Exception {
		starting = true;

		bashConnector = new BashNetworkConnector(port);
		try {
			bashConnector.startServerSocket();

		} catch (BindException e) {
			IStatus status = new Status(IStatus.ERROR, BashEditorActivator.getDefault().getPluginID(), "Bash debug session binding failed for port:" + port, e);
			EclipseUtil.safeAsyncExec(() -> ErrorDialog.openError(null, "Bash debug error", "Bash debug session binding failed.", status));
			return false;
		} catch (Exception e) {
			EclipseUtil.logError("Unable to start debug session", e, BashEditorActivator.getDefault());
			return false;
		}
		starting = false;
		return true;
	}

	public boolean isConnected() {
		if (bashConnector == null) {
			return false;
		}
		return bashConnector.isConnected();
	}

	public void uiUpdate() throws Exception {

		// workaround: used to bypass incorrect display of variable values ​​if the
		// previous breakpoint was on same line
		BashStackFrame bashStackFrame = (BashStackFrame) stackFrames[0];
		boolean variableWorkaroundForSameLine = stackFrames.length > 0 && previousLine == bashStackFrame.frameLineNumber;
		if (variableWorkaroundForSameLine) {
			int cachedFrameLineNumber = bashStackFrame.frameLineNumber;

			if (cachedFrameLineNumber > 0) {
				bashStackFrame.frameLineNumber--;
			} else {
				bashStackFrame.frameLineNumber++;
			}

			debugTarget.suspended(DebugEvent.STEP_END);

			bashThread.setStepping(true);
			/* wait to get auto selection chance to select (use isStepping()) */
			Thread.sleep(60);
			bashThread.setStepping(false);

			debugTarget.resumed(DebugEvent.STEP_OVER);

			bashStackFrame.frameLineNumber = cachedFrameLineNumber;
			Thread.sleep(60);
		}
		if (stackFrames.length > 0) {
			previousLine = bashStackFrame.frameLineNumber;
		}
		debugTarget.suspended(DebugEvent.STEP_END);
		bashThread.setStepping(true);

	}

	public void process(boolean xstopOnStartup) throws Exception {
		
		ProcessContext context = new ProcessContext(this);
		
		if (xstopOnStartup) {
			context.stop();
		}
		if (!context.isStopped()) {
			resume = true;
		}
		do {
			lock();

			bashConnector.stepBegin();
			context.update(bashConnector);
			
			stackLevel = context.getBashLineNumber().getArraySize();

			handleStop(context);

			createStack(context);
			createFrames(context);
			
			unlock();
			if (context.isStopped()) {
				resume = false;
				if (stackFrames.length > 0) {
					uiUpdate();
				}
				synchronized (this) {
					if (stackFrames.length > 0) {
						wait();
					}
				}
			}
			context.stop();

			lock();

			stepIn = false;
			bashThread.setStepping(false);
			debugTarget.resumed(DebugEvent.STEP_OVER);

			if (terminate) {
				bashConnector.terminate();
				break;
			}
			bashConnector.stepEnd();

			unlock();
		} while (true);
		eventsupport.fireTerminateEvent(bashThread);
		bashThread.setStepping(false);

	}

	private void createStack(ProcessContext context) {
		
		BashNetworkVariableData bashLineNumber = context.getBashLineNumber();
		BashNetworkVariableData functionName = context.getFunctionName();
		BashNetworkVariableData bashSource = context.getBashSource();
		
		int maxStackLevel = bashSource.getArraySize() - 1;
		context.clearStack();
		
		for (int stackLevel = 0; stackLevel <= maxStackLevel; stackLevel++) {
			StackElement stackElement = new StackElement();
			
			stackElement.currentline = bashLineNumber.getIntValue(stackLevel);
			stackElement.source = bashSource.getStringValue(stackLevel);
			stackElement.name = functionName.getStringValue(stackLevel);
			stackElement.level = stackLevel;
			
			context.addStack(stackElement);
		}
	}

	private void handleStop(ProcessContext context) throws CoreException {
		if (resume) {
			stopOnStackLevelStopOrSuspend(context);
			stopOnBreakpoint(context);
		}
		suspend = false;
	}

	private void stopOnStackLevelStopOrSuspend(ProcessContext processContext) {
		processContext.go();
		if (stackLevel == stackLevelStop || suspend) {
			processContext.stop();
		}
	}

	private void stopOnBreakpoint(ProcessContext context) throws CoreException {
		IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
		if (! breakpointManager.isEnabled()) {
			/* all breakpoints turned off...*/
			return;
		}
		BashNetworkVariableData bashLineNumber = context.getBashLineNumber();
		BashNetworkVariableData bashSource = context.getBashSource();
		
		IBreakpoint[] breakpoints = breakpointManager.getBreakpoints(BashDebugConstants.BASH_DEBUG_MODEL_ID);
		int line;
		int currentline = bashLineNumber.getIntValue(0);
		String source = bashSource.getStringValue(0);
		for (int i = 0; i < breakpoints.length; i++) {
			ILineBreakpoint breakPoint = (ILineBreakpoint) breakpoints[i];
			if (!breakPoint.isEnabled()) {
				continue;
			}
			line = breakPoint.getLineNumber();
			
			String lookupSource = BashSourceLookupParticipant.getReverseLookupSourceItem(breakPoint.getMarker().getResource().getFullPath());
			if (line == currentline && source.equals(lookupSource)) {
				context.stop();
				break;
			}
		}
	}

	public void lock() {
		try {
			lock.lock();
		} catch (Exception e) {
			EclipseUtil.logError("Unable to lock debugger", e, BashEditorActivator.getDefault());
		}
	}

	public void unlock() {
		try {
			lock.unlock();
		} catch (Exception e) {
			EclipseUtil.logError("Unable to unlock debugger", e, BashEditorActivator.getDefault());
		}
	}

	public IValue getValue(String expression, IDebugElement element) throws Exception {
		if (element instanceof BashStackFrame) {
			BashStackFrame frame = (BashStackFrame) element;
			IValue value = findValue(expression, frame.getVariables());
			return value;
		}
		return null;
	}

	IValue findValue(String expression, IVariable[] vars) throws DebugException {
		IValue value = null;
		String name = expression;
		for (int i = 0; i < vars.length; i++) {
			if (vars[i].getName().equals(name)) {
				value = vars[i].getValue();
				break;
			}
		}
		return value;
	}

	private void createFrames(ProcessContext context) {
		IStackFrame[] framesTemp = new IStackFrame[context.getStackSize()];
		for (int level = 0; level < context.getStackSize(); level++) {
			StackElement stackElement = context.getStack(level);
			currentline = stackElement.currentline;
			String source = stackElement.source;
			
			BashDocumentChangeRegistry documentChangeRegistry = debugTarget.getDocumentChangeRegistry();
			IFile fileResource = debugTarget.getFileResource();
			IContainer parent = fileResource.getParent();
			
			DocumentChanges docChanges = documentChangeRegistry.getDocumentChanges(source,parent);
			if (docChanges != null) {
				for (int i = 0; i < docChanges.changes.size(); i++) {
					DocumentChange docChange = docChanges.changes.get(i);
					if (docChange.line <= currentline) {
						currentline += docChange.numLines;
					}
				}
				if (currentline <= 0)
					currentline = 1;
			}
			framesTemp[level] = new BashStackFrame(bashThread, source, stackElement.name + ":  " + source + "  : line " + currentline, currentline, level, stackElement);
		}
		stackFrames = framesTemp;

	}

	public IVariable[] createBashVariables(BashStackFrame frame) {
		IVariable[] variables = new IVariable[bashConnector.getVariableCount()];
		for (int i = 0; i < variables.length; i++) {
			BashNetworkVariableData data = bashConnector.getVariableData(i);
			variables[i] = new BashVariable(frame, data);
		}
		return variables;
	}

	public void markBreakPointsToggled() {
		this.breakpointToggled = true;
	}

	public void markTerminated() {
		this.terminate = true;
	}

	public void disconnect() throws IOException {
		starting = false;
		if (bashConnector != null) {
			bashConnector.disconnect();
		}

	}

	public void reset() {
		stackFrames = new IStackFrame[0];
	}

	public void connect() throws IOException {
		if (bashConnector == null) {
			throw new IOException("No connector available");
		}
		bashConnector.connect();

	}

}
