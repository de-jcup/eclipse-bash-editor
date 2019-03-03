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
