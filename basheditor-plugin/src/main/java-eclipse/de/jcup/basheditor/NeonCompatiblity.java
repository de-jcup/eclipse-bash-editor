package de.jcup.basheditor;

import java.util.function.Consumer;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public class NeonCompatiblity {

	/* fallback impl for neon. was introduced with phton rlease, 
	 * origin code: https://github.com/eclipse/eclipse.platform.swt/blob/master/bundles/org.eclipse.swt/Eclipse%20SWT/common/org/eclipse/swt/events/SelectionListener.java */
	public static SelectionListener widgetSelectedAdapter(Consumer<SelectionEvent> c) {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				c.accept(e);
			}
		};
	}
}
