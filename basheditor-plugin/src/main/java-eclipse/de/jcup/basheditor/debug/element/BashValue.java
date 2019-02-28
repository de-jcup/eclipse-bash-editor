package de.jcup.basheditor.debug.element;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

public class BashValue extends AbstractBashDebugElement implements IValue {

	public IVariable[] vars = new IVariable[0];
	private String fValue;
	public BashVariable fVariable;

	public BashValue(BashDebugTarget target, String value) {
		super(target);
		fValue = value;
	}

	public String getReferenceTypeName() throws DebugException {
		try {
			Integer.parseInt(fValue);
		} catch (NumberFormatException e) {
			return "text";
		}
		return "integer";
	}

	public String getValueString() throws DebugException {
		return fValue;
	}

	public boolean isAllocated() throws DebugException {
		return true;
	}

	public IVariable[] getVariables() throws DebugException {
		return vars;
	}

	public boolean hasVariables() throws DebugException {
		if (vars.length > 0) {
			return true;
		}
		return false;
	}
}
