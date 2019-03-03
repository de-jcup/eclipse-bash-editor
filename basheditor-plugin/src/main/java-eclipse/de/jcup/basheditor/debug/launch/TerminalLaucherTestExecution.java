package de.jcup.basheditor.debug.launch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TerminalLaucherTestExecution {

	private static boolean outsideEclipse;
	
    public static void main(String[] args) throws Exception {
    	outsideEclipse=true;
        tryToExecuteTemporaryTestBashScript();
    }

	public static void tryToExecuteTemporaryTestBashScript() throws IOException {
		TerminalLauncher launcher = new TerminalLauncher() {
            protected void logExectionError(java.io.IOException e) {
            	if (outsideEclipse) {
            		e.printStackTrace();
            	}else {
            		super.logExectionError(e);
            	}
            }

            @Override
            protected void logExecutedCommand(LaunchRunnable runnable) {
            	if (outsideEclipse) {
            		System.out.println(">>> runnable cmd:\n"+runnable.createCommandString());
            	}else {
            		super.logExecutedCommand(runnable);
            	}
            }
            
            @Override
            protected boolean isWaitingAlways() {
            	if (outsideEclipse) {
            		return false;
            	}
                return super.isWaitingAlways();
            }
            
            @Override
            protected boolean isWaitingOnErrors() {
            	if (outsideEclipse) {
            		return true;
            	}
                return super.isWaitingOnErrors();
            }
        };
        
        String params = "-a 1 -b 2";

        // --------------------------------------------------------------------------------------------------------------------------------|123456789
        Path tempFile = Files.createTempFile("terminallaunch", ".sh");
        File temp = tempFile.toFile();
        temp.setExecutable(true, true);

        try (FileWriter fw = new FileWriter(temp); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("#! /bin/bash\n");
            bw.write("echo 'A simple test'\n");
            bw.write("echo $1 $2 $3\n");
            bw.write("exit 1");
        }

        launcher.execute(temp, params,TerminalLauncher.DEFAULT_XTERMINAL_SNIPPET);
	}
}
