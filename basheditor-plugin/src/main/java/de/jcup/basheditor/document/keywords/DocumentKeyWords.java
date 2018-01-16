package de.jcup.basheditor.document.keywords;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DocumentKeyWords {
	private static final DocumentKeyWord[] ALL_KEYWORDS = createAllKeywords();
	
	public static DocumentKeyWord[] getAll(){
		return ALL_KEYWORDS;
	}
	
	private static DocumentKeyWord[] createAllKeywords() {
		List<DocumentKeyWord> list = new ArrayList<>();
		list.addAll(Arrays.asList(BashGnuCommandKeyWords.values()));
		list.addAll(Arrays.asList(BashIncludeKeyWords.values()));
		list.addAll(Arrays.asList(BashLanguageKeyWords.values()));
		list.addAll(Arrays.asList(BashSpecialVariableKeyWords.values()));
		list.addAll(Arrays.asList(BashSystemKeyWords.values()));
		
		return list.toArray(new DocumentKeyWord[list.size()]);
	}
}
