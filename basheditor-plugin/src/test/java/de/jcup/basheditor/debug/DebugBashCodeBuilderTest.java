package de.jcup.basheditor.debug;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;


public class DebugBashCodeBuilderTest {

	private DebugBashCodeBuilder builderToTest;

	@Before
	public void before() {
		builderToTest = new DebugBashCodeBuilder();
	}

	@Test
	public void buildSafeArrayValue() {
		/* test */
		assertEquals("", builderToTest.buildSafeArrayValue(null));
		assertEquals("", builderToTest.buildSafeArrayValue("("));
		assertEquals("", builderToTest.buildSafeArrayValue(""));
		assertEquals("", builderToTest.buildSafeArrayValue(")"));
		assertEquals("6", builderToTest.buildSafeArrayValue("6"));
		assertEquals("6", builderToTest.buildSafeArrayValue("\"6\""));
		assertEquals("-d", builderToTest.buildSafeArrayValue("\"-d\""));
	}
	
	@Test
	public void command_and_trap_function_are_defined() {
		/* test */
		assertFalse(builderToTest.getNameOfDebugCommand().isEmpty());
		assertFalse(builderToTest.getNameOfTrapFunction().isEmpty());
	}

	@Test
	public void remote_command_is_correct_build_for_defaults() {
		assertEquals("set >&33 ; echo $'\\\\x0A\\\\x0A\\\\x0A\\\\x0A\\\\x0A\\\\x0A\\\\x0A\\\\x0A\\\\x0A\\\\x0A\\\\x0A\\\\x09' >&33 ; read -u 33 " + builderToTest.getNameOfDebugCommand() + "\n",

				builderToTest.buildRemoteDebugCommand());
	}

	@Test
	public void remote_command_is_correct_build_for_custom_file_descriptor() {

		/* prepare */
		builderToTest.setFileDescriptor(77);

		/* test */
		assertEquals("set >&77 ; echo $'\\\\x0A\\\\x0A\\\\x0A\\\\x0A\\\\x0A\\\\x0A\\\\x0A\\\\x0A\\\\x0A\\\\x0A\\\\x0A\\\\x09' >&77 ; read -u 77 " + builderToTest.getNameOfDebugCommand() + "\n",

				builderToTest.buildRemoteDebugCommand());
	}

	@Test
	public void given_port_and_hostname_are_inside_script_default_filedescriptor_choosen_is_33() {
		/* prepare */
		builderToTest.setPort(666);
		builderToTest.setHostname("jcup.de");

		/* execute */
		String result = builderToTest.buildDebugBashCodeSnippet();

		/* test @formatter:off */
		int line=1;
		assertLines(result).
			contains(line++, "exec 33<>/dev/tcp/jcup.de/666").
			contains(line++, "function "+builderToTest.getNameOfTrapFunction()+"()").
			contains(line++, "{").
			contains(line++, "local "+builderToTest.getNameOfDebugCommand()).
			contains(line++, "read -u 33 "+builderToTest.getNameOfDebugCommand()).
			contains(line++, "eval $"+builderToTest.getNameOfDebugCommand()).
			contains(line++, "}").
			contains(line++, "set -o functrace").
			contains(line++, "trap "+builderToTest.getNameOfTrapFunction()+" DEBUG");
		/* @formatter:on*/
	}

	private static AssertResult assertLines(String code) {
		return new AssertResult(code);
	}

	private static class AssertResult {
		private String[] lines;

		private AssertResult(String result) {
			lines = result.split("\n");
		}

		public AssertResult contains(int line, String expected) {
			int index = line - 1;
			assertTrue(lines.length > index);
			assertEquals(expected, lines[index].trim());
			return this;
		}
	}

}
