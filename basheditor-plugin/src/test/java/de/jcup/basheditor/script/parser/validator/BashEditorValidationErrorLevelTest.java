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
