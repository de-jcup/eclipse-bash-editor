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
 package de.jcup.basheditor;

import static org.junit.Assert.*;

import org.junit.Test;

public class SimpleStringUtilsTest {

	@Test
	public void null_equals_null__is_true() {
		assertTrue(SimpleStringUtils.equals(null, null));
	}
	
	@Test
	public void a_equals_a__is_true() {
		assertTrue(SimpleStringUtils.equals("a", "a"));
	}
	
	@Test
	public void a_equals_null__is_false() {
		assertFalse(SimpleStringUtils.equals("a", null));
	}
	
	@Test
	public void a_equals_b__is_false() {
		assertFalse(SimpleStringUtils.equals("a","b"));
	}
	
	@Test
	public void b_equals_a__is_false() {
		assertFalse(SimpleStringUtils.equals("b","a"));
	}
	
	@Test
	public void null_equals_a__is_false() {
		assertFalse(SimpleStringUtils.equals(null,"a"));
	}

}
