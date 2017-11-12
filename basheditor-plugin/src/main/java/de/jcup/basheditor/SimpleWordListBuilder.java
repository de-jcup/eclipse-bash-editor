package de.jcup.basheditor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This builder build words from a given source. 
 * @author albert
 *
 */
public class SimpleWordListBuilder implements WordListBuilder {

	/* (non-Javadoc)
	 * @see de.jcup.basheditor.WordListBuilder#build(java.lang.String)
	 */
	@Override
	public List<String> build(String source) {
		if (source == null  || source.isEmpty()) {
			return Collections.emptyList();
		}
		String[] allWords = source.split("[\\s,;:.!()\\?=]");
		List<String> list = new ArrayList<>();
		for (String word: allWords){
			String transformed = transformIfNecessary(word);
			if (transformed!=null && ! transformed.isEmpty()){
				list.add(transformed);
			}
		}
		return list;
	}

	private String transformIfNecessary(String word) {
		if (word==null) {
			return null;
		}
		if (word.isEmpty()){
			return null;
		}
		
		String transformed=word;
		/* start*/
		if (transformed.startsWith("#")){
			transformed = dropFirstChar(transformed);
		}
		if (transformed.startsWith("'")){
			transformed = dropFirstChar(transformed);
		}
		if (transformed.startsWith("\"")){
			transformed = dropFirstChar(transformed);
		}
		
		/* end */
		if (transformed.endsWith("'")){
			transformed=dropLastChar(transformed);
		}
		if (transformed.endsWith("\"")){
			transformed=dropLastChar(transformed);
		}
		return transformed;
	}

	private String dropLastChar(String transformed) {
		return transformed.substring(0,transformed.length()-1);
	}

	private String dropFirstChar(String transformed) {
		transformed=transformed.substring(1);
		return transformed;
	}
}
