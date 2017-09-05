package de.jcup.basheditor.scriptmodel;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class BashScriptModelTest {
	private BashScriptModel modelToTest;

	@Before
	public void before() {
		modelToTest = new BashScriptModel();
	}

	@Test
	public void has_errors_returns_true_when_one_error_is_added() {
		/* execute */
		modelToTest.getErrors().add(new BashError(100, "buh"));
		
		/* test */
		assertTrue(modelToTest.hasErrors());
	}
	
	@Test
	public void has_errors_returns_false_when_no_error_is_added() {
		/* test */
		assertFalse(modelToTest.hasErrors());
	}
	
}
