package de.jcup.basheditor;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import static de.jcup.basheditor.AssertSimpleWordCodeCompletionResult.*;
public class SimpleWordCodeCompletionTest {

	private SimpleWordCodeCompletion completionToTest;

	@Before
	public void before(){
		completionToTest = new SimpleWordCodeCompletion();
	}
	
	@Test
	public void space_albert_likes_automated_testing__offset_0_results_in_albert_likes_automated_testing() {
		/* execute */
		Set<String> result = completionToTest.calculate(" albert likes automated testing", 0);
		
		/* test */
		assertResult(result).hasResults("albert","likes","automated","testing");
		
	}
	
	@Test
	public void space_albert_space_albert_space_space_albert__offset_0_results_in_albert() {
		/* execute */
		Set<String> result = completionToTest.calculate(" albert albert  albert", 0);
		
		/* test */
		assertResult(result).hasResults("albert");
		
	}
	
	@Test
	public void newline_albert_newline_albert_newline_newline_albert__offset_0_results_in_albert() {
		/* execute */
		Set<String> result = completionToTest.calculate(" albert\nalbert\nalbert", 0);
		
		/* test */
		assertResult(result).hasResults("albert");
		
	}
}
