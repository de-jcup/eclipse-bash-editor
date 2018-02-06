/*
 * Copyright 2018 Albert Tregnaghi
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

import de.jcup.basheditor.script.parser.HereStringParserSupport;

public class HereStringRule implements IPredicateRule{

	private IToken token;
	private HereStringParserSupport hereStringSupport;
	boolean trace;

	public HereStringRule(IToken token) {
		this.token=token;
		hereStringSupport=new HereStringParserSupport();
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
		int r = scanner.read();
		scanner.unread();
		/* fast guard closing:*/
		if (ICharacterScanner.EOF==r || r!='<'){
			return Token.UNDEFINED;
		}
		
		ICharacterScannerCodePosSupport codePosSupport = new ICharacterScannerCodePosSupport(scanner);
		if (hereStringSupport.isHereStringStateHandled(codePosSupport)){
			return getSuccessToken();
		}
		codePosSupport.resetToStartPos();
		return Token.UNDEFINED;
	}

}