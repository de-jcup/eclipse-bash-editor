package de.jcup.basheditor.scriptmodel;

import java.util.List;

public interface BashScriptValidator<T> {

	/**
	 * Validates
	 * @param toValidate part which has to be validated
	 * @return list containing validation data or an empty list. Is never <code>null</code>
	 */
	public List<ValidationResult> validate(T toValidate);
	
}
