/*
 * Copyright 2016 Albert Tregnaghi
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
package de.jcup.basheditor.process;

public interface OutputHandler {

	/**
	 * Output outputHandler does nothing
	 */
	public static final NoOutputHandler NO_OUTPUT = new NoOutputHandler();
	public static final StringOutputHandler STRING_OUTPUT = new StringOutputHandler();

	void output(String line);

	
	public static class NoOutputHandler implements OutputHandler {

		private NoOutputHandler() {
		}

		@Override
		public void output(String line) {
		}
	}
	
	public static class StringOutputHandler implements OutputHandler {
		
		private String fullOutput;

		private StringOutputHandler() {
			fullOutput = "";
		}

		@Override
		public void output(String line) {
			fullOutput += line;
		}
		
		public String getFullOutput() {
			return fullOutput;
		}
	}

}
