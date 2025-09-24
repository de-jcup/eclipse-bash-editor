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
