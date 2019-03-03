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