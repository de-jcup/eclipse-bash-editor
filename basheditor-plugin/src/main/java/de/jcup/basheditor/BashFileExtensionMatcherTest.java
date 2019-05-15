package de.jcup.basheditor;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class BashFileExtensionMatcherTest {

    private BashFileExtensionMatcher matcherToTest;
    @Before
    public void before() {
        matcherToTest = new BashFileExtensionMatcher();
    }
    @Test
    public void test_matching() {
        assertTrue(matcherToTest.isMatching(null));
        assertTrue(matcherToTest.isMatching(""));
        assertTrue(matcherToTest.isMatching("bash"));
        assertTrue(matcherToTest.isMatching("sh"));
        assertTrue(matcherToTest.isMatching(".bash"));
        assertTrue(matcherToTest.isMatching(".sh"));
        assertTrue(matcherToTest.isMatching(".bash",true));
    }
    
    @Test
    public void test_not_matching() {
        assertFalse(matcherToTest.isMatching(".ash"));
        assertFalse(matcherToTest.isMatching("."));
        assertFalse(matcherToTest.isMatching("hash"));
        assertFalse(matcherToTest.isMatching(".bashi"));
        assertFalse(matcherToTest.isMatching(".she"));

        assertFalse(matcherToTest.isMatching(".bash",false));
        assertFalse(matcherToTest.isMatching(".bash",false));
    }

}
