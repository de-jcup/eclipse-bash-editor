package de.jcup.basheditor;

import java.util.List;

public interface WordListBuilder {

	/**
	 * Build words from source
	 * @param source
	 * @return words from given source. Separators are ,;.!?= If there are single or double quotes
	 * at end or start those are removed. Never <code>null</code>
	 */
	List<String> build(String source);

}