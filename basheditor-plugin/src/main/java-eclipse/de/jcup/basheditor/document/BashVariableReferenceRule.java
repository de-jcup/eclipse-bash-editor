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
 * A special rule to scan bash va, accepts just names - e.g. a $MYVAR is recognized, but not $(xxx)
 * @author Albert Tregnaghi
 *
 */
public class BashVariableReferenceRule implements IPredicateRule {
	private IToken token;

	public BashVariableReferenceRule(IToken token) {
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
		int countBack = 0;
		int varLength=0;
		char start = (char) scanner.read();
		if (start!='$') {
			scanner.unread();
			return Token.UNDEFINED;
		}
		countBack++;
		varLength++;
		
		do {
			int read = scanner.read(); // use int for EOF detection, char makes problems here!
			countBack++;
			char c = (char) read;
			if (ICharacterScanner.EOF == read || (!isWordPart(c))) {
				if (varLength>1) {
					/* variable found, but now end reached */
					scanner.unread();
				}else {
					/* not a $X - but e.g. a $ X which is not valid */
					for (int i=0;i<countBack;i++) {
						scanner.unread();
					}
					return Token.UNDEFINED;
				}
				break;
			}else {
				varLength++;
			}
		} while (true);
		return getSuccessToken();
	}

	// see http://tldp.org/LDP/abs/html/string-manipulation.html
	private boolean isWordPart(char c) {
		if (c=='_'|| Character.isLetterOrDigit(c)) {
			return true;
		}
		return false;
	}
}
