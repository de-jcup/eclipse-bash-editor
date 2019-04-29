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

import java.util.Map;
import java.util.TreeMap;

public class BashFunction implements BashVariableRegistry  {

	String name;
	Map<String, BashVariable> variables = new TreeMap<String, BashVariable>();
	int position;
	int lengthToNameEnd;
	int end;

	public int getLengthToNameEnd() {
		return lengthToNameEnd;
	}
	
	public String getName() {
		return name;
	}

	public int getPosition() {
		return position;
	}
	
	public int getEnd() {
		return end;
	}
	
	@Override
	public String toString() {
		return "function "+name+"()";
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
}
