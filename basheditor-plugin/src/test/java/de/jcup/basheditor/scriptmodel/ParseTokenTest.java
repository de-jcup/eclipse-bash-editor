package de.jcup.basheditor.scriptmodel;

import static org.junit.Assert.*;

import org.junit.Test;

public class ParseTokenTest {

	@Test
	public void token_starts_with_hash_space_xyz_is_comment() {
		assertTrue(new ParseToken("# xyz").isComment());
	}
	
	@Test
	public void token_starts_with_hash_xyz_is_comment() {
		assertTrue(new ParseToken("#xyz").isComment());
	}
	
	@Test
	public void token_starts_with_a_is_no_comment() {
		assertFalse(new ParseToken("axyz").isComment());
	}
	
	@Test
	public void single_string_xxx_is_string() {
		assertTrue(new ParseToken("'xxx'").isString());
	}
	
	@Test
	public void double_ticked_string_xxx_is_string() {
		assertTrue(new ParseToken("`xxx`").isString());
	}
	
	@Test
	public void double_string_xxx_is_string() {
		assertTrue(new ParseToken("\"xxx\"").isString());
	}
	
	@Test
	public void function_is_functionKeyword() {
		assertTrue(new ParseToken("function").isFunctionKeyword());
	}
	
	@Test
	public void functions_is_NOT_functionKeyword() {
		assertFalse(new ParseToken("functions").isFunctionKeyword());
	}
	
	@Test
	public void function_is_NOT_functionName() {
		assertFalse(new ParseToken("function").isFunctionName());
	}
	
	@Test
	public void xyz_is_NOT_functionName() {
		assertFalse(new ParseToken("xyz").isFunctionName());
	}
	
	@Test
	public void xyz_followed_by_open_and_close_bracket_is_functionName() {
		assertTrue(new ParseToken("xyz()").isFunctionName());
	}
	
	@Test
	public void only_open_and_close_bracket_is_NOT_functionName() {
		assertFalse(new ParseToken("()").isFunctionName());
	}
	
	@Test
	public void xyz_getTextAsFunctionName_returns_xyz() {
		assertEquals("xyz", new ParseToken("xyz").getTextAsFunctionName());
	}
	
	@Test
	public void xyz_open_close_bracketgetTextAsFunctionName_returns_xyz() {
		assertEquals("xyz", new ParseToken("xyz()").getTextAsFunctionName());
	}
}