package de.jcup.basheditor.parser;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

public class SimpleBashParserTest {
	
	private SimpleBashParser parserToTest;

	@Before
	public void before(){
		parserToTest = new SimpleBashParser(); 
	}

	@Test
	public void an_empty_line_returns_not_null_AST() {
		BashScriptModel bashScriptModel = parserToTest.parse("");
		assertNotNull(bashScriptModel);
	}
	
	@Test
	public void a_line_with_Xfunction_test_is_NOT_recognized() {
		BashScriptModel bashScriptModel = parserToTest.parse("Xfunction test {}");
		assertNotNull(bashScriptModel);
		
		Collection<BashFunction> functions = bashScriptModel.getFunctions();
		assertNotNull(functions);
		assertEquals(0,functions.size());
		
	}
	
	@Test
	public void a_line_with_function_test_is_recognized_and_returns_function_with_name_test() {
		BashScriptModel bashScriptModel = parserToTest.parse("function test {}");
		assertNotNull(bashScriptModel);
		
		Collection<BashFunction> functions = bashScriptModel.getFunctions();
		assertNotNull(functions);
		assertEquals(1,functions.size());
		
		BashFunction function = functions.iterator().next();
		assertEquals("test",function.getName());
	}
	
	@Test
	public void two_lines_with_functions_test1_and_test2_are_recognized_and_returns_2_function_with_name_test1_and_teset2() {
		BashScriptModel bashScriptModel = parserToTest.parse("function test1 {\n#something\n}\n #other line\n\nfunction test2 {\n#something else\n}\n");
		assertNotNull(bashScriptModel);
		
		Collection<BashFunction> functions = bashScriptModel.getFunctions();
		assertNotNull(functions);
		assertEquals(2,functions.size());
		
		Iterator<BashFunction> iterator = functions.iterator();
		BashFunction function = iterator.next();
		assertEquals("test1",function.getName());
		
		function = iterator.next();
		assertEquals("test2",function.getName());
	}
	
	@Test
	public void a_line_with_function_test_is_recognized_and_returns_function_with_pos_0() {
		BashScriptModel bashScriptModel = parserToTest.parse("function test {}");
		assertNotNull(bashScriptModel);
		
		Collection<BashFunction> functions = bashScriptModel.getFunctions();
		assertNotNull(functions);
		assertEquals(1,functions.size());
		
		BashFunction function = functions.iterator().next();
		assertEquals(0,function.getPosition());
	}
	
	@Test
	public void a_line_with_function_test_is_recognized_and_returns_function_with_pos_1_when_first_line_empty() {
		BashScriptModel bashScriptModel = parserToTest.parse("\nfunction test {}");
		assertNotNull(bashScriptModel);
		
		Collection<BashFunction> functions = bashScriptModel.getFunctions();
		assertNotNull(functions);
		assertEquals(1,functions.size());
		
		BashFunction function = functions.iterator().next();
		assertEquals(1,function.getPosition());
	}
	
	@Test
	public void a_line_with_5_spaces_and_function_test_is_recognized_and_returns_function_with_pos_5() {
		BashScriptModel bashScriptModel = parserToTest.parse("     function test {}");
		assertNotNull(bashScriptModel);
		
		Collection<BashFunction> functions = bashScriptModel.getFunctions();
		assertNotNull(functions);
		assertEquals(1,functions.size());
		
		BashFunction function = functions.iterator().next();
		assertEquals(5,function.getPosition());
	}
	
	@Test
	public void a_line_with_5_spaces_and_Xfunction_test_is_NOT_recognized() {
		BashScriptModel bashScriptModel = parserToTest.parse("     Xfunction test {}");
		assertNotNull(bashScriptModel);
		
		Collection<BashFunction> functions = bashScriptModel.getFunctions();
		assertNotNull(functions);
		assertEquals(0,functions.size());
		
	}

	

}
