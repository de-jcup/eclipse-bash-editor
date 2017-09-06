package de.jcup.basheditor.scriptmodel;

import java.util.List;

public class DoEndsWithDoneValidator extends AbstractParseTokenListValidator {

	@Override
	protected void doValidation(List<ParseToken> tokens, List<ValidationResult> result) {
		ParseToken inspectedUnchainedDoToken = null;
		int countOfDo = 0;
		int countOfDone = 0;
		for (ParseToken token : tokens) {
			if (token==null){
				continue;
			}
			if (inspectedUnchainedDoToken == null) {
				inspectedUnchainedDoToken = token;
			}
			if (isDo(token)) {
				if (countOfDo == countOfDone) {
					/*
					 * former do was closed - so set this token as last
					 * inspected unchained token
					 */
					inspectedUnchainedDoToken = token;
				}
				countOfDo++;
			} else if (isDone(token)) {
				if (countOfDo > 0) {
					countOfDone++;
				}
			}
		}
		if (countOfDo!=countOfDone){
			if (inspectedUnchainedDoToken!=null){
				BashError error = new BashError(inspectedUnchainedDoToken.start,inspectedUnchainedDoToken.end,"This DO is not correct closed. A DONE is missing");
				result.add(error);
			}
		}
	}

	private boolean isDo(ParseToken token) {
		return "do".equalsIgnoreCase(token.text);
	}

	private boolean isDone(ParseToken token) {
		return "done".equalsIgnoreCase(token.text);
	}

}
