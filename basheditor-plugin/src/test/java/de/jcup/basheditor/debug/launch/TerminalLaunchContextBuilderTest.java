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

    @Before
    public void before() throws Exception {
        file = Files.createTempFile("xyz", ".txt").toFile();
    }

    @Test
    public void check_default_linux_command_works() throws Exception {
        String command = testProviderAndReturnCommandString(new DefaultLinuxTerminalCommandStringProvider());
        assertNotNull(command);
        assertNotEquals("", command);
    }
    
    @Test
    public void check_default_windows_command_works() throws Exception {
        String command = testProviderAndReturnCommandString(new DefaultWindowsTerminalCommandStringProvider());
        assertNotNull(command);
        assertNotEquals("", command);
    }

    private String testProviderAndReturnCommandString(DefaultTerminalCommandStringProvider defaultProvider) {
        return TerminalLaunchContextBuilder.builder().file(file).terminalCommand(defaultProvider.getTerminalCommandString()).params("-a 1 -b 2").build().commandString;
    }

}
