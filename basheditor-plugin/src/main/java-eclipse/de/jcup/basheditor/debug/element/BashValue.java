package de.jcup.basheditor.debug.element;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

public class BashValue extends AbstractBashDebugElement implements IValue {

	IVariable[] containedVariables = new IVariable[0];
	private String value;

	public BashValue(BashDebugTarget target, String value) {
		super(target);
		this.value = value;
	}

	public String getReferenceTypeName() throws DebugException {
		try {
			Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return "text";
		}
		return "integer";
	}

	public String getValueString() throws DebugException {
		return value;
	}

	public boolean isAllocated() throws DebugException {
		return true;
	}

	public IVariable[] getVariables() throws DebugException {
		return containedVariables;
	}

	public boolean hasVariables() throws DebugException {
		if (containedVariables.length > 0) {
			return true;
		}
		return false;
	}
}
