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
