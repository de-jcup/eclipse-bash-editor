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
