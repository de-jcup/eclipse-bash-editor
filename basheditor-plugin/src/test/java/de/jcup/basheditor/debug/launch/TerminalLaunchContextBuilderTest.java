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

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Test;

public class TerminalLaunchContextBuilderTest {

    private File file;
    private String path;
    private String user;

    @Before
    public void before() throws Exception {
        user = System.getProperty("user.name");
        
        file = Files.createTempFile("xyz", ".sh").toFile();
        
        path = file.getParentFile().getAbsolutePath();
        path = OSUtil.toUnixPath(path);
    }

    @Test
    public void check_default_linux_command_works() throws Exception {
        /* prepare */
        String expectedTerminalCommand = "x-terminal-emulator -e bash --login -c '_debug_terminal_pid=$$;touch /tmp/basheditor_terminal_pid4port_0_"+user+".txt;echo $_debug_terminal_pid >> /tmp/basheditor_terminal_pid4port_0_"+user+".txt;cd "+path+";./" + file.getName() + " -a 1 -b 2;_exit_status=$?;echo \"Exit code=$_exit_status\";read -p \"Press enter to continue...\"'";

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
        //cmd.exe /c start "my title" cmd.exe /C bash --login -c 'cd /C/Users/atrigna/AppData/Local/Temp;./terminallaunch8349202239915867888.sh -a 1 -b 2;_exit_status=$?;echo "Exit code=$_exit_status";read -p "Press enter to continue..."'
        /* prepare */
        String expectedTerminalCommand = "start \"Bash Editor DEBUG Session:"+file.getName()+"\" cmd.exe /C bash --login -c '_debug_terminal_pid=$$;touch /tmp/basheditor_terminal_pid4port_0_"+user+".txt;echo $_debug_terminal_pid >> /tmp/basheditor_terminal_pid4port_0_"+user+".txt;cd "+path+";./" + file.getName() + " -a 1 -b 2;_exit_status=$?;echo \"Exit code=$_exit_status\";read -p \"Press enter to continue...\"'";

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
