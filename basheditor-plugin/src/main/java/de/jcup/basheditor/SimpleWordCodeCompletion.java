package de.jcup.basheditor;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class SimpleWordCodeCompletion {

	public Set<String> calculate(String source, int offset){
		if (source==null){
			return Collections.emptySet();
		}
		String[] allWords = source.split("\\s");
		/* silly implementation  -just return all words...*/
		Set<String> set = new TreeSet<>(Arrays.asList(allWords));
		set.remove("");
		return set;
	}
}
