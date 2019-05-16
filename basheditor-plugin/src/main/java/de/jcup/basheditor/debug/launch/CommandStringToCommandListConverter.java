package de.jcup.basheditor.debug.launch;

import java.util.ArrayList;
import java.util.List;

public class CommandStringToCommandListConverter {

    public List<String> convert(String commandString) {
        List<String> list = new ArrayList<>();
        if (commandString == null) {
            return list;
        }
        String inspect = commandString.trim();
        if (inspect.isEmpty()) {
            return list;
        }
        String[] commands = commandString.split(" ");
        for (String command: commands) {
            if (command==null || command.isEmpty()) {
                continue;
            }
            list.add(command);
        }
        
        return list;
    }
}
