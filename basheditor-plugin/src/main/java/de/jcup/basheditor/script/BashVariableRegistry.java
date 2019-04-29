package de.jcup.basheditor.script;

import java.util.Map;

public interface BashVariableRegistry {

    BashVariable getVariable(String varName);
    
    public Map<String, BashVariable> getVariables() ;

}
