/*
 * Copyright 2017 Albert Tregnaghi
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
package de.jcup.basheditor.script;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.jcup.basheditor.script.parser.ParseToken;

public class BashScriptModel implements BashVariableRegistry {

    Collection<BashFunction> functions = new ArrayList<>();
    Collection<BashError> errors = new ArrayList<>();
    List<ParseToken> debugTokenList;
    Map<String, BashVariable> variables = new TreeMap<String, BashVariable>();

    public Collection<BashFunction> getFunctions() {
        return functions;
    }

    public Collection<BashError> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !getErrors().isEmpty();
    }

    /**
     * Returns a debug token children - if children is null, a new one will be created
     * 
     * @return debug token children, never <code>null</code>
     */
    public List<ParseToken> getDebugTokens() {
        if (debugTokenList == null) {
            debugTokenList = new ArrayList<>();
        }
        return debugTokenList;
    }

    public boolean hasDebugTokens() {
        return debugTokenList != null;
    }

    /**
     * @return map of variables, scope is root of script, no functions
     */
    public Map<String, BashVariable> getVariables() {
        return variables;
    }

    /**
     * @param varName
     * @return variable or <code>null</code>
     */
    public BashVariable getVariable(String varName) {
        if (varName == null) {
            return null;
        }
        return variables.get(varName);
    }

    /**
     * Finds bash function inside resource by given function name
     * @param name
     * @return  bash function inside resource, or <code>null</code>
     */
    public BashFunction findBashFunctionByName(String name) {
        for (BashFunction function: functions) {
            if (function.getName().contentEquals(name)) {
                return function;
            }
        }
        return null;
    }

}
