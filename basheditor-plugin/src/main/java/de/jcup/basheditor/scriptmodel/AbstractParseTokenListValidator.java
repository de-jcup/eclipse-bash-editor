package de.jcup.basheditor.scriptmodel;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractParseTokenListValidator implements BashScriptValidator<List<ParseToken>>{

	@Override
	public final List<ValidationResult> validate(List<ParseToken> toValidate) {
		List<ValidationResult> result = new ArrayList<ValidationResult>();
		if (toValidate==null || toValidate.size()==0){
			return result;
		}
		doValidation(toValidate, result);
		return result;
	}

	/**
	 * Do validation
	 * @param tokens - not <code>null</code> and not empty
	 * @param result - not <b>null</b>
	 */
	protected abstract void doValidation(List<ParseToken> tokens, List<ValidationResult> result);

}
