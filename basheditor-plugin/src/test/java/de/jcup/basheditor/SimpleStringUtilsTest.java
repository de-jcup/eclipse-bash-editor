package de.jcup.basheditor;

import static org.junit.Assert.*;

import org.junit.Test;

public class SimpleStringUtilsTest {

	@Test
	public void null_equals_null__is_true() {
		assertTrue(SimpleStringUtils.equals(null, null));
	}
	
	@Test
	public void a_equals_a__is_true() {
		assertTrue(SimpleStringUtils.equals("a", "a"));
	}
	
	@Test
	public void a_equals_null__is_false() {
		assertFalse(SimpleStringUtils.equals("a", null));
	}
	
	@Test
	public void a_equals_b__is_false() {
		assertFalse(SimpleStringUtils.equals("a","b"));
	}
	
	@Test
	public void b_equals_a__is_false() {
		assertFalse(SimpleStringUtils.equals("b","a"));
	}
	
	@Test
	public void null_equals_a__is_false() {
		assertFalse(SimpleStringUtils.equals(null,"a"));
	}

}
