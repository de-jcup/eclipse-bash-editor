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

import static de.jcup.basheditor.document.BashDocumentIdentifiers.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

import de.jcup.basheditor.document.keywords.BashGnuCommandKeyWords;
import de.jcup.basheditor.document.keywords.BashIncludeKeyWords;
import de.jcup.basheditor.document.keywords.BashLanguageKeyWords;
import de.jcup.basheditor.document.keywords.BashLiteralKeyWords;
import de.jcup.basheditor.document.keywords.BashSpecialVariableKeyWords;
import de.jcup.basheditor.document.keywords.BashSystemKeyWords;
import de.jcup.basheditor.document.keywords.DocumentKeyWord;
public class BashDocumentPartitionScanner extends RuleBasedPartitionScanner {

	private OnlyLettersKeyWordDetector onlyLettersWordDetector = new OnlyLettersKeyWordDetector();
	private BashVariableDetector bashVariableDetector = new BashVariableDetector();
	
	public BashDocumentPartitionScanner() {
		
		IToken comment = createToken(COMMENT);
		IToken simpleString = createToken(SINGLE_STRING);
		IToken doubleString = createToken(DOUBLE_STRING);
		IToken backtickString = createToken(BACKTICK_STRING);
		
		IToken systemKeyword = createToken(BASH_SYSTEM_KEYWORD);
		IToken bashKeyword = createToken(BASH_KEYWORD);
		IToken literal = createToken(LITERAL);

		IToken knownVariables = createToken(KNOWN_VARIABLES);
		IToken variables = createToken(VARIABLES);
		IToken includeKeyword = createToken(INCLUDE_KEYWORD);
		IToken bashCommand = createToken(BASH_COMMAND);

		List<IPredicateRule> rules = new ArrayList<>();
		buildWordRules(rules, systemKeyword, BashSystemKeyWords.values(),onlyLettersWordDetector);
		rules.add(new BashVariableRule(bashVariableDetector,variables));
		rules.add(new SingleLineRule("#", "", comment));
		rules.add(new MultiLineRule("\"", "\"", doubleString));
		rules.add(new MultiLineRule("\'", "\'", simpleString));
		rules.add(new MultiLineRule("`", "`", backtickString));
		
		buildWordRules(rules, includeKeyword, BashIncludeKeyWords.values(),onlyLettersWordDetector);
		buildWordRules(rules, bashCommand, BashGnuCommandKeyWords.values(),onlyLettersWordDetector);
		buildWordRules(rules, bashKeyword, BashLanguageKeyWords.values(),onlyLettersWordDetector);
		buildWordRules(rules, literal, BashLiteralKeyWords.values(),onlyLettersWordDetector);
		
		buildWordRules(rules, knownVariables, BashSpecialVariableKeyWords.values(),onlyLettersWordDetector);

		setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
	}

	private void buildWordRules(List<IPredicateRule> rules, IToken token,
			DocumentKeyWord[] values, IWordDetector wordDetector) {
		for (DocumentKeyWord keyWord: values){
			rules.add(new ExactWordPatternRule(wordDetector, createWordStart(keyWord),token));
		}
	}
	
	private String createWordStart(DocumentKeyWord keyWord) {
		return keyWord.getText();
	}

	private IToken createToken(BashDocumentIdentifier identifier) {
		return new Token(identifier.getId());
	}
}
