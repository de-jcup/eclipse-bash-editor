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
 package de.jcup.basheditor.script.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AssertParseTokens {

	private List<ParseToken> parseTokens;

	/**
	 * Ensures given list is not null and returns dedicated assert object
	 * @param parseTokens
	 * @return assert object
	 */
	public static AssertParseTokens assertThat(List<ParseToken> parseTokens){
		return new AssertParseTokens(parseTokens);
	}
	
	public AssertParseTokens(List<ParseToken> parseTokens) {
		assertNotNull("Parse tokens may not be null!", parseTokens);
		this.parseTokens=parseTokens;
	}
	
	public AssertParseTokens containsOneToken(String text){
		return containsToken(text,1);
	}
	
	public AssertParseTokens containsNotToken(String text){
		return containsToken(text,0);
	}
	
	public AssertParseTokens containsToken(String text, int expectedAmount){
		int count =0;
		for (ParseToken token: parseTokens){
			if (text.equals(token.getText())){
				count++;
			}
		}
		if (expectedAmount!=count){
			assertEquals("The token amount for '"+text+"' is not as expected", expectedAmount,count);
		}
		return this;
	}
	
	/**
	 * Ensures there exists at least one token with given text. The first token will be returned
	 * @param token
	 * @return token, never <code>null</code>
	 */
	public AssertParseToken token(String token){
		return token(token,1);
	}
		
	public AssertParseToken token(String token, int tokenNumber){
		int nr=0;
		for (ParseToken found: parseTokens){
			if (token.equals(found.getText())){
				nr++;
				if (tokenNumber==nr){
					return AssertParseToken.assertThat(found);
				}
			}
		}
		fail("Tried to get token '"+token+"' number:"+tokenNumber+" but did found only "+nr+" in:"+parseTokens);
		return null;
	}

	public AssertParseTokens containsTokens(String ...tokens){
		List<String> found = new ArrayList<String>();
		for (ParseToken token: parseTokens){
			found.add(token.getText());
		} 
		if (tokens.length != found.size()){
			showTokenDiffAssertion("Tokens length differ - expected "+tokens.length+", but got: "+found.size(),found,tokens);
		}
		if (! Arrays.equals(tokens, found.toArray())){
			showTokenDiffAssertion("Tokens length content - expected "+tokens.length+", andd got: "+found.size()+" but content is unexpected",found,tokens);
		}
		return this;
	}
	
	private void showTokenDiffAssertion(String message, List<String> found, String ...expected) {
		assertEquals(message, buildTokenStringForCompare(expected),buildTokenStringForCompare(found));
	}
	
	private String buildTokenStringForCompare(String ... tokens) {
		
		List<String> list = Arrays.asList(tokens);
		return buildTokenStringForCompare(list);
	}

	private String buildTokenStringForCompare(List<String> list) {
		StringBuilder sb = new StringBuilder();
		int i=0;
		for (String token: list) {
			sb.append(lineNumber(i++)).append(':');
			sb.append(token);
			sb.append("\n");
		}
		return sb.toString();
	}
	
	private String lineNumber(int lineNumber) {
		StringBuilder sb = new StringBuilder();
		sb.append(lineNumber);
		while (sb.length()<4) {
			sb.insert(0, '0');
		}
		return sb.toString();
	}

	public ParseToken resolveToken(String string) {
		for (ParseToken token: parseTokens){
			if (token.getText().equals(string)){
				return token;
			}
		} 
		fail("Tried to resolve token '"+string+"' but did not found in:"+parseTokens);
		return null;
	}
	
	
}
