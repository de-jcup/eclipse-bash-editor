package de.jcup.basheditor.script.parser.validator;

import static org.junit.Assert.*;

import org.junit.Test;

public class BashEditorValidationErrorLevelTest {

	@Test
	public void test_from_info_is_info() {
		assertEquals(BashEditorValidationErrorLevel.INFO, BashEditorValidationErrorLevel.fromId("info"));
	}
	
	@Test
	public void test_from_warn_is_warn() {
		assertEquals(BashEditorValidationErrorLevel.WARNING, BashEditorValidationErrorLevel.fromId("warning"));
	}
	
	@Test
	public void test_from_error_is_error() {
		assertEquals(BashEditorValidationErrorLevel.ERROR, BashEditorValidationErrorLevel.fromId("error"));
	}
	
	@Test
	public void test_from_null_is_error() {
		assertEquals(BashEditorValidationErrorLevel.ERROR, BashEditorValidationErrorLevel.fromId(null));
	}
	
	@Test
	public void test_from_illegal_value_is_error() {
		assertEquals(BashEditorValidationErrorLevel.ERROR, BashEditorValidationErrorLevel.fromId("illegal-value"));
	}
}
