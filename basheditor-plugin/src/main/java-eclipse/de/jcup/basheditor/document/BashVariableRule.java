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

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * A special rule to scan bash variables
 * @author Albert Tregnaghi
 *
 */
public class BashVariableRule implements IPredicateRule {

	private IToken token;

	public BashVariableRule(IToken token) {
		this.token = token;
	}

	@Override
	public IToken getSuccessToken() {
		return token;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		return evaluate(scanner, false);
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		char start = (char) scanner.read();
		if (!isWordStart(start)) {
			scanner.unread();
			return Token.UNDEFINED;
		}
		boolean curlyBracesOpened = false;
		/* okay is a variable, so read until end reached */
		do {
			char c = (char) scanner.read();
			if (ICharacterScanner.EOF == c || (!isWordPart(c, curlyBracesOpened))) {
				scanner.unread();
				break;
			}
			if (c == '{') {
				curlyBracesOpened = true;
			}
			if (c == '}') {
				/* end of variable detected */
				break;
			}
		} while (true);
		return getSuccessToken();
	}

	private boolean isWordStart(char c) {
		return c == '$';
	}

	// see http://tldp.org/LDP/abs/html/string-manipulation.html
	private boolean isWordPart(char c, boolean curlyBracesOpened) {
		if (curlyBracesOpened) {
			if (c == '{') {
				return false; // already opened brace, we do not support {{
			}
			return true;
		}
		/* curly braces not opened! so we allow all except whitespaces */
		if (Character.isWhitespace(c)){
			return false;
		}
		/* all other characters are allowed */
		return true;
	}
}
