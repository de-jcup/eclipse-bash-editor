/*
 * Copyright 2019 Albert Tregnaghi
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
package de.jcup.basheditor.debug;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import de.jcup.basheditor.TestScriptLoader;

public class DebugBashCodeToggleSupportTest {

	private static final String BASE_EXPECTED_DEBUG_ENABLED_CODE = "source scriptHome/.basheditor/remote-debugging-v1.sh";
	private static final String EXPECTED_DEBUG_ENABLED_CODE = BASE_EXPECTED_DEBUG_ENABLED_CODE+" localhost "+BashDebugConstants.DEFAULT_DEBUG_PORT+" #BASHEDITOR-TMP-REMOTE-DEBUGGING-END";
	private DebugBashCodeToggleSupport supportToTest;
    private TestBashDebugInfoProvider infoProvider;

	@Before
	public void before() {
		infoProvider = new TestBashDebugInfoProvider();
        supportToTest = new DebugBashCodeToggleSupport(infoProvider);
	}
	
	
	@Test
	public void enable_debugging_empty_code_results_in_firstline_including_temp_debugger_file() throws Exception {
		/* execute */
		String newCode = supportToTest.enableDebugging("","localhost", BashDebugConstants.DEFAULT_DEBUG_PORT);

		/* test */
		String[] asArray = newCode.split("\n");
		assertEquals(1, asArray.length);
		assertTrue(asArray[0].startsWith(EXPECTED_DEBUG_ENABLED_CODE));

	}

	@Test
	public void enable_debugging_starting_with_comment_results_in_firstline_including_temp_debugger_file_and_with_comment_before() throws Exception {
		/* execute */
		String newCode = supportToTest.enableDebugging("#! /bin/mybash","localhost", BashDebugConstants.DEFAULT_DEBUG_PORT);

		/* test */
		String[] asArray = newCode.split("\n");
		assertEquals(2, asArray.length);
		assertEquals(EXPECTED_DEBUG_ENABLED_CODE, asArray[0]);
		assertEquals("#! /bin/mybash", asArray[1]);

	}
	
	@Test
	public void enable_debugging_starting_with_not_comment_but_code_results_in_firstline_including_temp_debugger_file_and_new_line_with_command_before() throws Exception {
		/* execute */
		String newCode = supportToTest.enableDebugging("echo alpha","localhost", BashDebugConstants.DEFAULT_DEBUG_PORT);

		/* test */
		String[] asArray = newCode.split("\n");
		assertEquals(2, asArray.length);
		assertEquals(EXPECTED_DEBUG_ENABLED_CODE, asArray[0]);
		assertEquals("echo alpha", asArray[1]);

	}

	@Test
	public void enable_debugging_will_automatically_create_debug_bash_code_file_which_contains_data_of_code_builder() throws Exception {
		/* prepare */
		File file = new File(infoProvider.getSystemUserHomePath(),"/.basheditor/remote-debugging-v1.sh");
		if (file.exists()) {
			file.delete();
		}
		assertFalse(file.exists());

		/* execute */
		supportToTest.enableDebugging("","localhost",BashDebugConstants.DEFAULT_DEBUG_PORT);

		/* test */
		assertTrue(file.exists()); // file must be recreated
		// check content is as expected:

		DebugBashCodeBuilder codeBuilder = new DebugBashCodeBuilder();
		String expected = codeBuilder.buildDebugBashCodeSnippet();

		String contentOfFile = TestScriptLoader.loadScript(file);
		assertEquals(expected, contentOfFile);

	}

	@Test
	public void disable_debugging_empty_code_results_in_empty_code() throws Exception {
		assertEquals("", supportToTest.disableDebugging(""));
	}

	@Test
	public void disable_debugging_first_line_has_include_but_nothing_else_results_in_empty_code() throws Exception {
		assertEquals("", supportToTest.disableDebugging(EXPECTED_DEBUG_ENABLED_CODE+"\n"));
	}
	@Test
	public void disable_debugging_first_line_has_include_and_one_empty_line_nothing_else_results_in_one_empty_line() throws Exception {
		assertEquals("\n", supportToTest.disableDebugging(EXPECTED_DEBUG_ENABLED_CODE+"\n\n"));
	}

	@Test
	public void disable_debugging_first_line_has_include_and_comment_after_include_only_comment_remains() throws Exception {
		assertEquals("#! /bin/mybash", supportToTest.disableDebugging(EXPECTED_DEBUG_ENABLED_CODE + "\n#! /bin/mybash"));
	}

	@Test
	public void disable_debugging_first_line_has_include_and_comment_secondline_has_alpha_after_include_only_comment_remains_in_first_line_second_has_alpha() throws Exception {
		assertEquals("#! /bin/mybash\nalpha", supportToTest.disableDebugging(EXPECTED_DEBUG_ENABLED_CODE + "\n#! /bin/mybash\nalpha"));
	}
	
	@Test
	public void disable_debugging_first_line_has_include_and_second_an_echo_alpha_result_first_line_will_be_echo_alpha() throws Exception {
		assertEquals("echo alpha", supportToTest.disableDebugging(EXPECTED_DEBUG_ENABLED_CODE + "\necho alpha"));
	}

}
