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

import static de.jcup.basheditor.document.BashDocumentIdentifiers.BACKTICK_STRING;
import static de.jcup.basheditor.document.BashDocumentIdentifiers.BASH_COMMAND;
import static de.jcup.basheditor.document.BashDocumentIdentifiers.BASH_KEYWORD;
import static de.jcup.basheditor.document.BashDocumentIdentifiers.BASH_SYSTEM_KEYWORD;
import static de.jcup.basheditor.document.BashDocumentIdentifiers.COMMENT;
import static de.jcup.basheditor.document.BashDocumentIdentifiers.DOUBLE_STRING;
import static de.jcup.basheditor.document.BashDocumentIdentifiers.HERE_DOCUMENT;
import static de.jcup.basheditor.document.BashDocumentIdentifiers.HERE_STRING;
import static de.jcup.basheditor.document.BashDocumentIdentifiers.INCLUDE_KEYWORD;
import static de.jcup.basheditor.document.BashDocumentIdentifiers.KNOWN_VARIABLES;
import static de.jcup.basheditor.document.BashDocumentIdentifiers.PARAMETER;
import static de.jcup.basheditor.document.BashDocumentIdentifiers.SINGLE_STRING;
import static de.jcup.basheditor.document.BashDocumentIdentifiers.VARIABLES;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

import de.jcup.basheditor.document.keywords.BashGnuCommandKeyWords;
import de.jcup.basheditor.document.keywords.BashIncludeKeyWords;
import de.jcup.basheditor.document.keywords.BashLanguageKeyWords;
import de.jcup.basheditor.document.keywords.BashSpecialVariableKeyWords;
import de.jcup.basheditor.document.keywords.BashSystemKeyWords;
import de.jcup.eclipse.commons.keyword.DocumentKeyWord;

public class BashDocumentPartitionScanner extends RuleBasedPartitionScanner {

	private OnlyLettersKeyWordDetector onlyLettersWordDetector = new OnlyLettersKeyWordDetector();

	public BashDocumentPartitionScanner() {
		IToken hereDocument = createToken(HERE_DOCUMENT);
		IToken hereString = createToken(HERE_STRING);
		IToken parameters = createToken(PARAMETER);
		IToken comment = createToken(COMMENT);
		IToken simpleString = createToken(SINGLE_STRING);
		IToken doubleString = createToken(DOUBLE_STRING);
		IToken backtickString = createToken(BACKTICK_STRING);

		IToken systemKeyword = createToken(BASH_SYSTEM_KEYWORD);
		IToken bashKeyword = createToken(BASH_KEYWORD);

		IToken knownVariables = createToken(KNOWN_VARIABLES);
		IToken variables = createToken(VARIABLES);
		IToken includeKeyword = createToken(INCLUDE_KEYWORD);
		IToken bashCommand = createToken(BASH_COMMAND);

		List<IPredicateRule> rules = new ArrayList<>();
		rules.add(new HereStringRule(hereString));
		rules.add(new HereDocumentRule(hereDocument));

		buildWordRules(rules, systemKeyword, BashSystemKeyWords.values());
		rules.add(new SingleLineRule("#", "", comment, (char) -1, true));

		rules.add(new BashDoubleQuoteRule("\"", "\"", doubleString));
		rules.add(new BashSingleQuoteRule(simpleString));
		rules.add(new BashDoubleQuoteRule("`", "`", backtickString));

		rules.add(new BashVariableInBracketsReferenceRule(variables));
		rules.add(new BashVariableReferenceRule(variables));
		rules.add(new BashCommandOutputToVariableRule(bashCommand));

		rules.add(new CommandParameterRule(parameters));

		rules.add(new BashVariableDefineRule(bashKeyword, "true", true));
		rules.add(new BashVariableDefineRule(bashKeyword, "false", true));

		buildWordRules(rules, includeKeyword, BashIncludeKeyWords.values());
		buildWordRules(rules, bashKeyword, BashLanguageKeyWords.values());
		buildWordRules(rules, bashCommand, BashGnuCommandKeyWords.values(), true);

		buildWordRules(rules, knownVariables, BashSpecialVariableKeyWords.values());

		setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
	}

	private void buildWordRules(List<IPredicateRule> rules, IToken token, DocumentKeyWord[] values) {
		buildWordRules(rules, token, values, false);
	}

	private void buildWordRules(List<IPredicateRule> rules, IToken token, DocumentKeyWord[] values,
			boolean acceptStartBrackets) {
		for (DocumentKeyWord keyWord : values) {
			ExactWordPatternRule rule = new ExactWordPatternRule(onlyLettersWordDetector, createWordStart(keyWord),
					token, keyWord.isBreakingOnEof());
			rule.setAcceptStartBrackets(acceptStartBrackets);
			rules.add(rule);
		}
	}

	private String createWordStart(DocumentKeyWord keyWord) {
		return keyWord.getText();
	}

	private IToken createToken(BashDocumentIdentifier identifier) {
		return new Token(identifier.getId());
	}
}
