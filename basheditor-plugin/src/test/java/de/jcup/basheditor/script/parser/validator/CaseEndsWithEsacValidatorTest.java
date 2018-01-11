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
 package de.jcup.basheditor.script.parser.validator;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.jcup.basheditor.script.ValidationResult;
import de.jcup.basheditor.script.parser.ParseToken;
import de.jcup.basheditor.script.parser.TestParseToken;

public class CaseEndsWithEsacValidatorTest {

	private CaseEndsWithEsacValidator validatorToTest;
	private ArrayList<ParseToken> tokens;
	
	@Before
	public void before(){
		validatorToTest = new CaseEndsWithEsacValidator();
		tokens = new ArrayList<>();
	}
	
	@Test
	public void esac_case_has_problem() {
		/* prepare */
		tokens.add(new TestParseToken("esac"));
		tokens.add(new TestParseToken("case"));
		
		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);
		
		/* test */
		assertEquals(1,results.size());
	}
	
	@Test
	public void case_esac_esac_has_problem() {
		/* prepare */
		tokens.add(new TestParseToken("case"));
		tokens.add(new TestParseToken("esac"));
		tokens.add(new TestParseToken("case"));
		
		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);
		
		/* test */
		assertEquals(1,results.size());
	}
	
	@Test
	public void case_something_esac__has_no_problems() {
		/* prepare */
		tokens.add(new TestParseToken("case"));
		tokens.add(new TestParseToken("something"));
		tokens.add(new TestParseToken("esac"));
		
		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);
		
		/* test */
		assertEquals(0,results.size());
	}
	
	@Test
	public void case_something_casexne__has_problem() {
		/* prepare */
		tokens.add(new TestParseToken("case"));
		tokens.add(new TestParseToken("something"));
		tokens.add(new TestParseToken("casexne"));
		
		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);
		
		/* test */
		assertEquals(1,results.size());
		ValidationResult validationResult = results.iterator().next();
		assertEquals(ValidationResult.Type.ERROR, validationResult.getType());
	}

	
	@Test
	public void case_something_has_problem() {
		/* prepare */
		tokens.add(new TestParseToken("case"));
		tokens.add(new TestParseToken("something"));
		
		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);
		
		/* test */
		assertEquals(1,results.size());
		ValidationResult validationResult = results.iterator().next();
		assertEquals(ValidationResult.Type.ERROR, validationResult.getType());
	}
	
	
	@Test
	public void case_something_case_something2_esac_has_problem() {
		/* prepare */
		tokens.add(new TestParseToken("case"));
		tokens.add(new TestParseToken("something"));
		tokens.add(new TestParseToken("case"));
		tokens.add(new TestParseToken("something2"));
		tokens.add(new TestParseToken("esac"));
		
		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);
		
		/* test */
		assertEquals(1,results.size());
		ValidationResult validationResult = results.iterator().next();
		assertEquals(ValidationResult.Type.ERROR, validationResult.getType());
	}
	
	@Test
	public void case_something_esac_something2_esac_has_problem() {
		/* prepare */
		tokens.add(new TestParseToken("case"));
		tokens.add(new TestParseToken("something"));
		tokens.add(new TestParseToken("esac"));
		tokens.add(new TestParseToken("something2"));
		tokens.add(new TestParseToken("esac"));
		
		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);
		
		/* test */
		assertEquals(1,results.size());
		ValidationResult validationResult = results.iterator().next();
		assertEquals(ValidationResult.Type.ERROR, validationResult.getType());
	}
	
	@Test
	public void case_x_esac_case_y_esac_case_esac_esac_case__has_problem() {
		/* prepare */
		tokens.add(new TestParseToken("case"));
		tokens.add(new TestParseToken("x"));
		tokens.add(new TestParseToken("esac"));
		tokens.add(new TestParseToken("case"));
		tokens.add(new TestParseToken("y"));
		tokens.add(new TestParseToken("esac"));
		tokens.add(new TestParseToken("case"));
		tokens.add(new TestParseToken("esac"));
		tokens.add(new TestParseToken("case"));
		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);
		
		/* test */
		assertEquals(1,results.size());
		ValidationResult validationResult = results.iterator().next();
		assertEquals(ValidationResult.Type.ERROR, validationResult.getType());
	}
	
	@Test
	public void case_something_case_something2_esac_esac_has_no_problems() {
		/* prepare */
		tokens.add(new TestParseToken("case"));
		tokens.add(new TestParseToken("something"));
		tokens.add(new TestParseToken("case"));
		tokens.add(new TestParseToken("something2"));
		tokens.add(new TestParseToken("esac"));
		tokens.add(new TestParseToken("esac"));
		
		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);
		
		/* test */
		assertEquals(0,results.size());
	}
	
	@Test
	public void case_something_simple_string_with_case_something2_esac_esac_has_problems() {
		/* prepare */
		tokens.add(new TestParseToken("case"));
		tokens.add(new TestParseToken("something"));
		tokens.add(new TestParseToken("'case'"));
		tokens.add(new TestParseToken("something2"));
		tokens.add(new TestParseToken("esac"));
		tokens.add(new TestParseToken("esac"));
		
		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);
		
		/* test */
		assertEquals(1,results.size());
		ValidationResult validationResult = results.iterator().next();
		assertEquals(ValidationResult.Type.ERROR, validationResult.getType());
	}
	
}
