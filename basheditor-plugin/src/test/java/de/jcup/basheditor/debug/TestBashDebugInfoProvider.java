package de.jcup.basheditor.debug;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestBashDebugInfoProvider implements BashDebugInfoProvider {

    private String testFolderPth;

    public TestBashDebugInfoProvider() {
        try {
            Path testFolder;
            testFolder = Files.createTempDirectory("test_bash_debug_info");
            testFolderPth = testFolder.toRealPath().toAbsolutePath().toString();
        } catch (IOException e) {
           throw new IllegalStateException("Test corrupt!",e);
        }
    }
    
    @Override
    public String getSystemUserHomePath() {
        return testFolderPth;
    }

    @Override
    public String getDefaultScriptPathToUserHome() {
        return testFolderPth;
    }

    @Override
    public String getResultingScriptPathToUserHome() {
        return "scriptHome";
    }

    
}
