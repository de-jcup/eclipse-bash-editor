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
package de.jcup.basheditor.debug.launch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Test;

import de.jcup.basheditor.debug.BashPIDSnippetSupport;

public class TerminalLaunchContextBuilderTest {

    private File file;
    private String path;
	private String userPath;

    @Before
    public void before() throws Exception {
        file = Files.createTempFile("xyz", ".sh").toFile();
        
        path = file.getParentFile().getAbsolutePath();
        path = OSUtil.toUnixPath(path);
        
        userPath = OSUtil.toUnixPath(System.getProperty("user.home"));
    }

    @Test
    public void check_default_linux_command_works() throws Exception {
        /* prepare */
    	/* @formatter:off */
        String expectedTerminalCommand ="x-terminal-emulator -e bash "+
    	                                "--login -c 'cd \""+userPath+"/.basheditor\";./"+
    	                                BashPIDSnippetSupport.FILENAME_STORE_TERMINAL_PIDS_SCRIPT+" 0 $$;cd "+path+";./" + 
        		                        file.getName() + " -a 1 -b 2;_exit_status=$?;echo \"Exit code=$_exit_status\";read -p \"Press enter to continue...\"'";
        /* @formatter:on */

        /* execute */
        TerminalLaunchContext context =  testProviderAndReturnCommandString(new DefaultLinuxTerminalCommandStringProvider());

        /* test */
        assertNotNull(context);
        assertNull(context.exception);
        String terminalCommand = context.terminalExecutionCommand;
        assertEquals(expectedTerminalCommand,terminalCommand);
        
        String launchCommand = context.launchTerminalCommand;
        assertNotNull(launchCommand);
        assertEquals("bash -c "+expectedTerminalCommand+" &", launchCommand);
    }

    @Test
    public void check_default_windows_command_works() throws Exception {
        /* prepare */
        String expectedTerminalCommand = "start \"Bash Editor DEBUG Session:"+file.getName()+"\" cmd.exe /C bash "+
        		                         "--login -c 'cd \""+userPath+"/.basheditor\";./"+
                                         BashPIDSnippetSupport.FILENAME_STORE_TERMINAL_PIDS_SCRIPT+" 0 $$;cd "+path+";./" + 
                                         file.getName() + " -a 1 -b 2;_exit_status=$?;echo \"Exit code=$_exit_status\";read -p \"Press enter to continue...\"'";

        /* execute */
        TerminalLaunchContext context = testProviderAndReturnCommandString(new DefaultWindowsTerminalCommandStringProvider());

        /* test */
        /* test */
        assertNotNull(context);
        assertNull(context.exception);
        String terminalCommand = context.terminalExecutionCommand;
        assertEquals(expectedTerminalCommand,terminalCommand);
        
        String launchCommand = context.launchTerminalCommand;
        assertNotNull(launchCommand);
        assertEquals("cmd.exe /C "+expectedTerminalCommand, launchCommand);
    }

    private TerminalLaunchContext testProviderAndReturnCommandString(DefaultTerminalCommandStringProvider defaultProvider) {
        /* @formatter:off*/
        TerminalLaunchContext build = TerminalLaunchContextBuilder.builder().
                    file(file).
                    terminalCommand(defaultProvider.getTerminalCommandString()).
                    starterCommand(defaultProvider.getStarterCommandString()).
                    params("-a 1 -b 2").
                    waitingAlways(true).
                    build();
        return build;
        /* @formatter:on*/
    }

}
