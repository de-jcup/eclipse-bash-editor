/*
 * Copyright 2017 Albert Tregnaghi
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
package de.jcup.basheditor.script.parser.validator;

import java.util.List;

import de.jcup.basheditor.script.BashError;
import de.jcup.basheditor.script.ValidationResult;
import de.jcup.basheditor.script.parser.ParseToken;

public class DoEndsWithDoneValidator extends AbstractParseTokenListValidator {

	@Override
	protected void doValidation(List<ParseToken> tokens, List<ValidationResult> result) {
		ParseToken inspectedUnchainedDoToken = null;
		int countOfDo = 0;
		int countOfDone = 0;
		for (ParseToken token : tokens) {
			if (token == null) {
				continue;
			}
			if (inspectedUnchainedDoToken == null) {
				inspectedUnchainedDoToken = token;
			}
			if (token.isDo()) {
				if (countOfDo == countOfDone) {
					/*
					 * former do was closed - so set this token as last
					 * inspected unchained token
					 */
					inspectedUnchainedDoToken = token;
				}
				countOfDo++;
			} else if (token.isDone()) {
				if (countOfDo > 0) {
					countOfDone++;
				}
			}
		}
		if (countOfDo != countOfDone) {
			if (inspectedUnchainedDoToken != null) {
				BashError error = new BashError(inspectedUnchainedDoToken.getStart(), inspectedUnchainedDoToken.getEnd(),
						"This 'Do' is not correct closed. A 'Done' is missing");
				result.add(error);
			}
		}
	}

}
