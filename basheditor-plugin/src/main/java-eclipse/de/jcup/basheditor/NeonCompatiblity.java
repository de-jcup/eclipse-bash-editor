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
