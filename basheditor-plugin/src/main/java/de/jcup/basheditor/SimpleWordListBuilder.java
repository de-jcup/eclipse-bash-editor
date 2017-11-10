package de.jcup.basheditor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleWordListBuilder {

	public List<String> build(String source) {
		if (source == null) {
			return Collections.emptyList();
		}
		String[] allWords = source.split("[\\s,;.!()\\?]");
		List<String> list = new ArrayList<>();
		for (String word: allWords){
			if (word!=null && ! word.isEmpty()){
				list.add(word);
			}
		}
		return list;
	}
}
