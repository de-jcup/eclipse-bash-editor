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
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

import de.jcup.basheditor.debug.BashNetworkVariableData;

public class BashVariable extends AbstractBashDebugElement implements IVariable, Comparable<BashVariable> {

	private String name;
	private BashValue value;
	private BashStackFrame frame;
	private BashNetworkVariableData data;
	private String lowerCaseName;

	public BashVariable(BashStackFrame frame, BashNetworkVariableData data) {
		super((BashDebugTarget) frame.getDebugTarget());
		
		this.data = data;
		this.frame = frame;
		this.name = data.getName();
		this.lowerCaseName = (""+this.name).toLowerCase();
		this.value = new BashValue((BashDebugTarget) frame.getDebugTarget(), data.getStringValue());
		
		if (data.isArray()) {
			value.containedVariables = new BashVariable[data.getArraySize()];
			for (int i = 0; i < data.getArraySize(); i++) {
				BashVariable arrayVariable = new BashVariable(frame, name+"["+i+"]", data.getStringValue(i));
				value.containedVariables[i] = arrayVariable;
			}
		}
	}

	private BashVariable(BashStackFrame frame, String name, String value) {
		super((BashDebugTarget) frame.getDebugTarget());
		this.frame = frame;
		this.name = name;
		this.value = new BashValue((BashDebugTarget) frame.getDebugTarget(), value);
	}

	public IValue getValue() throws DebugException {
		return value;
	}

	public String getName() throws DebugException {
		return name;
	}

	public String getReferenceTypeName() throws DebugException {
		return "";
	}

	public boolean hasValueChanged() throws DebugException {
		return false;
	}

	public void setValue(String expression) throws DebugException {
	}

	public void setValue(IValue value) throws DebugException {
	}

	public boolean supportsValueModification() {
		return false;
	}

	public boolean verifyValue(String expression) throws DebugException {
		return false;
	}

	public boolean verifyValue(IValue value) throws DebugException {
		return false;
	}
	
	public int compareTo(BashVariable o) {
		if (o == null) {
			return 1;
		}
		return lowerCaseName.compareTo(o.lowerCaseName);
	}

}
