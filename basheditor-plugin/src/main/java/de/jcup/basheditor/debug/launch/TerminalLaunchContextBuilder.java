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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TerminalLaunchContextBuilder {

    private InternalTerminalCommandStringBuilder itc = new InternalTerminalCommandStringBuilder();
    private CommandStringToCommandListConverter toListConverter = new CommandStringToCommandListConverter();
    private CommandStringVariableReplaceSupport variableReplaceSupport = new CommandStringVariableReplaceSupport();

    private File file;
    private String terminalCommand;
    private String params;
    
    private boolean waitingAlways;
    private boolean waitingOnErrors;
    private String starterTemplate;
	private Map<String, String> environment;
    private int port;
    private TerminalLaunchContextBuilder() {
        
    }
    public static TerminalLaunchContextBuilder builder() {
        return new TerminalLaunchContextBuilder();
    }
    
    public TerminalLaunchContextBuilder file(File file) {
        this.file = file;
        return this;
    }
    
    public TerminalLaunchContextBuilder terminalCommand(String command) {
        this.terminalCommand=command;
        return this;
    }
    public TerminalLaunchContextBuilder starterCommand(String starterTemplate) {
        this.starterTemplate=starterTemplate;
        return this;
    }
    
    public TerminalLaunchContextBuilder params(String params) {
        this.params=params;
        return this;
    }
    
    public TerminalLaunchContextBuilder port(int port) {
        this.port=port;
        return this;
    }
    
    public TerminalLaunchContextBuilder waitingAlways(boolean waitingAlways) {
        this.waitingAlways=waitingAlways;
        return this;
    }
    
    public TerminalLaunchContextBuilder waitingOnErrors(boolean waitingOnErrors) {
        this.waitingOnErrors=waitingOnErrors;
        return this;
    }

    public TerminalLaunchContextBuilder environment(Map<String, String> environment) {
    	this.environment=environment;
    	return this;
    }
    
    public TerminalLaunchContext build() {
        TerminalLaunchContext context = new TerminalLaunchContext();
        context.file = file;
        context.params = params;
        
        context.startTemplate=starterTemplate;
        context.terminalCommand = terminalCommand;

        context.waitAlways = waitingAlways;
        context.waitOnErrors = waitingOnErrors;
        context.switchToWorkingDirNecessary=true;
        context.launchTerminalCommand="";
        context.commands=new ArrayList<String>();
        context.environment=environment;
        context.port=port;
        if (context.environment==null) {
        	/* fallback */
        	context.environment=new HashMap<String, String>();
        }
        try{
            /* build command list */
            String internalCommand = itc.build(context);
            
            Map<String,String> map = new  HashMap<String,String>();
            map.put(TerminalCommandVariable.BE_CMD_CALL.getId(), internalCommand);
            map.put(TerminalCommandVariable.BE_CMD_TITLE.getId(), "Bash Editor DEBUG Session:"+file.getName());
            
            context.terminalExecutionCommand = variableReplaceSupport.replaceVariables(context.terminalCommand, map);
            map.put(TerminalCommandVariable.BE_TERMINAL.getId(), context.terminalExecutionCommand);
            
            StringBuilder sb = new StringBuilder();
            List<String> list = toListConverter.convert(context.startTemplate);
            for (String command: list) {
                String cmdWithContent = variableReplaceSupport.replaceVariables(command, map);
                context.commands.add(cmdWithContent);
                if (sb.length()>0) {
                    sb.append(' ');
                }
                sb.append(cmdWithContent);
            }
            context.launchTerminalCommand=sb.toString();
            
        }catch(RuntimeException e) {
            context.exception=e;
        }
        return context;
    }
}
