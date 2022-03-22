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
 * Special variantFullText, because boolean assignment to variables are not correct
 * highlighted otherwise "myvar=true" was not highlighted but "myvar= true"
 * which is wrong is highlighted
 * 
 * @author albert
 *
 */
public class BashVariableDefineRule implements IPredicateRule {

    private IToken token;
    private char[] exactPart;
    private boolean needsWhitespaceOrEofAtEnd;

    BashVariableDefineRule(IToken token, String exactPart) {
        this(token, exactPart, true);
    }

    BashVariableDefineRule(IToken token, String exactPart, boolean needsWhitespaceOrEofAtEnd) {
        this.token = token;
        this.exactPart = exactPart.toCharArray();
        this.needsWhitespaceOrEofAtEnd = needsWhitespaceOrEofAtEnd;
    }

    @Override
    public IToken evaluate(ICharacterScanner scanner) {
        return evaluate(scanner, false);
    }

    @Override
    public IToken getSuccessToken() {
        return token;
    }

    @Override
    public IToken evaluate(ICharacterScanner scanner, boolean resume) {
        if (scanner.getColumn() == 0) {
            return Token.UNDEFINED;
        }
        scanner.unread();
        int before = scanner.read();
        if (before != '=') {
            return Token.UNDEFINED;
        }
        Counter counter = new Counter();
        for (int i = 0; i < exactPart.length; i++) {
            int c = scanner.read();
            counter.count++;
            if (c != exactPart[i]) {
                counter.cleanup(scanner);
                return Token.UNDEFINED;
            }
        }
        if (needsWhitespaceOrEofAtEnd) {
            int c = scanner.read();
            counter.count++;
            if (!Character.isWhitespace(c) && c != ICharacterScanner.EOF) {
                counter.cleanup(scanner);
                return Token.UNDEFINED;
            }
        }
        return token;
    }

}
