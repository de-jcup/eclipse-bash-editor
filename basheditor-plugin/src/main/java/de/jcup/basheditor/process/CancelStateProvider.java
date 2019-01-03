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

/**
 * A state provider for information about an action is canceled or not
 * 
 * @author Albert Tregnaghi
 *
 */
public interface CancelStateProvider {
	public static final NeverCanceled NEVER_CANCELED = new NeverCanceled();

	public boolean isCanceled();

	static class NeverCanceled implements CancelStateProvider {

		private NeverCanceled() {
		}

		@Override
		public boolean isCanceled() {
			return false;
		}

	}

}
