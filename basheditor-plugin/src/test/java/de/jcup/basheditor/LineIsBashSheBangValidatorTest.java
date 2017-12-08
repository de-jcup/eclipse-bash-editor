package de.jcup.basheditor;

import static org.junit.Assert.*;

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
