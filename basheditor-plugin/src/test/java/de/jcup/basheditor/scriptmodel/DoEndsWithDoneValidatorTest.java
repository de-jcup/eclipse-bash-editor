package de.jcup.basheditor.scriptmodel;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class DoEndsWithDoneValidatorTest {

	private DoEndsWithDoneValidator validatorToTest;
	private ArrayList<ParseToken> tokens;
	
	@Before
	public void before(){
		validatorToTest = new DoEndsWithDoneValidator();
		tokens = new ArrayList<>();
	}
	
	@Test
	public void do_something_done__has_no_problems() {
		/* prepare */
		tokens.add(new ParseToken("do"));
		tokens.add(new ParseToken("something"));
		tokens.add(new ParseToken("done"));
		
		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);
		
		/* test */
		assertEquals(0,results.size());
	}
	
	@Test
	public void do_something_doxne__has_problem() {
		/* prepare */
		tokens.add(new ParseToken("do"));
		tokens.add(new ParseToken("something"));
		tokens.add(new ParseToken("doxne"));
		
		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);
		
		/* test */
		assertEquals(1,results.size());
		ValidationResult validationResult = results.iterator().next();
		assertEquals(ValidationResult.Type.ERROR, validationResult.getType());
	}

	
	@Test
	public void do_something_has_problem() {
		/* prepare */
		tokens.add(new ParseToken("do"));
		tokens.add(new ParseToken("something"));
		
		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);
		
		/* test */
		assertEquals(1,results.size());
		ValidationResult validationResult = results.iterator().next();
		assertEquals(ValidationResult.Type.ERROR, validationResult.getType());
	}
	
	
	@Test
	public void do_something_do_something2_done_has_problem() {
		/* prepare */
		tokens.add(new ParseToken("do"));
		tokens.add(new ParseToken("something"));
		tokens.add(new ParseToken("do"));
		tokens.add(new ParseToken("something2"));
		tokens.add(new ParseToken("done"));
		
		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);
		
		/* test */
		assertEquals(1,results.size());
		ValidationResult validationResult = results.iterator().next();
		assertEquals(ValidationResult.Type.ERROR, validationResult.getType());
	}
	
	@Test
	public void do_something_done_something2_done_has_problem() {
		/* prepare */
		tokens.add(new ParseToken("do"));
		tokens.add(new ParseToken("something"));
		tokens.add(new ParseToken("done"));
		tokens.add(new ParseToken("something2"));
		tokens.add(new ParseToken("done"));
		
		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);
		
		/* test */
		assertEquals(1,results.size());
		ValidationResult validationResult = results.iterator().next();
		assertEquals(ValidationResult.Type.ERROR, validationResult.getType());
	}
	
	@Test
	public void do_x_done_do_y_done_do_done_done_do__has_problem() {
		/* prepare */
		tokens.add(new ParseToken("do"));
		tokens.add(new ParseToken("x"));
		tokens.add(new ParseToken("done"));
		tokens.add(new ParseToken("do"));
		tokens.add(new ParseToken("y"));
		tokens.add(new ParseToken("done"));
		tokens.add(new ParseToken("do"));
		tokens.add(new ParseToken("done"));
		tokens.add(new ParseToken("do"));
		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);
		
		/* test */
		assertEquals(1,results.size());
		ValidationResult validationResult = results.iterator().next();
		assertEquals(ValidationResult.Type.ERROR, validationResult.getType());
	}
	
	@Test
	public void do_something_do_something2_done_done_has_no_problems() {
		/* prepare */
		tokens.add(new ParseToken("do"));
		tokens.add(new ParseToken("something"));
		tokens.add(new ParseToken("do"));
		tokens.add(new ParseToken("something2"));
		tokens.add(new ParseToken("done"));
		tokens.add(new ParseToken("done"));
		
		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);
		
		/* test */
		assertEquals(0,results.size());
	}
	
	@Test
	public void do_something_simple_string_with_do_something2_done_done_has_problems() {
		/* prepare */
		tokens.add(new ParseToken("do"));
		tokens.add(new ParseToken("something"));
		tokens.add(new ParseToken("'do'"));
		tokens.add(new ParseToken("something2"));
		tokens.add(new ParseToken("done"));
		tokens.add(new ParseToken("done"));
		
		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);
		
		/* test */
		assertEquals(1,results.size());
		ValidationResult validationResult = results.iterator().next();
		assertEquals(ValidationResult.Type.ERROR, validationResult.getType());
	}
	
}
