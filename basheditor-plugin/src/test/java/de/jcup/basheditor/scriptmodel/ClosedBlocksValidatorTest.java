package de.jcup.basheditor.scriptmodel;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ClosedBlocksValidatorTest {
	private ClosedBlocksValidator validatorToTest;
	private List<ParseToken> tokens;

	@Before
	public void before(){
		validatorToTest = new ClosedBlocksValidator();
		tokens = new ArrayList<>();
	}
	
	/* FIXME ATR, 06.09.2017:  write more test cases + special:inside comments and strings {may not be calculated!*/
	@Test
	public void missing_close_part_detected() {
		/* prepare */
		tokens.add(new ParseToken("{"));
		tokens.add(new ParseToken("{"));
		tokens.add(new ParseToken("}"));
		
		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);
		
		/* test */
		assertEquals(1,results.size());
	}
	
	@Test
	public void missing_open_part_detected() {
		/* prepare */
		tokens.add(new ParseToken("{"));
		tokens.add(new ParseToken("}"));
		tokens.add(new ParseToken("}"));
		
		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);
		
		/* test */
		assertEquals(1,results.size());
	}
	
	@Test
	public void no_missing_close_parts_have_no_error() {
		/* prepare */
		tokens.add(new ParseToken("{"));
		tokens.add(new ParseToken("{"));
		tokens.add(new ParseToken("}"));
		tokens.add(new ParseToken("}"));
		
		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);
		
		/* test */
		assertEquals(0,results.size());
	}

}
