/*
 * Copyright 2020 Albert Tregnaghi
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
 * A special rule for ${xxxx} will accept anything between the brackets
 * @author Albert Tregnaghi
 *
 */
public class BashVariableInBracketsReferenceRule implements IPredicateRule {
	private IToken token;

	public BashVariableInBracketsReferenceRule(IToken token) {
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
		if (start!='$') {
			scanner.unread();
			return Token.UNDEFINED;
		}
		
		start = (char) scanner.read();
		if (start!='{') {
			scanner.unread();
			scanner.unread();
			return Token.UNDEFINED;
		}
		
		do {
			int read = scanner.read(); // use int for EOF detection, char makes problems here!
			char c = (char) read;
			if (ICharacterScanner.EOF == read || c=='}') {
				break;
			}
		} while (true);
		return getSuccessToken();
	}

}
