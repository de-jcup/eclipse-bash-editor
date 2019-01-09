/*
 * Copyright 2018 Albert Tregnaghi
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

public class LineIsBashSheBangValidator {

	public boolean isValid(String line) {
		if (line == null) {
			return false;
		}
		if (line.isEmpty()) {
			return false;
		}
		if (!line.startsWith("#!")) {
			return false;
		}
		if (line.indexOf("bash") != -1 ) {
			return true;
		}
		if (line.endsWith(" sh")) {
            return true;
        }
		return false;
	}
}
