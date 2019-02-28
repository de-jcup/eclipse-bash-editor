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
