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
