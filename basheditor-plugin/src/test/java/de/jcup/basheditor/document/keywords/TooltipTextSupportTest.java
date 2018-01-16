package de.jcup.basheditor.document.keywords;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TooltipTextSupportTest {

	private TooltipTextSupport supportToTest;

	@Before
	public void before(){
		supportToTest = new TooltipTextSupport();
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

}
