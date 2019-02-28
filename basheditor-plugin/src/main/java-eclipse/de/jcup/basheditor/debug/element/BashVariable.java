package de.jcup.basheditor.debug.element;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

import de.jcup.basheditor.BashEditorActivator;
import de.jcup.basheditor.debug.BashNetworkVariableData;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class BashVariable extends AbstractBashDebugElement implements IVariable, Comparable<BashVariable> {

	private String fName;
	private BashValue fValue;
	public BashStackFrame fFrame;
	public BashNetworkVariableData internalVar;

	public BashVariable(BashStackFrame frame, BashNetworkVariableData internVar) {
		super((BashDebugTarget) frame.getDebugTarget());
		fFrame = frame;
		fName = internVar.getName();
		fValue = new BashValue((BashDebugTarget) frame.getDebugTarget(), internVar.getStringValue());
		internalVar = internVar;
		fValue.fVariable = this;
		if (internVar.isArray()) {
			fValue.vars = new BashVariable[internVar.getArraySize()];
			for (int i = 0; i < internVar.getArraySize(); i++) {
				fValue.vars[i] = new BashVariable(frame, internVar.getArrayKey(i), internVar.getStringValue(i));
			}
		}
	}

	public BashVariable(BashStackFrame frame, String name, String value) {
		super((BashDebugTarget) frame.getDebugTarget());
		fFrame = frame;
		fName = name;
		fValue = new BashValue((BashDebugTarget) frame.getDebugTarget(), value);
	}

	public IValue getValue() throws DebugException {
		return fValue;
	}

	public String getName() throws DebugException {
		return fName;
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
		/* handle double */
		try {
			/* FIXME Albert: Here its double, on another location only integer is handled (variable values))*/
			Double d_this = Double.parseDouble(getName());
			Double d = Double.parseDouble(o.getName());
			return d_this.compareTo(d);
		} catch (Exception e) {
		}

		try {
			String lowerCaseName = getName().toLowerCase();
			return lowerCaseName.compareTo(o.getName().toLowerCase());
		} catch (Exception e) {
			EclipseUtil.logError("Was not able to terminate!k", e, BashEditorActivator.getDefault());

		}
		return 0;
	}

}
