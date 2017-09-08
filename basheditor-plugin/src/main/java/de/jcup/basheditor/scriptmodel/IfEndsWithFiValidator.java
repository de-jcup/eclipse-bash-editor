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
 package de.jcup.basheditor.scriptmodel;

import java.util.List;

public class IfEndsWithFiValidator extends AbstractParseTokenListValidator {

	@Override
	protected void doValidation(List<ParseToken> tokens, List<ValidationResult> result) {
		ParseToken inspectedUnchainedIfToken = null;
		int countOfIf = 0;
		int countOfFi = 0;
		for (ParseToken token : tokens) {
			if (token==null){
				continue;
			}
			if (inspectedUnchainedIfToken == null) {
				inspectedUnchainedIfToken = token;
			}
			if (token.isIf()) {
				if (countOfIf == countOfFi) {
					/*
					 * former if was closed - so set this token as last
					 * inspected unchained token
					 */
					inspectedUnchainedIfToken = token;
				}
				countOfIf++;
			} else if (token.isFi()) {
				if (countOfIf > 0) {
					countOfFi++;
				}
			}
		}
		if (countOfIf!=countOfFi){
			if (inspectedUnchainedIfToken!=null){
				BashError error = new BashError(inspectedUnchainedIfToken.start,inspectedUnchainedIfToken.end,"This 'if' statement is not correct closed. A 'fi' is missing");
				result.add(error);
			}
		}
	}

}
