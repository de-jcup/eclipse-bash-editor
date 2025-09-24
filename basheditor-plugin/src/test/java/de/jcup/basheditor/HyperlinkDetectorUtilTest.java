/*
 * Copyright 2020 Albert Tregnaghi
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

import org.junit.Test;

public class HyperlinkDetectorUtilTest {

    @Test
    public void createHoveredTextInfo_no_whitespaces() {
        assertExpected(100, "12345", "678 ", "12345678", 95);
        assertExpected(100, "abcde", "fgh", "abcdefgh", 95);
        assertExpected(100, "abcde", "fg", "abcdefg", 95);
    }

    @Test
    public void createHoveredTextInfo_whitespaces_before() {
        assertExpected(100, "      abcde", "fgh", "abcdefgh", 95);
        assertExpected(10, "  abcde", "fgh", "abcdefgh", 5);
        assertExpected(0, "  abcde", "fgh", "abcdefgh", -5);
        assertExpected(100, " abcde", "fghi", "abcdefghi", 95);
        assertExpected(100, "\nabcde", "fghi", "abcdefghi", 95);

    }

    @Test
    public void createHoveredTextInfo_whitepaces_after() {
        assertExpected(100, "abcde", "fgh ", "abcdefgh", 95);
        assertExpected(100, "abcde", "fgh\n", "abcdefgh", 95);

    }

    @Test
    public void createHoveredTextInfo_special_chars_after_filename_variants() {
        assertExpected(100, "abcde", "fgh.txt|", "abcdefgh", 95, "abcdefgh.txt", 95);
    }

    @Test
    public void createHoveredTextInfo_special_chars_after() {
        assertExpected(100, "abcde", "fgh|", "abcdefgh", 95);

    }

    @Test
    public void createHoveredTextInfo_special_chars_before() {
        assertExpected(100, "|abcde", "fgh|", "abcdefgh", 95);
        assertExpected(100, "(abcde", "fgh)", "abcdefgh", 95);
    }

    @Test
    public void createHoveredTextInfo_special_chars_before_filename_variant() {
        assertExpected(100, "|abcd", "e/folder1/test.txt|", "abcde", 96, "abcde/folder1/test.txt", 96);
        assertExpected(100, "(abcd", "e/folder1/test.txt)", "abcde", 96, "abcde/folder1/test.txt", 96);
    }

    private void assertExpected(int offset, String leftChars, String rightChars, String expectedText, int expectedOffset) {
        assertExpected(offset, leftChars, rightChars, expectedText, expectedOffset, expectedText, expectedOffset);
    }

    private void assertExpected(int offset, String leftChars, String rightChars, String expectedFunctionName, int expectedFunctionOffset, String expectedFilenameText, int expectedFileNameOffset) {
        /* execute */
        HoveredTextInfo info = HyperlinkDetectorUtil.createHoveredTextInfo(leftChars, rightChars, offset);

        /* test */
        assertEquals("function name not as expected", expectedFunctionName, info.functionName);
        assertEquals("file name not as expected", expectedFilenameText, info.fileName);
        assertEquals("function offset failure", expectedFunctionOffset, info.functionOffsetLeft);
        assertEquals("filename offset failure", expectedFileNameOffset, info.filenameOffsetLeft);
    }

}
