package de.jcup.basheditor.document.keywords;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import de.jcup.basheditor.ResourceInputStreamProvider;

public class TooltipTextSupportTest {

	private TooltipTextSupport supportToTest;

	@Before
	public void before(){
		supportToTest = new TooltipTextSupport();
		supportToTest.setResourceInputStreamProvider(new TestResourceInputstreamProvider());
	}
	
	@Test
	public void support_get_null_returns_empty_string() {
		/* execute */
		String result = supportToTest.get(null);
		
		/* test */
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void support_get_echo_returns_not_empty_string() {
		/* execute */
		String result = supportToTest.get("echo");
		/* test */
		assertFalse(result.isEmpty());
	}
	
	@Test
	public void support_get_unknown_returns_empty_string() {
		/* execute */
		String result = supportToTest.get("unknown");
		/* test */
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void support_no_resource_handler_set_get_unknown_returns_empty_string() {
		/* prepare */
		supportToTest.setResourceInputStreamProvider(null);
		
		/* execute */
		String result = supportToTest.get("unknown");
		/* test */
		assertTrue(result.isEmpty());
	}

	private static class TestResourceInputstreamProvider implements ResourceInputStreamProvider{
		/* eclipse junit execution*/
		private static File TOOLTIPS_FOLDER = new File("./tooltips");
		static{
			if (!TOOLTIPS_FOLDER.exists()){
				/* gradle */
				TOOLTIPS_FOLDER=new File("./bash-editor-plugin/tooltips");
			}
		}
		@Override
		public InputStream getStreamFor(String path) throws IOException {
			File file = new File(TOOLTIPS_FOLDER.getParentFile(),path);
			return new FileInputStream(file);
		}
		
	}
}