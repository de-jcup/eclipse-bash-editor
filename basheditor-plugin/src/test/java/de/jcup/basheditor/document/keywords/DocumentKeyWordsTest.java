package de.jcup.basheditor.document.keywords;

import static org.junit.Assert.*;

import org.junit.Test;

public class DocumentKeyWordsTest {

	@Test
	public void all_key_words_can_be_initialzed() {
		DocumentKeyWord[] results = DocumentKeyWords.getAll();
		assertNotNull(results);
	}

}
