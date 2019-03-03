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

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IWatchExpressionDelegate;
import org.eclipse.debug.core.model.IWatchExpressionListener;
import org.eclipse.debug.core.model.IWatchExpressionResult;

import de.jcup.basheditor.BashEditorActivator;
import de.jcup.basheditor.debug.element.AbstractBashDebugElement;
import de.jcup.basheditor.debug.element.BashDebugTarget;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class BashWatchExpressionDelegate implements IWatchExpressionDelegate {

	public void evaluateExpression(String expression, IDebugElement context, IWatchExpressionListener listener) {
		listener.watchEvaluationFinished(new BashWatchExpressionResult(expression, context));
	}

	private class BashWatchExpressionResult implements IWatchExpressionResult {

		private String expression;
		private IValue value;
		private String errorMessage;
		private Exception exception;

		BashWatchExpressionResult(String expression, IDebugElement context) {
			this.expression = expression;
			if (!(context instanceof AbstractBashDebugElement)) {
				EclipseUtil.logError("Context is not a bash ebug element!", null, BashEditorActivator.getDefault());
				return;
			}
			AbstractBashDebugElement bashDebugElement = (AbstractBashDebugElement) context;
			BashDebugTarget target = bashDebugElement.getBashDebugTarget();
			if (target == null) {
				EclipseUtil.logError("No target defined!", null, BashEditorActivator.getDefault());
				return;
			}
			if (target.isTerminated()) {
				return;
			}
			try {
				value = target.getValue(expression, context);
				if (value == null) {
					errorMessage = "Not found";
				}
			} catch (Exception e) {
				errorMessage = e.getMessage();
				exception = e;
			}

		}

		public IValue getValue() {
			return value;
		}

		public boolean hasErrors() {
			return errorMessage != null;
		}

		public String[] getErrorMessages() {
			return new String[] { errorMessage };
		}

		public String getExpressionText() {
			return expression;
		}

		public DebugException getException() {
			if (exception instanceof DebugException) {
				return (DebugException) exception;
			}
			return null;
		}
	}
}
