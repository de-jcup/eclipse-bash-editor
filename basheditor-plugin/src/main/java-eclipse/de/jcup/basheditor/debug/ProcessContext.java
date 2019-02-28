package de.jcup.basheditor.debug;

import java.util.Vector;

import de.jcup.basheditor.debug.BashDebugger.StackElement;

class ProcessContext {
	/**
	 * 
	 */
	private final BashDebugger bashDebugger;

	/**
	 * @param bashDebugger
	 */
	ProcessContext(BashDebugger bashDebugger) {
		this.bashDebugger = bashDebugger;
	}

	private Vector<StackElement> stack = new Vector<StackElement>();
	private BashNetworkVariableData bashLineNumber;
	private BashNetworkVariableData functionName;
	private BashNetworkVariableData bashSource;
	private boolean stop2;
	
	public void update(BashNetworkConnector connctor) {
		bashLineNumber = this.bashDebugger.bashConnector.getBashLineNumber();
		functionName = this.bashDebugger.bashConnector.getFunctionName();
		bashSource = this.bashDebugger.bashConnector.getBashSource();
	}
	
	public BashNetworkVariableData getBashLineNumber() {
		return bashLineNumber;
	}
	
	public BashNetworkVariableData getFunctionName() {
		return functionName;
	}
	
	public BashNetworkVariableData getBashSource() {
		return bashSource;
	}
	
	public void clearStack() {
		stack.clear();
	}
	public void addStack(StackElement stackElement) {
		stack.add(stackElement);
	}
	public int getStackSize() {
		return stack.size();
	}
	public StackElement getStack(int level) {
		return stack.get(level);
	}

	public void stop() {
		this.stop2=true;
	}

	public boolean isStopped() {
		return stop2;
	}

	public void go() {
		stop2=false;
	}
}