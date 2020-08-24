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
