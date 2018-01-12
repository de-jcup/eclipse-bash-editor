package de.jcup.basheditor.script.parser.validator;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import de.jcup.basheditor.script.BashScriptValidator;
import de.jcup.basheditor.script.ValidationResult;
import de.jcup.basheditor.script.parser.ParseToken;
import de.jcup.basheditor.script.parser.TestParseToken;

public class AssertTokenValidator {

	private BashScriptValidator<List<ParseToken>> validator;
	private List<ParseToken> tokens;
	
	public static AssertTokenValidator assertThat(BashScriptValidator<List<ParseToken>> validator){
		return new AssertTokenValidator(validator);
	}

	private AssertTokenValidator(BashScriptValidator<List<ParseToken>> validator){
		assertNotNull(validator);
		this.validator=validator;
		this.tokens = new ArrayList<>();
	}
	
	public AssertTokenValidator withTokens(String ... tokens){
		for (String token: tokens){
			this.tokens.add(new TestParseToken(token));
		}
		return this;
	}
	
	public AssertTokenValidator isValid(){
		List<ValidationResult> results = validator.validate(tokens);
		if (! results.isEmpty()){
			StringBuilder sb = new StringBuilder();
			for (ValidationResult result: results){
				sb.append("\n");
				sb.append(result.getMessage());
			}
			fail("Expected NO validation failures/results but there are some:"+sb.toString());
		}
		return this;
	}
	
	public AssertTokenValidator isNotValid(){
		List<ValidationResult> results = validator.validate(tokens);
		if (results.isEmpty()){
			fail("Expected validation failures/results but there are none!");
		}
		return this;
	}

	public AssertTokenValidator hasValidationErrors(int expectedValidationResults) {
		List<ValidationResult> results = validator.validate(tokens);
		assertEquals("Validation amount not as expected!", expectedValidationResults, results.size());
		return this;
	}
	
}
