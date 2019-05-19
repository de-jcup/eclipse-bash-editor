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

import java.util.Map;

public class CommandStringVariableReplaceSupport {

    
    public String replaceVariables(String commandLineWithVariables, Map<String,String> mapping) {
        if (mapping==null) {
            return commandLineWithVariables;
        }
        String result = ""+commandLineWithVariables;
        for (String key: mapping.keySet()) {
            String replace = mapping.get(key);
            if (replace==null) {
                continue;
            }
            String search = buildSearchString(key);
            int length = search.length();
            int index = -1;

            while ( (index = result.indexOf(search))!=-1) {
                StringBuilder sb = new StringBuilder();
                sb.append(result.substring(0,index));
                sb.append(replace);
                int indexAfterReplace = index+length;
                if (result.length()>indexAfterReplace) {
                    sb.append(result.substring(indexAfterReplace));
                }
                result = sb.toString();
            };
           
        }
        
        return result;
    }

    private String buildSearchString(String key) {
        return "${"+key+"}";
    }
}
