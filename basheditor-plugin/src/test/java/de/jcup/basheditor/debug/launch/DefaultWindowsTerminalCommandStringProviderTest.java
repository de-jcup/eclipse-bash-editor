package de.jcup.basheditor.debug.launch;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DefaultWindowsTerminalCommandStringProviderTest {

    private DefaultWindowsTerminalCommandStringProvider providerToTest;

    @Before
    public void before() {
       providerToTest =new DefaultWindowsTerminalCommandStringProvider();
    }
    
    @Test
    public void startCommandAsExpected() {
        assertEquals("cmd.exe /C ${BE_TERMINAL}", providerToTest.getStarterCommandString());
    }
    
    @Test
    public void startTerminalCommandAsExpected() {
        assertEquals("start \"${BE_CMD_TITLE}\" cmd.exe /C bash --login -c '${BE_CMD_CALL}'", providerToTest.getTerminalCommandString());
    }

}
