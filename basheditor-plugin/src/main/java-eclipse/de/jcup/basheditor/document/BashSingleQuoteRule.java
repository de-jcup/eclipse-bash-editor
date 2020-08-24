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

//  echo 'test'\''test'  -> test'test
//  echo 'test\'\''test' -> test\'test
public class BashSingleQuoteRule implements IPredicateRule {

	private IToken sucessToken;

	public BashSingleQuoteRule(IToken token) {
		this.sucessToken = token;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		return evaluate(scanner, false);
	}

	@Override
	public IToken getSuccessToken() {
		return sucessToken;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {

		int column = scanner.getColumn();
		int read = scanner.read();
		if (read != '\'') {
			scanner.unread();
			return Token.UNDEFINED;
		}
		if (column != 1) {
			scanner.unread();
			scanner.unread();
			int before = scanner.read();
			char charBefore = (char) before;
			if (charBefore == '\\') {
				/* in this case we must ignore because not quoted */
			}
			return Token.UNDEFINED;
		}
		while (read!= ICharacterScanner.EOF && ((char)read)!='\'') {
			read = scanner.read();
		}

		return sucessToken;
	}

}
