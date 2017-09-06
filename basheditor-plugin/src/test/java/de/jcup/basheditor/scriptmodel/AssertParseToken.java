package de.jcup.basheditor.scriptmodel;

import static org.junit.Assert.*;

public class AssertParseToken {

	public static AssertParseToken assertThat(ParseToken token){
		return new AssertParseToken(token);
	}

	private ParseToken token;
	
	public AssertParseToken(ParseToken token) {
		assertNotNull("Parse token may not be null!", token);
		this.token=token;
	}
	
	public AssertParseToken hasStart(int position){
		assertEquals("start posistion not as expected",position,token.start);
		return this;
	}
	
	public AssertParseToken hasEnd(int position){
		assertEquals("end posistion not as expected", position,token.end);
		return this;
	}
	
}
