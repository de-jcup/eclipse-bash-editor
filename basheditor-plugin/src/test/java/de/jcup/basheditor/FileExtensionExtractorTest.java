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
