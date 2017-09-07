package de.jcup.basheditor.scriptmodel;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class IfEndsWithFiValidatorTest {

	private IfEndsWithFiValidator validatorToTest;
	private ArrayList<ParseToken> tokens;

	@Before
	public void before() {
		validatorToTest = new IfEndsWithFiValidator();
		tokens = new ArrayList<>();
	}
	

	@Test
	public void if_something_fi_has_no_problems() {
		/* prepare */
		tokens.add(new ParseToken("if"));
		tokens.add(new ParseToken("something"));
		tokens.add(new ParseToken("fi"));

		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);

		/* test */
		assertEquals(0, results.size());
	}

	@Test
	public void if_something_fine_has_problem() {
		/* prepare */
		tokens.add(new ParseToken("if"));
		tokens.add(new ParseToken("something"));
		tokens.add(new ParseToken("fine"));

		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);

		/* test */
		assertEquals(1, results.size());
		ValidationResult validationResult = results.iterator().next();
		assertEquals(ValidationResult.Type.ERROR, validationResult.getType());
	}

	@Test
	public void if_something_if_something2_fine_has_problem() {
		/* prepare */
		tokens.add(new ParseToken("if"));
		tokens.add(new ParseToken("something"));
		tokens.add(new ParseToken("if"));
		tokens.add(new ParseToken("fi"));

		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);

		/* test */
		assertEquals(1, results.size());
		ValidationResult validationResult = results.iterator().next();
		assertEquals(ValidationResult.Type.ERROR, validationResult.getType());
	}
}
