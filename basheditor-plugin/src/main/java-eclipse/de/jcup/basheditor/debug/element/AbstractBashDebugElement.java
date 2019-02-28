package de.jcup.basheditor.debug.element;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;

import de.jcup.basheditor.debug.BashDebugConstants;

public abstract class AbstractBashDebugElement extends PlatformObject implements IDebugElement {

	private BashDebugTarget bashDebugTarget;

	public AbstractBashDebugElement(BashDebugTarget target) {
		bashDebugTarget = target;
	}
	
	public BashDebugTarget getBashDebugTarget() {
		return bashDebugTarget;
	}
	
	public String getModelIdentifier() {
		return BashDebugConstants.BASH_DEBUG_MODEL_ID;
	}

	public final IDebugTarget getDebugTarget() {
		return getBashDebugTarget();
	}

	public ILaunch getLaunch() {
		return getDebugTarget().getLaunch();
	}

	@SuppressWarnings("unchecked")
	public <T> T getAdapter(Class<T> adapter) {
		if (adapter == IDebugElement.class) {
			return (T) this;
		}
		return super.getAdapter(adapter);
	}

	
}
