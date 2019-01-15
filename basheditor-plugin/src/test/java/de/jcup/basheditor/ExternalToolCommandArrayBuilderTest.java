package de.jcup.basheditor;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

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
		String[] result = actionToTest.build("beautysh.py -f $filename", editorFile);

		/* test*/
		assertEquals(3, result.length);
		assertEquals("beautysh.py",result[0]);
		assertEquals("-f",result[1]);
		assertEquals("./../basheditor-other/testscripts/strings.sh",result[2]);
	}

}
