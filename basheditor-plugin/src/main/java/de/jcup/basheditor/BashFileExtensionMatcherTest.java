/*
 * Copyright 2019 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
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
