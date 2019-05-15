package de.jcup.basheditor;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class FileExtensionExtractorTest {
    
    private FileExtensionExtractor extractorToTest;
    
    @Before
    public void before() {
        extractorToTest=new FileExtensionExtractor();
    }
    
    @Test
    public void test() {
        assertEquals(".txt",extractorToTest.extractFileExtension(new File("/tmp/file1.txt")));
        assertEquals(".",extractorToTest.extractFileExtension(new File("/tmp/file1.")));
        assertEquals(null,extractorToTest.extractFileExtension(new File("/tmp/file1")));
        assertEquals(".bash",extractorToTest.extractFileExtension(new File("/tmp/file1.bash")));
    }

}
