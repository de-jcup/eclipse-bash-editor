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
