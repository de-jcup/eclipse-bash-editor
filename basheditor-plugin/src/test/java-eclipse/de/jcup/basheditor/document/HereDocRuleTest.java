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
 package de.jcup.basheditor.document;

import static org.junit.Assert.*;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.junit.Before;
import org.junit.Test;

/**
 * Sorrowly not executable by gradle because of eclipse dependencies. But
 * at least executable in eclipse environment. Tests that rule works
 * @author Albert Tregnaghi
 *
 */
public class HereDocRuleTest {
	
	private IToken token;
	private SimpleTestCharacterScanner scanner;
	private HereDocumentRule rule;

	@Before
	public void before(){
		token = new Token("mocked");
		
		rule = new HereDocumentRule(token);
	}

	@Test
	public void line_starting_with_delimiter_returns_token() {
		/* prepare */
		scanner = new SimpleTestCharacterScanner("<< EOF\nsomething\nEOF");
		rule.trace=true;
		
		/* execute */
		IToken result = rule.evaluate(scanner);
		
		/* test */
		assertEquals(token,result);
		
	}
	
	@Test
	public void line_starting_with_space_followed_by_delimiter_returns_not_token_but_undefined() {
		/* prepare */
		scanner = new SimpleTestCharacterScanner(" << EOF\nsomething\nEOF");
		rule.trace=true;
		
		/* execute */
		IToken result = rule.evaluate(scanner);
		
		/* test */
		assertEquals(Token.UNDEFINED,result);
		
	}
	
	@Test
	public void line_starting_with_space_followed_by_delimiter_returns_token_when_scanner_pos_is_not_0_but_1() {
		/* prepare */
		scanner = new SimpleTestCharacterScanner(" << EOF\nsomething\nEOF");
		scanner.column=1;
		
		rule.trace=true;
		
		/* execute */
		IToken result = rule.evaluate(scanner);
		
		/* test */
		assertEquals(token,result);
		
	}
	

}
