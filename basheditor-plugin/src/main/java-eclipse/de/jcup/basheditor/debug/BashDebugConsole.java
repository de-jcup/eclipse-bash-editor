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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class BashDebugConsole extends MessageConsole {

	public BashDebugConsole(String name, ImageDescriptor imageDescriptor) {
		super(name, imageDescriptor);
	}

	@Override
	public MessageConsoleStream newMessageStream() {
		return super.newMessageStream();
	}
	
	private static BashDebugConsole get() {
		return BashDebugConsoleFactory.INSTANCE.getConsole();
	}
	
	public static void println(String message) {
		get().newMessageStream().println(message);
	}
	
	public static void clear() {
		get().clearConsole();
	}

}