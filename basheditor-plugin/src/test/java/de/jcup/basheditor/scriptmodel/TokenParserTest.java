package de.jcup.basheditor.scriptmodel;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TokenParserTest {

	private TokenParser parserToTest;

	@Before
	public void before(){
		parserToTest = new TokenParser();
	}
	
	@Test
	public void abc_def_ghji_is_parsed_as_three_tokens(){
		List<ParseToken> tokens = parserToTest.parse("abc def ghji");
		
		assertNotNull(tokens);
		assertEquals(3,tokens.size());
		
		Iterator<ParseToken> it = tokens.iterator();
		ParseToken token1 = it.next();
		ParseToken token2 = it.next();
		ParseToken token3 = it.next();

		assertEquals("abc",token1.text);
		assertEquals("def",token2.text);
		assertEquals("ghji",token3.text);
	}
	
	
	@Test
	public void some_spaces_abc_def_ghji_is_parsed_as_three_tokens(){
		List<ParseToken> tokens = parserToTest.parse("    abc def ghji");
		
		assertNotNull(tokens);
		assertEquals(3,tokens.size());
		
		Iterator<ParseToken> it = tokens.iterator();
		ParseToken token1 = it.next();
		ParseToken token2 = it.next();
		ParseToken token3 = it.next();

		assertEquals("abc",token1.text);
		assertEquals("def",token2.text);
		assertEquals("ghji",token3.text);
	}
	
	@Test
	public void abc_def_ghji_is_parsed_as_three_tokens__and_correct_positions(){
		List<ParseToken> tokens = parserToTest.parse("abc def ghji");
		
		assertNotNull(tokens);
		assertEquals(3,tokens.size());
		
		Iterator<ParseToken> it = tokens.iterator();
		ParseToken token1 = it.next();
		ParseToken token2 = it.next();
		ParseToken token3 = it.next();

		assertEquals(0,token1.start);
		assertEquals(4,token2.start);
		assertEquals(8,token3.start);
	}
	
	@Test
	public void comment1_returns_no_tokens(){
		List<ParseToken> tokens = parserToTest.parse("#comment1");
		
		assertNotNull(tokens);
		assertEquals(0,tokens.size());
		
	}
	
	@Test
	public void comment1_new_line_returns_no_tokens(){
		List<ParseToken> tokens = parserToTest.parse("#comment1\n");
		
		assertNotNull(tokens);
		assertEquals(0,tokens.size());
		
	}
	
	@Test
	public void comment1_new_line_function_space_name_returns_2_tokens_function_and_name(){
		List<ParseToken> tokens = parserToTest.parse("#comment1\nfunction name");
		
		assertNotNull(tokens);
		assertEquals(2,tokens.size());
		
		Iterator<ParseToken> it = tokens.iterator();
		ParseToken token1 = it.next();
		ParseToken token2 = it.next();

		assertEquals("function",token1.text);
		assertEquals("name",token2.text);
	}
	
	@Test
	public void comment1_new_line_function_space_name_directly_followed_by_brackets_returns_2_tokens_function_and_name(){
		List<ParseToken> tokens = parserToTest.parse("#comment1\nfunction name()");
		
		assertNotNull(tokens);
		assertEquals(2,tokens.size());
		
		Iterator<ParseToken> it = tokens.iterator();
		ParseToken token1 = it.next();
		ParseToken token2 = it.next();

		assertEquals("function",token1.text);
		assertEquals("name()",token2.text);
	}
	
	@Test
	public void test_code_tokens_not_filtered_per_default(){
		/* test */
		assertFalse(parserToTest.isFilterCodeTokens());
	}
	
	@Test
	public void test_comment_tokens_filtered_per_default(){
		/* test */
		assertTrue(parserToTest.isFilterCommentTokens());
	}
	
	
	@Test
	public void five_spaces_comment1_new_line_function_space_name_directly_followed_by_brackets_returns_1_token_comment1_when_codetokens_filtered_and_code_comments_not_filtered(){
		parserToTest.setFilterCodeTokens(true);
		parserToTest.setFilterCommentTokens(false);
		
		List<ParseToken> tokens = parserToTest.parse("     #comment1\nfunction name()");
		
		assertNotNull(tokens);
		assertEquals(1,tokens.size());
		
		Iterator<ParseToken> it = tokens.iterator();
		ParseToken token1 = it.next();

		assertEquals("#comment1",token1.text);
		assertEquals(5,token1.start);
		assertEquals(14,token1.end);
	}
	
	@Test
	public void function1_comment1_function2_comment2_comments_not_filtered_but_code(){
		parserToTest.setFilterCodeTokens(true);
		parserToTest.setFilterCommentTokens(false);
		
		List<ParseToken> tokens = parserToTest.parse("function 1 #comment1\nfunction name() #comment2");
		
		assertNotNull(tokens);
		assertEquals(2,tokens.size());
		
		Iterator<ParseToken> it = tokens.iterator();
		ParseToken token1 = it.next();

		assertEquals("#comment1",token1.text);
		
		ParseToken token2 = it.next();
		assertEquals("#comment2",token2.text);
	}
}
