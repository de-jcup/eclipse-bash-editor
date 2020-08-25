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
package de.jcup.basheditor.debug;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.jcup.basheditor.debug.launch.OSUtil;

public class BashCallPIDStoreSnippetBuilderTest {

	private BashCallPIDStoreSnippetBuilder builderToTest;
	private String tmpFolder;
	
	@Before
	public void before() {
		builderToTest=new BashCallPIDStoreSnippetBuilder();
		tmpFolder = OSUtil.toUnixPath(System.getProperty("user.home"));
		assertFalse(tmpFolder.endsWith(File.separator));
	}
	
	
	@Test
	public void buildPIDForPortPort1234_returns_path_as_tempfolder_debugger_terminal_pid4port_1234_$username$_txt() throws IOException {
	    /* execute + test */
	    assertEquals(tmpFolder+"/.basheditor/PID_debug-terminal_port_12345.txt", OSUtil.toUnixPath(builderToTest.buildPIDFileAbsolutePath("12345")));
	    
	}
	
	@Test
    public void buildWritePIDToPortSpecificTmpFileSnippet_generateds_expected_parts() throws IOException {
        /* execute + test */
        assertEquals("cd \""+tmpFolder+"/.basheditor\";./"+BashPIDSnippetSupport.FILENAME_STORE_TERMINAL_PIDS_SCRIPT+" 12345 $$;", builderToTest.buildWritePIDToPortSpecificTmpFileSnippet(12345));
    }
	
}
