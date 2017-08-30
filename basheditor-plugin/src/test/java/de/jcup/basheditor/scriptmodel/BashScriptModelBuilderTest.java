/*
 * Copyright 2017 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
 package de.jcup.basheditor.scriptmodel;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import de.jcup.basheditor.scriptmodel.BashFunction;
import de.jcup.basheditor.scriptmodel.BashScriptModel;
import de.jcup.basheditor.scriptmodel.BashScriptModelBuilder;

public class BashScriptModelBuilderTest {

	private BashScriptModelBuilder builderToTest;

	@Before
	public void before() {
		builderToTest = new BashScriptModelBuilder();
	}

	@Test
	public void an_empty_line_returns_not_null_AST() {
		BashScriptModel bashScriptModel = builderToTest.build("");
		assertNotNull(bashScriptModel);
	}

	@Test
	public void a_line_with_Xfunction_test_is_NOT_recognized() {
		BashScriptModel bashScriptModel = builderToTest.build("Xfunction test {}");
		assertNotNull(bashScriptModel);

		Collection<BashFunction> functions = bashScriptModel.getFunctions();
		assertNotNull(functions);
		assertEquals(0, functions.size());

	}

	@Test
	public void a_line_with_method_having_underscores_is_correct_parsed() {

		BashScriptModel bashScriptModel = builderToTest.build("function show_something_else{}");
		assertNotNull(bashScriptModel);

		Collection<BashFunction> functions = bashScriptModel.getFunctions();
		assertNotNull(functions);
		assertEquals(1, functions.size());
		BashFunction function = functions.iterator().next();
		assertEquals("show_something_else", function.getName());
	}

	@Test
	public void a_line_with_function_test_is_recognized_and_returns_function_with_name_test() {
		BashScriptModel bashScriptModel = builderToTest.build("function test {}");
		assertNotNull(bashScriptModel);

		Collection<BashFunction> functions = bashScriptModel.getFunctions();
		assertNotNull(functions);
		assertEquals(1, functions.size());

		BashFunction function = functions.iterator().next();
		assertEquals("test", function.getName());
	}

	@Test
	public void two_lines_with_functions_test1_and_test2_are_recognized_and_returns_2_function_with_name_test1_and_teset2() {
		BashScriptModel bashScriptModel = builderToTest
				.build("function test1 {\n#something\n}\n #other line\n\nfunction test2 {\n#something else\n}\n");
		assertNotNull(bashScriptModel);

		Collection<BashFunction> functions = bashScriptModel.getFunctions();
		assertNotNull(functions);
		assertEquals(2, functions.size());

		Iterator<BashFunction> iterator = functions.iterator();
		BashFunction function = iterator.next();
		assertEquals("test1", function.getName());

		function = iterator.next();
		assertEquals("test2", function.getName());
	}

	@Test
	public void two_lines_with_functions_test1_and_test2_are_recognized_and_returns_2_function_with_name_test1_and_teset2__but_with_backslash_r() {
		BashScriptModel bashScriptModel = builderToTest
				.build("function test1 {\n#something\n}\n #other line\n\nfunction test2 {\r\n#something else\r\n}\r\n");
		assertNotNull(bashScriptModel);

		Collection<BashFunction> functions = bashScriptModel.getFunctions();
		assertNotNull(functions);
		assertEquals(2, functions.size());

		Iterator<BashFunction> iterator = functions.iterator();
		BashFunction function = iterator.next();
		assertEquals("test1", function.getName());

		function = iterator.next();
		assertEquals("test2", function.getName());
	}

	@Test
	public void a_line_with_function_test_is_recognized_and_returns_function_with_pos_0() {
		BashScriptModel bashScriptModel = builderToTest.build("function test {}");
		assertNotNull(bashScriptModel);

		Collection<BashFunction> functions = bashScriptModel.getFunctions();
		assertNotNull(functions);
		assertEquals(1, functions.size());

		BashFunction function = functions.iterator().next();
		assertEquals(0, function.getPosition());
	}

	@Test
	public void a_line_with_function_test_is_recognized_and_returns_function_with_pos_1_when_first_line_empty() {
		BashScriptModel bashScriptModel = builderToTest.build("\nfunction test {}");
		assertNotNull(bashScriptModel);

		Collection<BashFunction> functions = bashScriptModel.getFunctions();
		assertNotNull(functions);
		assertEquals(1, functions.size());

		BashFunction function = functions.iterator().next();
		assertEquals(1, function.getPosition());
	}

	@Test
	public void a_line_with_5_spaces_and_function_test_is_recognized_and_returns_function_with_pos_5() {
		BashScriptModel bashScriptModel = builderToTest.build("     function test {}");
		assertNotNull(bashScriptModel);

		Collection<BashFunction> functions = bashScriptModel.getFunctions();
		assertNotNull(functions);
		assertEquals(1, functions.size());

		BashFunction function = functions.iterator().next();
		assertEquals(5, function.getPosition());
	}

	@Test
	public void a_line_with_5_spaces_and_Xfunction_test_is_NOT_recognized() {
		BashScriptModel bashScriptModel = builderToTest.build("     Xfunction test {}");
		assertNotNull(bashScriptModel);

		Collection<BashFunction> functions = bashScriptModel.getFunctions();
		assertNotNull(functions);
		assertEquals(0, functions.size());

	}

}
