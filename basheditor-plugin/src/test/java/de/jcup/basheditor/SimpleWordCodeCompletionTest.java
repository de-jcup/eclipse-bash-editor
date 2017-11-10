package de.jcup.basheditor;

import static de.jcup.basheditor.AssertSimpleWordCodeCompletionResult.*;
import static org.junit.Assert.*;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;
public class SimpleWordCodeCompletionTest {

	private SimpleWordCodeCompletion completionToTest;

	@Test
	public void add_alpha__source_x_space_space_albert__calculate_albert_alpha_x_on_index_2(){
		/* execute */
		completionToTest.add("alpha");
		Set<String> result = completionToTest.calculate("x   albert", 2);
		
		/* test */
		assertResult(result).hasResults("albert","alpha","x");
	}
	
	@Test
	public void add_alpha_space_space__source_x_space_space_albert__calculate_albert_alpha_x_on_index_2(){
		/* execute */
		completionToTest.add("alpha  ");
		Set<String> result = completionToTest.calculate("x   albert", 2);
		
		/* test */
		assertResult(result).hasResults("albert","alpha","x");
	}
	
	@Test
	public void add_alpha_and_y__source_x_space_space_albert__calculate_albert_alpha_x_on_index_2(){
		/* execute */
		completionToTest.add("alpha  ");
		completionToTest.add("y");
		Set<String> result = completionToTest.calculate("x   albert", 2);
		
		/* test */
		assertResult(result).hasResults("albert","alpha","x","y");
	}
	
	@Test
	public void a_space_albert_likes_automated_testing__offset_1_calculates_albert__automated_() {
		/* execute */
		Set<String> result = completionToTest.calculate("a albert likes automated testing", 1);
		
		/* test */
		assertResult(result).hasResults("albert","automated");
		
	}
	
	@Before
	public void before(){
		completionToTest = new SimpleWordCodeCompletion();
	}
	
	@Test
	public void getTextbefore__a_b_c__index_0_text_before_is_empty(){
		assertEquals("", completionToTest.getTextbefore("a b c", 0));
	}
	
	@Test
	public void getTextbefore__a_b_c__index_1_text_before_is_a(){
		assertEquals("a", completionToTest.getTextbefore("a b c", 1));
	}
	
	@Test
	public void getTextbefore__a_b_c__index_2_text_before_is_empty(){
		assertEquals("", completionToTest.getTextbefore("a b c", 2));
	}

	@Test
	public void getTextbefore__a_b_c__index_3_text_before_is_b(){
		assertEquals("b", completionToTest.getTextbefore("a b c", 3));
	}
	
	@Test
	public void getTextbefore__ax_b_c__index_2_text_before_is_ax(){
		assertEquals("ax", completionToTest.getTextbefore("ax b c", 2));
	}
	
	@Test
	public void getTextbefore_returns_empty_string_when_index_is_0(){
		assertEquals("",completionToTest.getTextbefore("abc", 0));
	}
	
	@Test
	public void getTextbefore_returns_empty_string_when_index_is_n1(){
		assertEquals("",completionToTest.getTextbefore("abc", -1));
	}
	
	@Test
	public void getTextbefore_returns_empty_string_when_source_empty(){
		assertEquals("",completionToTest.getTextbefore("", 0));
		assertEquals("",completionToTest.getTextbefore("", 1));
		assertEquals("",completionToTest.getTextbefore("", -1));
		
		assertEquals("",completionToTest.getTextbefore(" ", 0));
		assertEquals("",completionToTest.getTextbefore(" ", 1));
		assertEquals("",completionToTest.getTextbefore(" ", -1));
	}
	
	@Test
	public void getTextbefore_returns_empty_string_when_source_null(){
		assertEquals("",completionToTest.getTextbefore(null, 0));
		assertEquals("",completionToTest.getTextbefore(null, 1));
		assertEquals("",completionToTest.getTextbefore(null, -1));
	}
	
	@Test
	public void newline_albert_newline_albert_newline_newline_albert__offset_0_calculates_albert() {
		/* execute */
		Set<String> result = completionToTest.calculate(" albert\nalbert\nalbert", 0);
		
		/* test */
		assertResult(result).hasResults("albert");
		
	}
	
	@Test
	public void set_a_albert__wanted_a_filters_set_to_albert_only__means_wanted_is_not_part_of_result(){
		/* prepare */
		TreeSet<String> origin = new TreeSet<>();
		origin.add("albert");
		origin.add("a");
		
		TreeSet<String> expected = new TreeSet<>();
		expected.add("albert");
		
		/* execute +test */
		assertEquals(expected, completionToTest.filteredSet(origin,"a"));
	}
	
	@Test
	public void set_albert_nicole_sarah_andreas__wanted_a_filters_set_to_albert_andreas(){
		/* prepare */
		TreeSet<String> origin = new TreeSet<>();
		origin.add("albert");
		origin.add("nicole");
		origin.add("sarah");
		origin.add("andreas");
		
		TreeSet<String> expected = new TreeSet<>();
		expected.add("albert");
		expected.add("andreas");
		
		/* execute +test */
		assertEquals(expected, completionToTest.filteredSet(origin,"a"));
	}
	
	@Test
	public void space_albert_likes_automated_testing__offset_0_calculates_albert_likes_automated_testing() {
		/* execute */
		Set<String> result = completionToTest.calculate(" albert likes automated testing", 0);
		
		/* test */
		assertResult(result).hasResults("albert","likes","automated","testing");
		
	}
	
	@Test
	public void space_albert_space_albert_space_space_albert__offset_0_calculates_albert() {
		/* execute */
		Set<String> result = completionToTest.calculate(" albert albert  albert", 0);
		
		/* test */
		assertResult(result).hasResults("albert");
		
	}
}
