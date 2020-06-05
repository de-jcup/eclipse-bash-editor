package de.jcup.basheditor.script;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.jcup.basheditor.TestScriptLoader;

public class BashScriptModelBuilderIntegrationTest {
	private BashScriptModelBuilder builderToTest;

	@Before
	public void before() {
		builderToTest = new BashScriptModelBuilder();
	}

	@Test
	public void none_of_the_testscripts_contains_any_failure() throws Exception {
		/* prepare */
		StringBuilder errorCollector = new StringBuilder();
		List<File> scriptFiles = TestScriptLoader.fetchAllTestScriptFiles();
		for (File scriptFile : scriptFiles) {

			String script = TestScriptLoader.loadScript(scriptFile);

			/* execute */
			try {
				BashScriptModel bashScriptModel = builderToTest.build(script);
				/* test */
				if (bashScriptModel.hasErrors()) {
					errorCollector.append("script file:").append(scriptFile).append(" contains errors:\n");
					for (BashError error : bashScriptModel.getErrors()) {
						errorCollector.append("-");
						errorCollector.append(error.getMessage());
						errorCollector.append("\n");
					}
				}
			} catch (BashScriptModelException e) {
				/* test */
				errorCollector.append("script file:").append(scriptFile).append(" contains errors:\n");
				errorCollector.append("-");
				Throwable root = e;
				while (root.getCause() != null) {
					root = root.getCause();
				}
				errorCollector.append("Root cause:" + root.getMessage());
				errorCollector.append("\n");

				root.printStackTrace();
			}

		}
		if (errorCollector.length() > 0) {
			fail(errorCollector.toString());
		}
	}
}
