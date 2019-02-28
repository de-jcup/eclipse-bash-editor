package de.jcup.basheditor.debug;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

import de.jcup.basheditor.BashEditorActivator;
import de.jcup.basheditor.debug.DebugBashCodeBuilder;
import de.jcup.basheditor.debug.element.BashDebugTarget;
import de.jcup.basheditor.debug.element.BashStackFrame;
import de.jcup.basheditor.debug.element.BashThread;
import de.jcup.basheditor.debug.element.BashVariable;
import de.jcup.basheditor.debug.launch.BashSourceLookupParticipant;
import de.jcup.basheditor.debug.launch.BashDocumentChangeRegistry.DocumentChange;
import de.jcup.basheditor.debug.launch.BashDocumentChangeRegistry.DocumentChanges;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class BashDebugger {
	private int stackLevelStop = -100;
	private int stackLevel = -1;
	private boolean stepIn = false;
	private boolean resume = false;
	private boolean suspend = false;
	private boolean breakpointToggled = false;
	private boolean terminate = false;
	
	private DebugEventSupport eventsupport = new DebugEventSupport();
	
	private final ReentrantLock lock = new ReentrantLock();
	private int currentline = -1;
	private int previousLine = -1;
	volatile boolean inAcceptMode = false;

	private BashNetworkConnector bashConnector;
	private IStackFrame[] stackFrames;
	private BashDebugTarget debugTarget;
	private BashThread bashThread;

	public Vector<StackElement> stack = new Vector<StackElement>();

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

	public BashDebugger(BashDebugTarget fTarget, BashThread fThread) {
		stackFrames = new IStackFrame[0];
		this.debugTarget = fTarget;
		this.bashThread = fThread;
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
				if (inAcceptMode) {
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

	public void accept(int port) throws Exception {
		inAcceptMode = true;
		ServerSocket serverSocket = new ServerSocket(port);

		DebugBashCodeBuilder builder = new DebugBashCodeBuilder();
		builder.setPort(port);

		bashConnector = new BashNetworkConnector(serverSocket, builder);
		bashConnector.connect();
		inAcceptMode = false;
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
			}else {
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

	public void process(boolean stopOnStartup) throws Exception {
		if (!stopOnStartup) {
			resume = true;
		}
		do {
			lock();

			bashConnector.stepBegin();

			BashNetworkVariableData bashLineNumber = bashConnector.getBashLineNumber();
			BashNetworkVariableData functionName = bashConnector.getFunctionName();
			BashNetworkVariableData bashSource = bashConnector.getBashSource();

			stackLevel = bashLineNumber.getArraySize();
			if (resume) {
				stopOnStartup = false;
				if (stackLevel == stackLevelStop || suspend) {
					stopOnStartup = true;
				}
				IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(BashDebugConstants.BASH_DEBUG_MODEL_ID);
				int line;
				int currentline = bashLineNumber.getIntValue(0);
				String source = bashSource.getStringValue(0);
				for (int i = 0; i < breakpoints.length; i++) {
					if (!((ILineBreakpoint) breakpoints[i]).isEnabled())
						continue;
					line = ((ILineBreakpoint) breakpoints[i]).getLineNumber();
					String lookupSource = BashSourceLookupParticipant.getReverseLookupSourceItem(((ILineBreakpoint) breakpoints[i]).getMarker().getResource().getFullPath());
					if (line == currentline && source.equals(lookupSource)) {
						stopOnStartup = true;
						break;
					}
				}
			}
			suspend = false;
			int max_stack_level = bashSource.getArraySize() - 1;
			stack.clear();
			for (int stack_level = 0; stack_level <= max_stack_level; stack_level++) {
				StackElement ar = new StackElement();
				ar.currentline = bashLineNumber.getIntValue(stack_level);
				ar.source = bashSource.getStringValue(stack_level);
				ar.name = functionName.getStringValue(stack_level);
				ar.level = stack_level;
				stack.add(ar);
			}
			createFrames();
			unlock();
			if (stopOnStartup) {
				resume = false;
				if (stackFrames.length > 0) {
					uiUpdate();
				}
				synchronized (this) {
					if (stackFrames.length > 0)
						wait();
				}
			}
			stopOnStartup = true;
			
			lock();
			
			stepIn = false;
			bashThread.setStepping(false);
			debugTarget.resumed(DebugEvent.STEP_OVER);
			bashConnector.stepEnd();
			
			if (terminate) {
				bashConnector.terminate();
				break;
			}
			unlock();
		} while (true);
		eventsupport.fireTerminateEvent(bashThread);
		bashThread.setStepping(false);

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

	public IValue getValue(String expression, IDebugElement context) throws Exception {
		if (!(context instanceof BashStackFrame)) {
			return null;
		}
		BashStackFrame frame = (BashStackFrame) context;
		IValue value = findValue(expression, frame.getVariables());
		return value;
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

	public void createFrames() {
		IStackFrame[] framesTemp = new IStackFrame[stack.size()];
		for (int level = 0; level < stack.size(); level++) {
			currentline = stack.get(level).currentline;
			String source = stack.get(level).source;
			DocumentChanges docChanges = debugTarget.getDocumentChangeRegistry().getDocumentChanges(source);
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
			framesTemp[level] = new BashStackFrame(bashThread, source, stack.get(level).name + ":  " + source + "  : line " + currentline, currentline, level, stack.get(level));
		}
		stackFrames = framesTemp;

	}

	public IVariable[] getBashValues(BashStackFrame frame) {
		IVariable[] fVariables = new IVariable[bashConnector.getVariableCount()];
		for (int i = 0; i < fVariables.length; i++) {
			BashNetworkVariableData data = bashConnector.getVariableData(i);
			BashVariable var = new BashVariable(frame, data);
			var.internalVar = data;
			fVariables[i] = var;
		}
		return fVariables;
	}

	public void markBreakPointsToggled() {
		this.breakpointToggled = true;
	}

	public void markTerminated() {
		this.terminate = true;
	}

	public void disconnect() throws IOException {
		if (bashConnector != null) {
			bashConnector.disconnect();
		}

	}

	public void reset() {
		stackFrames = new IStackFrame[0];
	}

}
