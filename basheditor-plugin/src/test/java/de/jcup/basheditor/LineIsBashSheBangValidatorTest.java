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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class LineIsBashSheBangValidatorTest {

	private LineIsBashSheBangValidator validatorToTest;

	@Before
	public void before() {
		validatorToTest = new LineIsBashSheBangValidator();
	}

	@Test
	public void shebang_slash_bin_slash_bash_is_valid() {
		assertTrue(validatorToTest.isValid("#!/bin/bash"));
	}
	
	@Test
	public void shebang_slash_user_slash_bin_slash_bash_is_valid() {
		assertTrue(validatorToTest.isValid("#!/usr/bin/bash"));
	}

	@Test
	public void shebang_usr_bin_slash_env_space_bash_is_valid() {
		assertTrue(validatorToTest.isValid("#!/usr/bin/env bash"));
	}

	
	@Test
	public void shebang_slash_bin_slash_sh_is_not_valid() {
		assertFalse(validatorToTest.isValid("#!/bin/sh"));
	}

	@Test
	public void wrong_slash_bin_slash_bash_is_valid() {
		assertFalse(validatorToTest.isValid("!/bin/bash"));
	}

	@Test
	public void hash_slash_bin_slash_bash_is_valid() {
		assertFalse(validatorToTest.isValid("#/bin/bash"));
	}

	@Test
	public void shebang_slash_bin_some_where_else_slash_bash_is_valid() {
		assertTrue(validatorToTest.isValid("#!/bin/some_where_else/bash"));
	}

	@Test
	public void shebang_slash_bin_some_where_else_slash_bash_some_params_is_valid() {
		assertTrue(validatorToTest.isValid("#!/bin/some_where_else/bash -l -x"));
	}

}
