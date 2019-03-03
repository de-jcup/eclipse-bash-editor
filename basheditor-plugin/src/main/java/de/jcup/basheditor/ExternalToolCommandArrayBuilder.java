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

import java.io.File;

public class ExternalToolCommandArrayBuilder {

	private int numKeywordsReplaced = 0;

	public String[] build(String externalToolCall, File editorFile) {
		numKeywordsReplaced = 0;
		String[] ret = externalToolCall.split(" ");

		// detect special placeholder(s):
		for (int i = 0; i < ret.length; i++)
			if (ret[i].equalsIgnoreCase("$filename")) {
				ret[i] = editorFile.toPath().toString();
				numKeywordsReplaced++;
			}

		return ret;
	}

	public int getNumKeywordsReplaced() {
		return numKeywordsReplaced;
	}
}
