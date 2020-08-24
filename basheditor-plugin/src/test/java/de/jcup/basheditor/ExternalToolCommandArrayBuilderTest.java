/*
 * Copyright 2018 Albert Tregnaghi
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
package de.jcup.basheditor;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import de.jcup.basheditor.debug.launch.OSUtil;

public class ExternalToolCommandArrayBuilderTest {

	private ExternalToolCommandArrayBuilder actionToTest;

	@Before
	public void before() {
		actionToTest = new ExternalToolCommandArrayBuilder();
	}
	
	@Test
	public void anExistingFile_commands_are_as_expected_and_parameter_replaced() {
		/* prepare*/
		File editorFile = TestScriptLoader.getTestScriptFile("strings.sh");
		
		/* execute */
		String[] result = actionToTest.build("beautysh.py $filename", editorFile);

		/* test*/
		assertEquals(2, result.length);
		assertEquals("beautysh.py",result[0]);
		if (OSUtil.isWindows()){
			assertEquals(".\\..\\basheditor-other\\testscripts\\strings.sh",result[1]);
		}else {
			assertEquals("./../basheditor-other/testscripts/strings.sh",result[1]);
		}
	}

}
