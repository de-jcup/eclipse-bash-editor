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
package de.jcup.basheditor.script;

import java.util.ArrayList;
import java.util.List;

public class BashVariable {

    private String name;
    private String initialValue;
    private List<BashVariableAssignment> assignments = new ArrayList<>();
    private boolean local;

    public BashVariable(String name, BashVariableAssignment assignment) {
        this.name = name;
        assignments.add(assignment);// initial on first pos
    }

    public void setInitialValue(String value) {
        this.initialValue = value;
    }

    public String getInitialValue() {
        return initialValue;
    }

    public BashVariableAssignment getInitialAssignment() {
        return assignments.iterator().next();
    }

    /**
     * Get the assignments in ordered way as defined inside script! So first entry
     * here is also first (inital) assignment
     * 
     * @return
     */
    public List<BashVariableAssignment> getAssignments() {
        return assignments;
    }

    public String getName() {
        return name;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public boolean isLocal() {
        return local;
    }

}
