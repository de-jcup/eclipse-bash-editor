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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import de.jcup.basheditor.BashEditorActivator;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class TerminalLaucherTestExecution {

	private static boolean outsideEclipse;
	private static final String params = "-a 1 -b 2";
	
    public static void main(String[] args) throws Exception {
    	outsideEclipse=true;
        tryToExecuteTemporaryTestBashScript("xxx","yyy");
    }
    public static TerminalLaunchContext simulateCallCommandForTestBashScript(String terminalCommand, String starterCommand) throws IOException {
        try{
            TerminalLaunchContext context = createLauncher().simulate(createTempFile(), params, terminalCommand,starterCommand);
            return context;
        }catch(IOException e) {
            EclipseUtil.logError("Was not able create test file", e, BashEditorActivator.getDefault());
            return null;
        }
        
    }
	public static void tryToExecuteTemporaryTestBashScript(String terminalCommand, String starterCommand) throws IOException {

      

        TerminalLauncher launcher = createLauncher();
        
        launcher.execute(createTempFile(), params,terminalCommand,starterCommand);
	}
	
	private static File createTempFile() throws IOException {
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
        return temp;
	}
	
	private static TerminalLauncher createLauncher() {
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
        return launcher; 
	}
}
