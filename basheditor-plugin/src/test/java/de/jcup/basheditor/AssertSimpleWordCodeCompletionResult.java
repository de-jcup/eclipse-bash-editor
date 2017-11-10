package de.jcup.basheditor;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Set;

public class AssertSimpleWordCodeCompletionResult{
	private Set<String> result;
	private AssertSimpleWordCodeCompletionResult(Set<String> result) {
		assertNotNull(result);
		this.result=result;
	}

	public static AssertSimpleWordCodeCompletionResult assertResult(Set<String> result){
		return new AssertSimpleWordCodeCompletionResult(result);
	}
	
	public AssertSimpleWordCodeCompletionResult hasResults(String ... expected){
		if (expected.length != result.size()){
			assertEquals(createDiffText(expected),expected.length,result.size());
		}
		
		for (String exp: expected){
			if (! result.contains(exp)){
				fail("Did not found:"+exp+" as word.\n"+createDiffText(expected));
			}
		}
		return this;
	}
	
	public AssertSimpleWordCodeCompletionResult hasNoResults() {
		if (! result.isEmpty()){
			fail("Did not expect results, but found!\n"+createDiffText(new String[]{}));
		}
		return this;
	}

	private String createDiffText(String[] expected) {
		StringBuilder sb = new StringBuilder();
		sb.append("Difference between expected and result found:\nExpected:");
		sb.append(Arrays.asList(expected).toString());
		sb.append("\nResult was:");
		sb.append(result.toString());
		return sb.toString();
	}


}