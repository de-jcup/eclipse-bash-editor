package de.jcup.basheditor;

import static java.util.Collections.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class SimpleWordCodeCompletion {

	private SortedSet<String> allWordsCache = new TreeSet<>();
	
	private Set<String> additionalWordsCache = new HashSet<>();

	/**
	 * Reset allWordsCache
	 * 
	 * @return completion
	 */
	public SimpleWordCodeCompletion reset() {
		allWordsCache.clear();
		additionalWordsCache.clear();
		return this;
	}

	/**
	 * Calculates the resulting proposals for given offset.
	 * 
	 * @param source
	 * @param offset
	 * @return proposals, never <code>null</code>
	 */
	public Set<String> calculate(String source, int offset) {
		rebuildCacheIfNecessary(source);
		if (offset == 0) {
			return unmodifiableSet(allWordsCache);
		}
		String wanted = getTextbefore(source, offset);
		return filteredSet(allWordsCache, wanted);
	}

	SortedSet<String> filteredSet(SortedSet<String> set, String wanted) {
		if (wanted==null || wanted.isEmpty()){
			return set;
		}
		TreeSet<String> filtered = new TreeSet<>();
		for (String data: set){
			if (data.startsWith(wanted)){
				filtered.add(data);
			}
		}
		/* remove wanted itself*/
		filtered.remove(wanted);
		return filtered;
	}

	/**
	 * Adds an additional word - will be removed on all of {@link #reset()}
	 * @param word
	 */
	public void add(String word) {
		if (word==null){
			return;
		}
		allWordsCache.clear(); // reset the all words cache so rebuild will be triggered 
		additionalWordsCache.add(word.trim());
	}
	/**
	 * Resolves text before given offset
	 * @param source
	 * @param offset
	 * @return text, never <code>null</code>
	 */
	public String getTextbefore(String source, int offset) {
		if (source == null || source.isEmpty()) {
			return "";
		}
		if (offset <= 0) {
			return "";
		}
		if (offset >= source.length()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		int current = offset-1; //-1 because we want the char before
		boolean ongoing = false;
		do {
			if (current < 0) {
				break;
			}
			char c = source.charAt(current--);
			ongoing = !Character.isWhitespace(c);
			if (ongoing) {
				sb.insert(0,c);
			}
		} while (ongoing);
		
		return sb.toString();
	}

	private void rebuildCacheIfNecessary(String source) {
		if (allWordsCache.isEmpty()) {
			allWordsCache.addAll(additionalWordsCache);
			allWordsCache.addAll(buildAllWords(source));
			// we do not want the empty String
			allWordsCache.remove("");
		}
	}

	private List<String> buildAllWords(String source) {
		if (source == null) {
			return Collections.emptyList();
		}
		String[] allWords = source.split("\\s");
		List<String> list = Arrays.asList(allWords);
		return list;
	}

}
