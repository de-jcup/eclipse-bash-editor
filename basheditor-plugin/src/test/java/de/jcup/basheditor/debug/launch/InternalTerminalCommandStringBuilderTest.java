package de.jcup.basheditor.debug.launch;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Test;

public class InternalTerminalCommandStringBuilderTest {

    private InternalTerminalCommandStringBuilder toTest;
    private TerminalLaunchContext context;

    @Before
    public void before() {
        toTest = new InternalTerminalCommandStringBuilder();
        context = new TerminalLaunchContext();
    }

    @Test
    public void null_results_in_empty_string() {
        assertEquals("", toTest.build(null));
    }
    
    @Test
    public void context_1() {
        assertEquals("./null;_exit_status=$?;echo \"Exit code=$_exit_status\";", toTest.build(context));
    }
    
    @Test
    public void context_2() {
        /* prepare */
        context.file=null;
        context.terminalCommand="terminalCommand";
        context.switchToWorkingDirNecessary=true;
        context.waitAlways=true;
        context.waitOnErrors=true;
        
        /* test */
        assertEquals("cd null;./null;_exit_status=$?;echo \"Exit code=$_exit_status\";read -p \"Press enter to continue...\"", toTest.build(context));
    }
    
    @Test
    public void context_3() throws IOException {
        /* prepare */
        context.file=Files.createTempFile("test", ".txt").toFile();
        context.terminalCommand="terminalCommand";
        context.switchToWorkingDirNecessary=true;
        context.waitAlways=true;
        context.waitOnErrors=true;
        context.params=null;
       
        String path = context.file.getParentFile().toPath().toRealPath().toAbsolutePath().toString();
        String name = context.file.getName();
        /* test */
        assertEquals("cd "+path+";./"+name+";_exit_status=$?;echo \"Exit code=$_exit_status\";read -p \"Press enter to continue...\"", toTest.build(context));
    }
    
    @Test
    public void context_4() throws IOException {
        /* prepare */
        context.file=Files.createTempFile("test", ".txt").toFile();
        context.terminalCommand="terminalCommand";
        context.switchToWorkingDirNecessary=true;
        context.waitAlways=true;
        context.waitOnErrors=true;
        context.params="-a 1 -b 2";
       
        String path = context.file.getParentFile().toPath().toRealPath().toAbsolutePath().toString();
        String name = context.file.getName();
        /* test */
        assertEquals("cd "+path+";./"+name+" -a 1 -b 2;_exit_status=$?;echo \"Exit code=$_exit_status\";read -p \"Press enter to continue...\"", toTest.build(context));
    }

}
